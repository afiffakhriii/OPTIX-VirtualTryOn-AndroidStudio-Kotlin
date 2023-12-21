package com.afif.optix.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object BitmapUtils {

    fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val filesDir = context.filesDir
        val imageFile = File(filesDir, "profile_image.png")

        try {
            FileOutputStream(imageFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                imageFile
            )
        } else {
            Uri.fromFile(imageFile)
        }
    }
}
