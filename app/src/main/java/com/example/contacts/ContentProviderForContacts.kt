package com.example.contacts

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri


class ContentProviderForContacts: ContentProvider() {
    companion object{
        private val URI_MATCHER=UriMatcher(UriMatcher.NO_MATCH)
        private const val TABLE_CONTACT=1
        private const val TABLE_PHONE=2
        private const val TABLE_EMAIL=3
        private const val TABLE_ADDRESS=4
        private const val TABLE_JOINT=5
        init {
            URI_MATCHER.addURI(DataBaseContract.AUTHORITY, DataBaseContract.TABLE_CONTACT_NAME, TABLE_CONTACT)
            URI_MATCHER.addURI(DataBaseContract.AUTHORITY, DataBaseContract.TABLE_PHONE_NAME, TABLE_PHONE)
            URI_MATCHER.addURI(DataBaseContract.AUTHORITY, DataBaseContract.TABLE_EMAIL_NAME, TABLE_EMAIL)
            URI_MATCHER.addURI(DataBaseContract.AUTHORITY, DataBaseContract.TABLE_ADDRESS_NAME, TABLE_ADDRESS)
            URI_MATCHER.addURI(DataBaseContract.AUTHORITY,DataBaseContract.TABLE_JOINT, TABLE_JOINT)
        }
    }

    private lateinit var dataBaseHelper: DataBase
    override fun onCreate(): Boolean {
        context?.let {
            dataBaseHelper=DataBase(it)
            return true
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val dataBase= dataBaseHelper.readableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
               dataBase.query(DataBaseContract.TABLE_CONTACT_NAME,projection,selection,selectionArgs,null,null,sortOrder)
            }
            TABLE_PHONE->{
                dataBase.query(DataBaseContract.TABLE_PHONE_NAME,projection,selection,selectionArgs,null,null,sortOrder)
            }
            TABLE_EMAIL->{
                dataBase.query(DataBaseContract.TABLE_EMAIL_NAME,projection,selection,selectionArgs,null,null,sortOrder)
            }
            TABLE_ADDRESS->{
                dataBase.query(DataBaseContract.TABLE_ADDRESS_NAME,projection,selection,selectionArgs,null,null,sortOrder)
            }
            TABLE_JOINT->{
                selectionArgs?.let {
                    dataBase.rawQuery("SELECT DISTINCT ${DataBaseContract.contactID} FROM (SELECT DISTINCT ${DataBaseContract.contactID} FROM ${DataBaseContract.TABLE_CONTACT_NAME} WHERE ${DataBaseContract.contactName} LIKE ? UNION SELECT DISTINCT ${DataBaseContract.contactID} FROM ${DataBaseContract.TABLE_PHONE_NAME} WHERE ${DataBaseContract.contactPhoneNumber} LIKE ? UNION SELECT DISTINCT ${DataBaseContract.contactID} FROM ${DataBaseContract.TABLE_EMAIL_NAME} WHERE ${DataBaseContract.contactEmail} LIKE ? UNION SELECT DISTINCT ${DataBaseContract.contactID} FROM ${DataBaseContract.TABLE_ADDRESS_NAME} WHERE ${DataBaseContract.contactAddress} LIKE ? )",Array(4){"%${selectionArgs[0]}%"})
                }
            }
            else->{
                throw IllegalArgumentException("Invalid uri $uri")
            }
        }
    }

    override fun getType(uri: Uri): String {
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->"vnd.android.cursor.dir/vnd.example.Contact"
            TABLE_PHONE->"vnd.android.cursor.dir/vnd.example.Phone_Number"
            TABLE_EMAIL->"vnd.android.cursor.dir/vnd.example.Email"
            TABLE_ADDRESS->"vnd.android.cursor.dir/vnd.example.Address"
            TABLE_JOINT->"vnd.android.cursor.dir/vnd.example.Joint"
            else->throw IllegalArgumentException("Invalid uri $uri")
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {

        val dataBase = dataBaseHelper.writableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                val id=dataBase.insert(DataBaseContract.TABLE_CONTACT_NAME,null,contentValues)
                Uri.withAppendedPath(uri,id.toString())
            }
            TABLE_PHONE->{
                val id=dataBase.insert(DataBaseContract.TABLE_PHONE_NAME,null,contentValues)
                Uri.withAppendedPath(uri,id.toString())
            }
            TABLE_EMAIL->{
                val id=dataBase.insert(DataBaseContract.TABLE_EMAIL_NAME,null,contentValues)
                Uri.withAppendedPath(uri,id.toString())
            }
            TABLE_ADDRESS->{
                val id=dataBase.insert(DataBaseContract.TABLE_ADDRESS_NAME,null,contentValues)
                Uri.withAppendedPath(uri,id.toString())
            }
            else->{
                throw IllegalArgumentException("Invalid uri $uri")
            }
        }


    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val dataBase = dataBaseHelper.writableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                dataBase.delete(DataBaseContract.TABLE_CONTACT_NAME,selection,selectionArgs)
            }
            TABLE_PHONE->{
                dataBase.delete(DataBaseContract.TABLE_PHONE_NAME,selection,selectionArgs)
            }
            TABLE_EMAIL->{
                dataBase.delete(DataBaseContract.TABLE_EMAIL_NAME,selection,selectionArgs)
            }
            TABLE_ADDRESS->{
                dataBase.delete(DataBaseContract.TABLE_ADDRESS_NAME,selection,selectionArgs)

            }
            else->{
                throw IllegalArgumentException("Invalid uri $uri")
            }
        }
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val dataBase = dataBaseHelper.writableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                dataBase.update(DataBaseContract.TABLE_CONTACT_NAME,contentValues,selection,selectionArgs)
            }
            TABLE_PHONE->{
                dataBase.update(DataBaseContract.TABLE_PHONE_NAME,contentValues,selection,selectionArgs)
            }
            TABLE_EMAIL->{
                dataBase.update(DataBaseContract.TABLE_EMAIL_NAME,contentValues,selection,selectionArgs)
            }
            TABLE_ADDRESS->{
                dataBase.update(DataBaseContract.TABLE_ADDRESS_NAME,contentValues,selection,selectionArgs)
            }
            else->{
                throw IllegalArgumentException("Invalid uri $uri")
            }
        }

    }

}