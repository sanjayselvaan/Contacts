package com.example.contacts

import android.content.ContentResolver
import android.content.ContentValues
import android.util.Log



class DataBaseHelper(private val contentResolver: ContentResolver){
    fun getContactsList():List<Contact>{
        val contactList= mutableListOf<Contact>()
        val cursor=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_CONTACT,null,null,null,null)
        Log.d("test1","line after assignment statement")
        cursor?.use {cursor1->
            while (cursor1.moveToNext()){
                Log.d("test1","line before crash")
                val id=cursor1.getLong(cursor1.getColumnIndexOrThrow(DataBaseContract.contactID))
                val name=cursor1.getString(cursor1.getColumnIndexOrThrow(DataBaseContract.contactName))
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
        return contactList.sortedBy { getDisplayName(it) }
    }

     fun getContact(id:Long):Contact?{

        val cursorForContactTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_CONTACT,null,"${DataBaseContract.contactID}= ?", arrayOf(id.toString()),null)
        cursorForContactTable?.use {
            cursorForContactTable.moveToFirst()
            val name=cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(
                DataBaseContract.contactName
            ))?:null
            val cursorForPhoneTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_PHONE,null,"${DataBaseContract.contactID}= ?",
                arrayOf(id.toString()),null)
            var phoneNumber: MutableList<String>? = cursorForPhoneTable?.use {
                mutableListOf<String>().apply {
                    while (cursorForPhoneTable.moveToNext()) {
                        cursorForPhoneTable.getString(cursorForPhoneTable.getColumnIndexOrThrow(
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
            val cursorForEmailTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_EMAIL,null,"${DataBaseContract.contactID}= ?",
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
            val cursorForAddressTable=contentResolver.query(DataBaseContract.CONTENT_URI_FOR_TABLE_ADDRESS,null,"${DataBaseContract.contactID}= ?",
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
            return Contact(id,name,phoneNumber,email,address)
        }
        return null
    }


    fun addContact(name:String?,phoneNumberList:List<String>?,emailList:List<String>?,addressList:List<String>?): Long {
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
        return insertedID
    }

    fun deleteContact(id:Long){
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
     fun updateContactName(id:Long,newName:String){
        val contentValues= ContentValues().apply {
            put(DataBaseContract.contactName,newName)
        }
        contentResolver.update(DataBaseContract.CONTENT_URI_FOR_TABLE_CONTACT,contentValues,"${DataBaseContract.contactID}= ?",
            arrayOf(id.toString())
        )
    }
     fun updateContactPhoneNumberList(id:Long,newPhoneNumberList:List<String>){
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
     fun updateContactEmailList(id:Long,newEmailList:List<String>){
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
     fun updateContactAddressList(id:Long,newAddressList:List<String>){
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
     fun searchContact(searchQuery:String): List<Contact> {
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
        return contactList
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
