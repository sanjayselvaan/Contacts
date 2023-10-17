package com.example.contacts

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DataBase(private val context: Context) : SQLiteOpenHelper(context,"DATABASE",null,1) {
    companion object{
        const val contactTableName="Contact"
        const val contactAddressTableName="Address"
        const val contactID="_id"
        const val contactName="Contact_Name"
        const val contactPhoneNumberList="Phone_Number"
        const val contactEmailList="Email"
        const val contactAddress="Address"
    }

    fun getContactsList():List<Contact>{
        val contactList= mutableListOf<Contact>()
        val cursor=context.contentResolver.query(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_CONTACT,null,null,null,null)
        cursor?.use {cursor1->
            while (cursor1.moveToNext()){
                val id=cursor1.getLong(cursor1.getColumnIndexOrThrow(contactID))
                val name=cursor1.getString(cursor1.getColumnIndexOrThrow(contactName))
                val phoneNumber=if(cursor1.getString(cursor1.getColumnIndexOrThrow(contactPhoneNumberList))!=null) cursor1.getString(cursor1.getColumnIndexOrThrow(
                    contactPhoneNumberList)).split(", ") else null
                val email=if(cursor1.getString(cursor1.getColumnIndexOrThrow(contactEmailList))!=null) cursor1.getString(cursor1.getColumnIndexOrThrow(
                    contactEmailList)).split(", ") else null
                val cursorForAddressTable=context.contentResolver.query(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_ADDRESS,null,"$contactID = ?",
                    arrayOf(id.toString()),null)
                var address: MutableList<String>? = cursorForAddressTable?.use {
                    mutableListOf<String>().apply {
                        while (cursorForAddressTable.moveToNext()) {
                            cursorForAddressTable.getString(cursorForAddressTable.getColumnIndexOrThrow(contactAddress))?.let {
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

        val cursorForContactTable=context.contentResolver.query(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_CONTACT,null,"$contactID= ?", arrayOf(id.toString()),null)
        cursorForContactTable?.use {cursorForContactTable.moveToFirst()
            val name=cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(contactName))?:null
            val phoneNumber=if(cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(contactPhoneNumberList))!=null) cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(contactPhoneNumberList)).split(", ") else null
            val email=if(cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(
                    contactEmailList))!=null) cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(
                contactEmailList)).split(", ") else null
            val cursorForAddressTable=context.contentResolver.query(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_ADDRESS,null,"$contactID= ?",
                arrayOf(id.toString()),null)
            var address: MutableList<String>? = cursorForAddressTable?.use {
                mutableListOf<String>().apply {
                    while (cursorForAddressTable.moveToNext()) {
                        cursorForAddressTable.getString(cursorForAddressTable.getColumnIndexOrThrow(contactAddress))?.let {
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
        val valuesForContactTable=ContentValues().apply {
            put(contactName,name)
            put(contactPhoneNumberList,phoneNumberList?.joinToString(", "))
            put(contactEmailList,emailList?.joinToString(", "))
        }
        val insertedUri=context.contentResolver.insert(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_CONTACT,valuesForContactTable)
        val insertedID=insertedUri?.lastPathSegment?.toLongOrNull()?:-1
        if(insertedID!=-1L){
            addressList?.forEach {
                val valuesForAddressTable = ContentValues().apply {
                    put(contactID, insertedID)
                    put(contactAddress, it)
                }
                context.contentResolver.insert(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_ADDRESS,valuesForAddressTable)
            }
        }
        return insertedID
    }

    fun deleteContact(id:Long){
        context.contentResolver.delete(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_CONTACT,"$contactID= ?",
            arrayOf(id.toString())
        )
        context.contentResolver.delete(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_ADDRESS,"$contactID= ?",
            arrayOf(id.toString())
        )
    }
    fun updateContactName(id:Long,newName:String){
        val contentValues=ContentValues().apply {
            put(contactName,newName)
        }
        context.contentResolver.update(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_CONTACT,contentValues,"$contactID= ?",
            arrayOf(id.toString())
        )
    }
    fun updateContactPhoneNumberList(id:Long,newPhoneNumberList:List<String>){
        val contentValues=ContentValues().apply {
            put(contactPhoneNumberList,newPhoneNumberList.joinToString(", "))
        }
        context.contentResolver.update(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_CONTACT,contentValues,"$contactID= ?",
            arrayOf(id.toString())
        )
    }
    fun updateContactEmailList(id:Long,newEmailList:List<String>){
        val contentValues=ContentValues().apply {
            put(contactEmailList,newEmailList.joinToString(", "))
        }
        context.contentResolver.update(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_CONTACT,contentValues,"$contactID= ?",
            arrayOf(id.toString())
        )
    }
    fun updateContactAddressList(id:Long,newAddressList:List<String>){
        context.contentResolver.delete(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_ADDRESS,"$contactID= ?",
            arrayOf(id.toString())
        )
        newAddressList.forEach {
            val contentValues=ContentValues().apply {
                put(contactID,id)
                put(contactAddress,it)
            }
            context.contentResolver.insert(ContentProviderForContacts.CONTENT_URI_FOR_TABLE_ADDRESS,contentValues)
        }
    }
    fun searchContact(searchQuery:String): List<Contact> {
        val contactList= mutableListOf<Contact>()
        val dataBase=readableDatabase
        val cursor=dataBase.rawQuery("SELECT DISTINCT c.$contactID FROM $contactTableName c LEFT JOIN $contactAddressTableName a ON c.$contactID = a.$contactID WHERE c.$contactName LIKE ? OR c.$contactPhoneNumberList LIKE ? OR c.$contactEmailList LIKE ? OR a.$contactAddress LIKE ?",
            arrayOf("%$searchQuery%","%$searchQuery%","%$searchQuery%","%$searchQuery"))
        cursor?.use {
            while (cursor.moveToNext()){
                getContact(cursor.getLong(cursor.getColumnIndexOrThrow(contactID)))?.let {contact->
                    contactList.add(contact)
                }
            }
        }
        return contactList
    }

    private fun insertInitialValues(){
        val name = mutableListOf(
            "Aaaaa",
            "Bbbbbb",
            "Ccccc",
            "Dddd",
            "Eeee"
        )//,"F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
        val phoneNumber = mutableListOf(
            listOf("1"),
            listOf("2"),
            listOf("3"),
            listOf("4"),
            listOf("5")
        )//,PhoneNumber(6),PhoneNumber(7),PhoneNumber(8),PhoneNumber(9),PhoneNumber(10),PhoneNumber(11),PhoneNumber(12),PhoneNumber(13),PhoneNumber(14),PhoneNumber(15),PhoneNumber(16),PhoneNumber(17),PhoneNumber(18),PhoneNumber(19),PhoneNumber(20),PhoneNumber(21),PhoneNumber(22),PhoneNumber(23),PhoneNumber(24),PhoneNumber(25),PhoneNumber(26))
        val emailAddress = mutableListOf(
            listOf("A@email.com"),
            listOf("B@email.com"),
            listOf("C@email.com"),
            listOf("D@email.com"),
            listOf("E@email.com")
        )//,(Email("F@email.com"),(Email("G@email.com"),(Email("H@email.com"),(Email("I@email.com"),(Email("J@email.com"),(Email("K@email.com"),(Email("L@email.com"),(Email("M@email.com"),(Email("P@email.com"),(Email("O@email.com"),(Email("P@email.com"),(Email("Q@email.com"),(Email("R@email.com"),(Email("S@email.com"),(Email("T@email.com"),(Email("U@email.com"),(Email("V@email.com"),(Email("W@email.com"),(Email("X@email.com"),(Email("Y@email.com"),(Email("Z@email.com"))
        val address = mutableListOf(
            listOf("A-homeAddress"),
            listOf("B-homeAddress"),
            listOf("C-homeAddress"),
            listOf("D-homeAddress"),
            listOf("E-homeAddress")
        )//,Address("-homeAddress"),Address("G-homeAddress"),Address("H-homeAddress"),Address("I-homeAddress"),Address("J-homeAddress"),Address("K-homeAddress"),Address("L-homeAddress"),Address("M-homeAddress"),Address("N-homeAddress"),Address("O-homeAddress"),Address("P-homeAddress"),Address("Q-homeAddress"),Address("R-homeAddress"),Address("S-homeAddress"),Address("T-homeAddress"),Address("U-homeAddress"),Address("V-homeAddress"),Address("W-homeAddress"),Address("X-homeAddress"),Address("Y-homeAddress"),Address("Z-homeAddress"))
        for (i in name.indices) {
            addContact(name[i],phoneNumber[i],emailAddress[i],address[i])
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

    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("CREATE TABLE $contactTableName($contactID INTEGER primary key autoincrement,$contactName TEXT,$contactPhoneNumberList TEXT,$contactEmailList TEXT)")
        database?.execSQL("CREATE TABLE $contactAddressTableName($contactID INTEGER,$contactAddress TEXT,FOREIGN KEY($contactID) REFERENCES $contactTableName($contactID))")
        if (database != null) {
            insertInitialValues()
        }
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        database?.execSQL("DROP TABLE IF EXISTS $contactTableName")
        database?.execSQL("DROP TABLE IF EXISTS $contactAddressTableName")
        onCreate(database)
    }


}