package com.example.contacts

import android.content.ContentResolver
import android.content.ContentValues
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



class DataBaseHelper(private val contentResolver: ContentResolver){
    suspend fun getContactsList():List<Contact> = withContext(Dispatchers.IO){
        val contactList= mutableListOf<Contact>()
        val cursor=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_CONTACT,null,null,null,null)
        cursor?.use {cursor1->
            while (cursor1.moveToNext()){
                val id=cursor1.getLong(cursor1.getColumnIndexOrThrow(DataBaseContract.contactID))
                val name=cursor1.getString(cursor1.getColumnIndexOrThrow(DataBaseContract.contactName))
                val phone=cursor1.getString(cursor1.getColumnIndexOrThrow(DataBaseContract.contactPhoneNumber))
                Log.d("questForAnswer","in getContactList id=$id")
                Log.d("questForAnswer","in getContactList name=$name")
                Log.d("questForAnswer","in getContactList phone=$phone")
                val emailTrial=cursor1.getString(cursor1.getColumnIndexOrThrow(DataBaseContract.contactEmail))
                Log.d("questForAnswer","in getContactList email=$emailTrial")
                val columnNames=cursor1.columnNames
                columnNames.forEach {
                    Log.d("questForAnswer","getContactList cursor column names = $it")
                }

                val cursorForPhoneNumberTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_PHONE,null,"${DataBaseContract.contactID} = ?",
                    arrayOf(id.toString()),null)
                var phoneNumber: MutableList<String>? = cursorForPhoneNumberTable?.use {
                    mutableListOf<String>().apply {
                        while (cursorForPhoneNumberTable.moveToNext()) {
                            cursorForPhoneNumberTable.getString(cursorForPhoneNumberTable.getColumnIndexOrThrow(
                                DataBaseContract.contactPhoneNumber
                            ))?.let {
                                add(it)
                            }
                        }
                    }
                }
                phoneNumber?.let {
                    if (it.isEmpty()){
                        phoneNumber=null
                    }
                }
                val cursorForEmailTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_EMAIL,null,"${DataBaseContract.contactID} = ?",
                    arrayOf(id.toString()),null)
                var email: MutableList<String>? = cursorForEmailTable?.use {
                    mutableListOf<String>().apply {
                        while (cursorForEmailTable.moveToNext()) {
                            cursorForEmailTable.getString(cursorForEmailTable.getColumnIndexOrThrow(
                                DataBaseContract.contactEmail
                            ))?.let {
                                add(it)
                            }
                        }
                    }
                }
                email?.let {
                    if (it.isEmpty()){
                        email=null
                    }
                }
                val cursorForAddressTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_ADDRESS,null,"${DataBaseContract.contactID} = ?",
                    arrayOf(id.toString()),null)
                var address: MutableList<String>? = cursorForAddressTable?.use {
                    mutableListOf<String>().apply {
                        while (cursorForAddressTable.moveToNext()) {
                            cursorForAddressTable.getString(cursorForAddressTable.getColumnIndexOrThrow(
                                DataBaseContract.contactAddress
                            ))?.let {
                                add(it)
                            }
                        }
                    }
                }
                address?.let {
                    if (it.isEmpty()){
                        address=null
                    }
                }
                contactList.add(Contact(id,name,phoneNumber,email,address))
            }

        }
        return@withContext contactList.sortedBy { getDisplayName(it)}
    }

     suspend fun getContact(id:Long):Contact?= withContext(Dispatchers.IO){
             val cursorForContactTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_CONTACT,null,"${DataBaseContract.contactID}= ?", arrayOf(id.toString()),null)
         cursorForContactTable?.use { cursor ->
             cursorForContactTable.moveToFirst()
             val name=cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(DataBaseContract.contactName))?:null
             val columnNames=cursor.columnNames
             columnNames.forEach {columnName->
                 Log.d("questForAnswer","getContact cursor column names = $columnName")
             }
             val cursorForPhoneTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_PHONE,null,"${DataBaseContract.contactID}= ?", arrayOf(id.toString()),null)
             var phoneNumber: MutableList<String>? = cursorForPhoneTable?.use { mutableListOf<String>().apply {
                 while (cursorForPhoneTable.moveToNext()) {
                     cursorForPhoneTable.getString(cursorForPhoneTable.getColumnIndexOrThrow(DataBaseContract.contactPhoneNumber))?.let {
                         add(it)
                     }
                 }
             }
             }
             phoneNumber?.let {
                 if (it.isEmpty()){
                     phoneNumber=null
                 }
             }
             val cursorForEmailTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_EMAIL,null,"${DataBaseContract.contactID}= ?", arrayOf(id.toString()),null)
             var email: MutableList<String>? = cursorForEmailTable?.use { mutableListOf<String>().apply {
                 while (cursorForEmailTable.moveToNext()) {
                     cursorForEmailTable.getString(cursorForEmailTable.getColumnIndexOrThrow(DataBaseContract.contactEmail))?.let {
                         add(it)
                     }
                 }
             }
             }
             email?.let {
                 if (it.isEmpty()){
                     email=null
                 }
             }
             val cursorForAddressTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_ADDRESS,null,"${DataBaseContract.contactID}= ?", arrayOf(id.toString()),null)
             var address: MutableList<String>? = cursorForAddressTable?.use { mutableListOf<String>().apply {
                 while (cursorForAddressTable.moveToNext()) {
                     cursorForAddressTable.getString(cursorForAddressTable.getColumnIndexOrThrow(DataBaseContract.contactAddress))?.let {
                         add(it)
                     }
                 }
             }
             }
             address?.let {
                 if (it.isEmpty()){
                     address=null
                 }
             }
             return@withContext Contact(id,name,phoneNumber,email,address)
         }
             ?: return@withContext null
         }



    suspend fun addContact(name:String?,phoneNumberList:List<String>?,emailList:List<String>?,addressList:List<String>?): Long =
        withContext(Dispatchers.IO){
            val valuesForContactTable= ContentValues().apply {
                put(DataBaseContract.contactName,name)
            }
            val insertedUri=contentResolver.insert(DataBaseContract.CONTENT_URI_FOR_TABLE_CONTACT,valuesForContactTable)
            val insertedID=insertedUri?.lastPathSegment?.toLongOrNull()?:-1
            if(insertedID!=-1L){
                phoneNumberList?.forEach {
                    val valuesForPhoneNumberTable=ContentValues().apply {
                        put(DataBaseContract.contactID,insertedID)
                        put(DataBaseContract.contactPhoneNumber,it)
                    }
                    contentResolver.insert(DataBaseContract.CONTENT_URI_FOR_TABLE_PHONE,valuesForPhoneNumberTable)
                }
                emailList?.forEach {
                    val valuesForEmailTable=ContentValues().apply {
                        put(DataBaseContract.contactID,insertedID)
                        put(DataBaseContract.contactEmail,it)
                    }
                    contentResolver.insert(DataBaseContract.CONTENT_URI_FOR_TABLE_EMAIL,valuesForEmailTable)
                }
                addressList?.forEach {
                    val valuesForAddressTable = ContentValues().apply {
                        put(DataBaseContract.contactID, insertedID)
                        put(DataBaseContract.contactAddress, it)
                    }
                    contentResolver.insert(DataBaseContract.CONTENT_URI_FOR_TABLE_ADDRESS,valuesForAddressTable)
                }
            }
            return@withContext insertedID
        }

    suspend fun deleteContact(id:Long)= withContext(Dispatchers.IO){
        contentResolver.delete(DataBaseContract.CONTENT_URI_FOR_TABLE_CONTACT,"${DataBaseContract.contactID} = ?",
            arrayOf(id.toString())
        )
        contentResolver.delete(DataBaseContract.CONTENT_URI_FOR_TABLE_PHONE,"${DataBaseContract.contactID} = ?",
            arrayOf(id.toString())
        )
        contentResolver.delete(DataBaseContract.CONTENT_URI_FOR_TABLE_EMAIL,"${DataBaseContract.contactID} = ?",
            arrayOf(id.toString())
        )
        contentResolver.delete(DataBaseContract.CONTENT_URI_FOR_TABLE_ADDRESS,"${DataBaseContract.contactID} = ?",
            arrayOf(id.toString())
        )
    }
     suspend fun updateContactName(id:Long,newName:String)= withContext(Dispatchers.IO){
        val contentValues= ContentValues().apply {
            put(DataBaseContract.contactName,newName)
        }
        contentResolver.update(DataBaseContract.CONTENT_URI_FOR_TABLE_CONTACT,contentValues,"${DataBaseContract.contactID}= ?",
            arrayOf(id.toString())
        )
    }
     suspend fun updateContactPhoneNumberList(id:Long,newPhoneNumberList:List<String>)= withContext(Dispatchers.IO){
        contentResolver.delete(DataBaseContract.CONTENT_URI_FOR_TABLE_EMAIL,"${DataBaseContract.contactID}= ?",
            arrayOf(id.toString())
        )
        newPhoneNumberList.forEach {
            val contentValues= ContentValues().apply {
                put(DataBaseContract.contactID,id)
                put(DataBaseContract.contactPhoneNumber,it)
            }
            contentResolver.insert(DataBaseContract.CONTENT_URI_FOR_TABLE_PHONE,contentValues)
        }
    }
     suspend fun updateContactEmailList(id:Long,newEmailList:List<String>)= withContext(Dispatchers.IO){
        contentResolver.delete(DataBaseContract.CONTENT_URI_FOR_TABLE_EMAIL,"${DataBaseContract.contactID}= ?",
            arrayOf(id.toString())
        )
        newEmailList.forEach {
            val contentValues= ContentValues().apply {
                put(DataBaseContract.contactID,id)
                put(DataBaseContract.contactEmail,it)
            }
            contentResolver.insert(DataBaseContract.CONTENT_URI_FOR_TABLE_EMAIL,contentValues)
        }
    }
     suspend fun updateContactAddressList(id:Long,newAddressList:List<String>)= withContext(Dispatchers.IO){
        contentResolver.delete(DataBaseContract.CONTENT_URI_FOR_TABLE_ADDRESS,"${DataBaseContract.contactID}= ?",
            arrayOf(id.toString())
        )
        newAddressList.forEach {
            val contentValues= ContentValues().apply {
                put(DataBaseContract.contactID,id)
                put(DataBaseContract.contactAddress,it)
            }
            contentResolver.insert(DataBaseContract.CONTENT_URI_FOR_TABLE_ADDRESS,contentValues)
        }
    }
    suspend fun searchContact(searchQuery:String): List<Contact>{
        return withContext(Dispatchers.IO){
            val contactList= mutableListOf<Contact>()
            val cursor = contentResolver.query(DataBaseContract.CONTENT_URI_FOR_JOINT_OF_ALL_TABLES,null,null,arrayOf(searchQuery),null)
            cursor?.use {
                while (it.moveToNext()){
                    Log.d("test1",it.getString(it.getColumnIndexOrThrow(DataBaseContract.contactID)))
                    getContact(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.contactID)))?.let { contact ->
                        contactList.add(contact)
                    }
                }
            }
            contactList
        }
    }
    private fun getDisplayName(contact:Contact): String {
        return when{
            contact.contactName!=null-> contact.contactName
            contact.contactPhoneNumber!=null-> contact.contactPhoneNumber.first()
            contact.contactEmail!=null-> contact.contactEmail.first()
            else->""
        }
    }
}
