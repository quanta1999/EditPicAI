package apero.quanta.picai.util

/**
 * Created by QuanTA on 07/01/2026.
 */

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File


fun shareImage(imagePath: String, context: Context) {

        val file = File(imagePath)

        if (!file.exists()) return


        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {

            type = "image/*"

            putExtra(Intent.EXTRA_STREAM, uri)

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }

        val chooser = Intent.createChooser(intent, "Share Image").apply {

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        }
        context.startActivity(chooser)

    }
