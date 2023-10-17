package com.example.contacts

import android.net.Uri

object DataBaseContract {
    const val AUTHORITY="com.example.contacts.provider"
    const val TABLE_CONTACT_NAME="Contact"
    const val TABLE_ADDRESS_NAME="Address"
    val CONTENT_URI_FOR_TABLE_CONTACT: Uri = Uri.parse("content://${AUTHORITY}/${TABLE_CONTACT_NAME}")
    val CONTENT_URI_FOR_TABLE_ADDRESS: Uri = Uri.parse("content://${AUTHORITY}/${TABLE_ADDRESS_NAME}")
}