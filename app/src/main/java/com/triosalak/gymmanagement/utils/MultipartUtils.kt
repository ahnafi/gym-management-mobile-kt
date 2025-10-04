package com.triosalak.gymmanagement.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object MultipartUtils {

    /**
     * Membuat MultipartBody.Part dari Uri
     *
     * @param context Context untuk akses ContentResolver
     * @param fieldName Nama field sesuai API (contoh: "profile_image")
     * @param fileName Nama file yang dikirim ke server (contoh: "avatar.jpg")
     * @param uri Uri file (gambar, pdf, dll.)
     */
    fun createPartFromUri(
        context: Context,
        fieldName: String,
        fileName: String,
        uri: Uri
    ): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

        val inputStream = contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes()
        inputStream?.close()

        val requestBody = fileBytes?.toRequestBody(mimeType.toMediaTypeOrNull())
        return requestBody?.let {
            MultipartBody.Part.createFormData(
                fieldName,
                fileName,
                it
            )
        }
    }

    /**
     * Membuat RequestBody dari String (untuk form-data biasa).
     */
    fun createPartFromString(value: String): okhttp3.RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}
