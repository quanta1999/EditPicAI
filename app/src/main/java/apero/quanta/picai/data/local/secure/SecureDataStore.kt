package apero.quanta.picai.data.local.secure

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureDataStore private constructor(
    context: Context,
    private val name: String
) {
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SEPARATOR = "]"
        private const val GCM_TAG_LENGTH = 128

        private val instances = mutableMapOf<String, SecureDataStore>()

        /**
         * Get or create a SecureDataStore instance
         */
        @Synchronized
        fun getInstance(context: Context, name: String): SecureDataStore {
            return instances.getOrPut(name) {
                SecureDataStore(context.applicationContext, name)
            }
        }
    }

    // Create DataStore instance using property delegation
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = name)
    private val dataStore = context.dataStore

    private val keyAlias = "secure_datastore_key_$name"

    // Lazy initialization to avoid crashes during construction
    private var secretKey: SecretKey? = null
    private var encryptionEnabled = true

    /**
     * Get or create a secret key in Android Keystore with proper error handling
     */
    private fun getOrCreateSecretKey(): SecretKey? {
        // Return cached key if available
        secretKey?.let { return it }

        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

            // Return existing key if available
            keyStore.getKey(keyAlias, null)?.let {
                secretKey = it as SecretKey
                return secretKey
            }

            // Generate new key with proper error handling
            return generateKey()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get or create secret key")
            handleKeystoreFailure(e)
            return null
        }
    }

    /**
     * Generate a new encryption key in Android Keystore
     */
    private fun generateKey(): SecretKey? {
        return try {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false) // Don't require lock screen
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            val key = keyGenerator.generateKey()
            secretKey = key
            key
        } catch (e: Exception) {
            Timber.e(e, "Key generation failed")
            handleKeystoreFailure(e)
            null
        }
    }

    /**
     * Handle keystore failures gracefully
     */
    private fun handleKeystoreFailure(e: Exception) {
        Timber.w(e, "Keystore operation failed, attempting recovery")

        try {
            // Attempt to delete corrupted key and retry
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (keyStore.containsAlias(keyAlias)) {
                keyStore.deleteEntry(keyAlias)
                Timber.d("Deleted corrupted key, attempting regeneration")

                // Try one more time after deletion
                secretKey = generateKey()
                if (secretKey != null) {
                    Timber.d("Successfully regenerated key after deletion")
                    return
                }
            }
        } catch (recoveryException: Exception) {
            Timber.e(recoveryException, "Recovery attempt failed")
        }

        // If all recovery attempts fail, disable encryption
        encryptionEnabled = false
        Timber.w("Encryption disabled due to keystore failure - data will be stored unencrypted")
    }

    /**
     * Encrypt data using AES-GCM
     * Falls back to unencrypted storage if encryption is disabled
     */
    private fun encrypt(plaintext: String): String {
        if (!encryptionEnabled) {
            Timber.w("Encryption disabled, storing data unencrypted")
            return plaintext
        }

        return try {
            val key = getOrCreateSecretKey()
            if (key == null) {
                Timber.w("Secret key unavailable, storing data unencrypted")
                encryptionEnabled = false
                return plaintext
            }

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

            // Combine IV and encrypted data: [IV_BASE64]ENCRYPTED_BASE64
            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

            "$ivBase64$IV_SEPARATOR$encryptedBase64"
        } catch (e: Exception) {
            Timber.e(e, "Encryption failed, storing data unencrypted")
            encryptionEnabled = false
            plaintext
        }
    }

    /**
     * Decrypt data using AES-GCM
     * Handles both encrypted and unencrypted data
     */
    private fun decrypt(encryptedData: String): String {
        if (!encryptionEnabled) {
            // Data is stored unencrypted
            return encryptedData
        }

        // Check if data is in encrypted format
        val parts = encryptedData.split(IV_SEPARATOR)
        if (parts.size != 2) {
            // Data is likely unencrypted or in old format
            Timber.d("Data not in encrypted format, returning as-is")
            return encryptedData
        }

        return try {
            val key = getOrCreateSecretKey()
            if (key == null) {
                Timber.w("Secret key unavailable for decryption, returning data as-is")
                return encryptedData
            }

            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e, "Decryption failed, returning data as-is")
            // Return original data if decryption fails
            encryptedData
        }
    }

    /**
     * Save a string value asynchronously
     */
    suspend fun putString(key: String, value: String) {
        val encryptedValue = encrypt(value)
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[prefKey] = encryptedValue
        }
    }

    /**
     * Save a string value synchronously (for backward compatibility)
     * Note: Should be avoided in production - use suspend version instead
     */
    fun putStringBlocking(key: String, value: String) {
        runBlocking {
            putString(key, value)
        }
    }

    /**
     * Get a string value as Flow (async stream)
     */
    fun getStringFlow(key: String, defaultValue: String): Flow<String> {
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[prefKey]?.let { encryptedValue ->
                try {
                    decrypt(encryptedValue)
                } catch (e: Exception) {
                    // If decryption fails, return default value
                    defaultValue
                }
            } ?: defaultValue
        }
    }

    /**
     * Get a string value asynchronously
     */
    suspend fun getString(key: String, defaultValue: String): String {
        return getStringFlow(key, defaultValue).first()
    }

    /**
     * Get a string value synchronously (for backward compatibility)
     * Note: Should be avoided in production - use suspend version instead
     */
    fun getStringBlocking(key: String, defaultValue: String): String {
        return runBlocking {
            getString(key, defaultValue)
        }
    }

    /**
     * Check if a key exists
     */
    suspend fun contains(key: String): Boolean {
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.map { it.contains(prefKey) }.first()
    }

    /**
     * Check if a key exists synchronously
     */
    fun containsBlocking(key: String): Boolean {
        return runBlocking {
            contains(key)
        }
    }

    /**
     * Remove a specific key
     */
    suspend fun remove(key: String) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences.remove(prefKey)
        }
    }

    /**
     * Remove a specific key synchronously
     */
    fun removeBlocking(key: String) {
        runBlocking {
            remove(key)
        }
    }

    /**
     * Clear all data
     */
    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Clear all data synchronously
     */
    fun clearBlocking() {
        runBlocking {
            clear()
        }
    }

    /**
     * Save a long value asynchronously
     */
    suspend fun putLong(key: String, value: Long) {
        putString(key, value.toString())
    }

    /**
     * Get a long value asynchronously
     */
    suspend fun getLong(key: String, defaultValue: Long): Long {
        return getString(key, defaultValue.toString()).toLongOrNull() ?: defaultValue
    }

    /**
     * Get a long value synchronously
     */
    fun getLongBlocking(key: String, defaultValue: Long): Long {
        return runBlocking {
            getLong(key, defaultValue)
        }
    }

    /**
     * Save a long value synchronously
     */
    fun putLongBlocking(key: String, value: Long) {
        runBlocking {
            putLong(key, value)
        }
    }
}