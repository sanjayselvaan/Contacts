package com.example.contacts

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DataBase(context: Context) : SQLiteOpenHelper(context,"DATABASE",null,1) {
    companion object{
        const val contactTableName="Contact"
        const val contactAddressTableName="Address"
        const val contactID="_id"
        const val contactName="Contact_Name"
        const val contactPhoneNumberList="Phone_Number"
        const val contactEmailList="Email"
        const val contactAddress="Address"
    }

//    fun getContactsList():List<Contact> = contactsList.sortedBy{
//        getDisplayName(it)
//    }

    fun getContactsList():List<Contact>{
        val contactList= mutableListOf<Contact>()
        val db=readableDatabase
        val cursor=db.rawQuery("SELECT * FROM $contactTableName",null)
        while (cursor.moveToNext()){
            val id=cursor.getLong(cursor.getColumnIndexOrThrow(contactID))
            val name=cursor.getString(cursor.getColumnIndexOrThrow(contactName))
            val phoneNumber=if(cursor.getString(cursor.getColumnIndexOrThrow(contactPhoneNumberList))!=null) cursor.getString(cursor.getColumnIndexOrThrow(
                contactPhoneNumberList)).split(", ") else null
            val email=if(cursor.getString(cursor.getColumnIndexOrThrow(contactEmailList))!=null) cursor.getString(cursor.getColumnIndexOrThrow(
                contactEmailList)).split(", ") else null
            val address= mutableListOf<String>()
            val cursorForAddressTable=db.rawQuery("SELECT * FROM $contactAddressTableName WHERE $contactID = $id",null)
            while (cursorForAddressTable.moveToNext()){
                address.add(cursorForAddressTable.getString(cursorForAddressTable.getColumnIndexOrThrow(
                    contactAddress)))
            }
            contactList.add(Contact(id,name,phoneNumber,email,address))
            cursorForAddressTable.close()
        }
        db.close()
        cursor.close()
        return contactList.sortedBy { getDisplayName(it) }
    }

//    fun getContact(id:Int): Contact? {
//        contactsList.forEach {
//            if (it.contactID==id){
//                return it
//            }
//        }
//        return null
//    }

    fun getContact(id:Long):Contact?{
        val db=readableDatabase
        val cursorForContactTable=db.rawQuery("SELECT * FROM $contactTableName WHERE $contactID =$id",null)
        cursorForContactTable.moveToFirst()
        val name=cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(contactName))?:null
        val phoneNumber=if(cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(contactPhoneNumberList))!=null) cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(
            contactPhoneNumberList)).split(", ") else null
        val email=if(cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(
                contactEmailList))!=null) cursorForContactTable.getString(cursorForContactTable.getColumnIndexOrThrow(
            contactEmailList)).split(", ") else null
        cursorForContactTable.close()
        val address= mutableListOf<String>()
        val cursorForAddressTable=db.rawQuery("SELECT * FROM $contactAddressTableName WHERE $contactID = $id",null)
        while (cursorForAddressTable.moveToNext()){
            address.add(cursorForAddressTable.getString(cursorForAddressTable.getColumnIndexOrThrow(contactAddress)))
        }
        db.close()
        cursorForAddressTable.close()
        return Contact(id,name,phoneNumber,email,address)
    }



