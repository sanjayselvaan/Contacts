package com.example.contacts

import android.net.Uri

object DataBaseContract {
    const val AUTHORITY="com.example.contacts.provider"
    const val TABLE_CONTACT_NAME="Contact"
    const val TABLE_PHONE_NAME="Phone_Number"
    const val TABLE_EMAIL_NAME="Email"
    const val TABLE_ADDRESS_NAME="Address"
    const val TABLE_JOINT="Joint"
    val CONTENT_URI_FOR_TABLE_CONTACT: Uri = Uri.parse("content://$AUTHORITY/$TABLE_CONTACT_NAME")
    val CONTENT_URI_FOR_TABLE_PHONE:Uri=Uri.parse("content://$AUTHORITY/$TABLE_PHONE_NAME")
    val CONTENT_URI_FOR_TABLE_EMAIL:Uri=Uri.parse("content://$AUTHORITY/$TABLE_EMAIL_NAME")
    val CONTENT_URI_FOR_TABLE_ADDRESS: Uri = Uri.parse("content://$AUTHORITY/$TABLE_ADDRESS_NAME")
    val CONTENT_URI_FOR_JOINT_OF_ALL_TABLES:Uri= Uri.parse("content://$AUTHORITY/$TABLE_JOINT")
    const val contactID="contactID"
    const val contactName="Contact_Name"
    const val contactPhoneNumber="Phone_Number"
    const val contactEmail="Email"
    const val contactAddress="Address"
}