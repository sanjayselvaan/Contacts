package com.example.contacts

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class ContentProviderForContacts: ContentProvider() {
    companion object{
        val CONTENT_URI_FOR_TABLE_CONTACT: Uri =Uri.parse("content://${DataBaseContract.AUTHORITY}/${DataBaseContract.TABLE_CONTACT_NAME}")
        val CONTENT_URI_FOR_TABLE_ADDRESS: Uri =Uri.parse("content://${DataBaseContract.AUTHORITY}/${DataBaseContract.TABLE_ADDRESS_NAME}")
        private val URI_MATCHER=UriMatcher(UriMatcher.NO_MATCH)
        private const val TABLE_CONTACT=1
        private const val TABLE_ADDRESS=2
        init {
            URI_MATCHER.addURI(DataBaseContract.AUTHORITY, DataBaseContract.TABLE_CONTACT_NAME, TABLE_CONTACT)
            URI_MATCHER.addURI(DataBaseContract.AUTHORITY, DataBaseContract.TABLE_ADDRESS_NAME, TABLE_ADDRESS)
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
        val dataBase=dataBaseHelper.readableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                val cursor=dataBase.query(DataBaseContract.TABLE_CONTACT_NAME,projection,selection,selectionArgs,null,null,sortOrder)
                cursor?.let {
                    it.setNotificationUri(context?.contentResolver,uri)
                    it
                }
            }
            TABLE_ADDRESS->{
                val cursor=dataBase.query(DataBaseContract.TABLE_ADDRESS_NAME,projection,selection,selectionArgs,null,null,sortOrder)
                cursor?.let {
                    it.setNotificationUri(context?.contentResolver,uri)
                    it
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
            TABLE_ADDRESS->"vnd.android.cursor.dir/vnd.example.Address"
            else->throw IllegalArgumentException("Invalid uri $uri")
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {

        val dataBase=dataBaseHelper.writableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                val id=dataBase.insert(DataBaseContract.TABLE_CONTACT_NAME,null,contentValues)
                context?.contentResolver?.notifyChange(uri,null)
                Uri.withAppendedPath(uri,id.toString())
            }
            TABLE_ADDRESS->{
                val id=dataBase.insert(DataBaseContract.TABLE_ADDRESS_NAME,null,contentValues)
                context?.contentResolver?.notifyChange(uri,null)
                Uri.withAppendedPath(uri,id.toString())
            }
            else->{
                throw IllegalArgumentException("Invalid uri $uri")
            }
        }


    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val dataBase=dataBaseHelper.writableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                val count=dataBase.delete(DataBaseContract.TABLE_CONTACT_NAME,selection,selectionArgs)
                context?.contentResolver?.notifyChange(uri,null)
                count

            }
            TABLE_ADDRESS->{
                val count=dataBase.delete(DataBaseContract.TABLE_ADDRESS_NAME,selection,selectionArgs)
                context?.contentResolver?.notifyChange(uri,null)
                count

            }
            else->{
                throw IllegalArgumentException("Invalid uri $uri")
            }
        }
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val dataBase=dataBaseHelper.writableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                val count=dataBase.update(DataBaseContract.TABLE_CONTACT_NAME,contentValues,selection,selectionArgs)
                context?.contentResolver?.notifyChange(uri,null)
                count
            }
            TABLE_ADDRESS->{
                val count=dataBase.update(DataBaseContract.TABLE_ADDRESS_NAME,contentValues,selection,selectionArgs)
                context?.contentResolver?.notifyChange(uri,null)
                count
            }
            else->{
                throw IllegalArgumentException("Invalid uri $uri")
            }
        }

    }


}