//    fun addContact(contact: Contact) {
//        contactsList.add(contact)
//        idLatest++
//    }
//    fun getLatestAvailableId()= idLatest

    fun addContact(name:String?,phoneNumberList:List<String>?,emailList:List<String>?,addressList:List<String>?): Long {
        val db=writableDatabase
        val valuesForContactTable=ContentValues().apply {
            put(contactName,name)
            put(contactPhoneNumberList,phoneNumberList?.joinToString(", "))
            put(contactEmailList,emailList?.joinToString(", "))
        }
        val id=db.insert(contactTableName, null,valuesForContactTable)
        addressList?.forEach {
            val valuesForAddressTable=ContentValues().apply {
                put(contactID,id)
                put(contactAddress,it)
            }
            db.insert(contactAddressTableName,null,valuesForAddressTable)
        }
        db.close()
        return id
    }

    fun deleteContact(id:Long){
        val db=writableDatabase
        db.delete(contactTableName,"$contactID = $id",null)
        db.delete(contactAddressTableName,"$contactID = $id",null)
    }

    private fun insertInitialValues(db: SQLiteDatabase){
        val name = mutableListOf(
            "Aaaaa",
            "Bbbbbb",
            "Ccccc",
            "Dddd",
            "Eeee"
        )//,"F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
        val phoneNumber = mutableListOf(
            "1",
            "2",
            "3",
            "4",
            "5"
        )//,PhoneNumber(6),PhoneNumber(7),PhoneNumber(8),PhoneNumber(9),PhoneNumber(10),PhoneNumber(11),PhoneNumber(12),PhoneNumber(13),PhoneNumber(14),PhoneNumber(15),PhoneNumber(16),PhoneNumber(17),PhoneNumber(18),PhoneNumber(19),PhoneNumber(20),PhoneNumber(21),PhoneNumber(22),PhoneNumber(23),PhoneNumber(24),PhoneNumber(25),PhoneNumber(26))
        val emailAddress = mutableListOf(
            "A@email.com",
            "B@email.com",
            "C@email.com",
            "D@email.com",
            "E@email.com"
        )//,(Email("F@email.com"),(Email("G@email.com"),(Email("H@email.com"),(Email("I@email.com"),(Email("J@email.com"),(Email("K@email.com"),(Email("L@email.com"),(Email("M@email.com"),(Email("P@email.com"),(Email("O@email.com"),(Email("P@email.com"),(Email("Q@email.com"),(Email("R@email.com"),(Email("S@email.com"),(Email("T@email.com"),(Email("U@email.com"),(Email("V@email.com"),(Email("W@email.com"),(Email("X@email.com"),(Email("Y@email.com"),(Email("Z@email.com"))
        val address = mutableListOf(
            "A-homeAddress",
            "B-homeAddress",
            "C-homeAddress",
            "D-homeAddress",
            "E-homeAddress"
        )//,Address("-homeAddress"),Address("G-homeAddress"),Address("H-homeAddress"),Address("I-homeAddress"),Address("J-homeAddress"),Address("K-homeAddress"),Address("L-homeAddress"),Address("M-homeAddress"),Address("N-homeAddress"),Address("O-homeAddress"),Address("P-homeAddress"),Address("Q-homeAddress"),Address("R-homeAddress"),Address("S-homeAddress"),Address("T-homeAddress"),Address("U-homeAddress"),Address("V-homeAddress"),Address("W-homeAddress"),Address("X-homeAddress"),Address("Y-homeAddress"),Address("Z-homeAddress"))
        for (i in name.indices) {
            val valuesForContactTable=ContentValues().apply {
                put(contactName,name[i])
                put(contactPhoneNumberList,phoneNumber[i])
                put(contactEmailList,emailAddress[i])
            }
            val id=db.insert(contactTableName,null,valuesForContactTable)
            val valuesForAddressTable=ContentValues().apply {
                put(contactID,id)
                put(contactAddress,address[i])
            }
            db.insert(contactAddressTableName,null,valuesForAddressTable)
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

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("CREATE TABLE $contactTableName($contactID INTEGER primary key autoincrement,$contactName text,$contactPhoneNumberList text,$contactEmailList text)")
        p0?.execSQL("CREATE TABLE $contactAddressTableName($contactID INTEGER,$contactAddress text,FOREIGN KEY($contactID) REFERENCES $contactTableName($contactID))")
        if (p0 != null) {
            insertInitialValues(p0)
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $contactTableName")
        p0?.execSQL("DROP TABLE IF EXISTS $contactAddressTableName")
        onCreate(p0)
    }


}