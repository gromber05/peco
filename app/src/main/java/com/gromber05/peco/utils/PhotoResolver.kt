package com.gromber05.peco.utils

import android.content.ContentResolver
import android.net.Uri

fun uriToBytes(contentResolver: ContentResolver, uri: Uri): ByteArray {
    return contentResolver.openInputStream(uri)!!.use { it.readBytes() }
}