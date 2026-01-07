package apero.quanta.picai.util

import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FileUtils @Inject constructor(
    @param:ApplicationContext
    private val context: Context
) {
    suspend fun uriToFile(uri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val tempFile = File.createTempFile("upload_image", ".jpg", context.cacheDir)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }


        suspend fun downloadImage(url: String): File? = withContext(Dispatchers.IO) {

            try {

                val fileName = "downloaded_image_${System.currentTimeMillis()}.jpg"

                val file = File(context.filesDir, fileName)

                java.net.URL(url).openStream().use { inputStream ->

                    FileOutputStream(file).use { outputStream ->

                        inputStream.copyTo(outputStream)

                    }

                }

                file

            } catch (e: Exception) {

                Log.e("FileUtils", "Error downloading image: ${e.message}", e)

                null

            }

        }

    }

    