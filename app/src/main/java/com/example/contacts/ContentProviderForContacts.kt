package com.example.contacts

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log

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
            Log.d("test1","the thread in content provider on create : ${Thread.currentThread().name}")
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
        Log.d("test1","the thread in on content provider query is : ${Thread.currentThread().name}")
        val dataBase= dataBaseHelper.readableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                val cursor=dataBase.query(DataBaseContract.TABLE_CONTACT_NAME,projection,selection,selectionArgs,null,null,sortOrder)
                val cursorFirst=dataBase.query(DataBaseContract.TABLE_PHONE_NAME,null,null,null,null,null,null)
                cursorFirst.moveToFirst()
                val string=cursorFirst.getLong(cursorFirst.getColumnIndexOrThrow(DataBaseContract.contactID))
                Log.d("questForAnswer","the column is available in query itself = $string")
                cursorFirst.close()
                cursor?.let {
                    Log.d("questForAnswer","cursor being $$$ prepared from CONTENT RESOLVER")
                    it.setNotificationUri(context?.contentResolver,uri)
                    Log.d("test1","cursor being returned from CONTENT RESOLVER")
                    it
                }
            }
            TABLE_PHONE->{
                val cursor=dataBase.query(DataBaseContract.TABLE_PHONE_NAME,projection,selection,selectionArgs,null,null,sortOrder)
                cursor?.let {
                    it.setNotificationUri(context?.contentResolver,uri)
                    it
                }

            }
            TABLE_EMAIL->{
                val cursor=dataBase.query(DataBaseContract.TABLE_EMAIL_NAME,projection,selection,selectionArgs,null,null,sortOrder)
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
            TABLE_JOINT->{
                selectionArgs?.let {
                     val cursor= dataBase.rawQuery("SELECT DISTINCT ${DataBaseContract.contactID} FROM (SELECT DISTINCT ${DataBaseContract.contactID} FROM ${DataBaseContract.TABLE_CONTACT_NAME} WHERE ${DataBaseContract.contactName} LIKE ? UNION SELECT DISTINCT ${DataBaseContract.contactID} FROM ${DataBaseContract.TABLE_PHONE_NAME} WHERE ${DataBaseContract.contactPhoneNumber} LIKE ? UNION SELECT DISTINCT ${DataBaseContract.contactID} FROM ${DataBaseContract.TABLE_EMAIL_NAME} WHERE ${DataBaseContract.contactEmail} LIKE ? UNION SELECT DISTINCT ${DataBaseContract.contactID} FROM ${DataBaseContract.TABLE_ADDRESS_NAME} WHERE ${DataBaseContract.contactAddress} LIKE ? )",Array(4){"%${selectionArgs[0]}%"})
                    cursor?.let {
                        it.setNotificationUri(context?.contentResolver,uri)
                        it
                    }
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
                context?.contentResolver?.notifyChange(uri,null)
                Uri.withAppendedPath(uri,id.toString())
            }
            TABLE_PHONE->{
                val id=dataBase.insert(DataBaseContract.TABLE_PHONE_NAME,null,contentValues)
                context?.contentResolver?.notifyChange(uri,null)
                Uri.withAppendedPath(uri,id.toString())
            }
            TABLE_EMAIL->{
                val id=dataBase.insert(DataBaseContract.TABLE_EMAIL_NAME,null,contentValues)
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
        val dataBase = dataBaseHelper.writableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                val count=dataBase.delete(DataBaseContract.TABLE_CONTACT_NAME,selection,selectionArgs)
                context?.contentResolver?.notifyChange(uri,null)
                count

            }
            TABLE_PHONE->{
                val count=dataBase.delete(DataBaseContract.TABLE_PHONE_NAME,selection,selectionArgs)
                context?.contentResolver?.notifyChange(uri,null)
                count
            }
            TABLE_EMAIL->{
                val count=dataBase.delete(DataBaseContract.TABLE_EMAIL_NAME,selection,selectionArgs)
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
        val dataBase = dataBaseHelper.writableDatabase
        return when(URI_MATCHER.match(uri)){
            TABLE_CONTACT->{
                val count=dataBase.update(DataBaseContract.TABLE_CONTACT_NAME,contentValues,selection,selectionArgs)
                context?.contentResolver?.notifyChange(uri,null)
                count
            }
            TABLE_PHONE->{
                val count=dataBase.update(DataBaseContract.TABLE_PHONE_NAME,contentValues,selection,selectionArgs)
                context?.contentResolver?.notifyChange(uri,null)
                count
            }
            TABLE_EMAIL->{
                val count=dataBase.update(DataBaseContract.TABLE_EMAIL_NAME,contentValues,selection,selectionArgs)
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