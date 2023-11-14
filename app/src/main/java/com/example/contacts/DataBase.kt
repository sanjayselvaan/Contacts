package com.example.contacts

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DataBase(context: Context) : SQLiteOpenHelper(context, contactDataBaseName,null, contactDataBaseVersion) {
    companion object{
        private const val contactDataBaseName="Contact_DataBase"
        private const val contactTableName="Contact"
        private const val phoneTableName="Phone_Number"
        private const val emailTableName="Email"
        private const val addressTableName="Address"
        private const val contactID="contactID"
        private const val phoneNumberId="phoneNumberId"
        private const val emailId="emailId"
        private const val addressId="addressId"
        private const val contactName="Contact_Name"
        private const val contactPhoneNumber="Phone_Number"
        private const val contactEmail="Email"
        private const val contactAddress="Address"
        private const val tableDummy="dummy"
        private const val tableAddressDummy="dummyAddress"
        private const val contactDataBaseVersion=2
    }

    private fun insertInitialValues(db:SQLiteDatabase){
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
            }
            val id=db.insert(contactTableName,null,valuesForContactTable)
            val valuesForPhoneTable=ContentValues().apply {
                put(contactID,id)
                put(contactPhoneNumber,phoneNumber[i])
            }
            db.insert(phoneTableName,null,valuesForPhoneTable)
            val valuesForEmailTable=ContentValues().apply {
                put(contactID,id)
                put(contactEmail,emailAddress[i])
            }
            db.insert(emailTableName,null,valuesForEmailTable)
            val valuesForAddressTable=ContentValues().apply {
                put(contactID,id)
                put(contactAddress,address[i])
            }
            db.insert(addressTableName,null,valuesForAddressTable)
        }
    }

    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("CREATE TABLE $contactTableName($contactID INTEGER primary key autoincrement,$contactName TEXT)")
        database?.execSQL("CREATE TABLE $phoneTableName($phoneNumberId INTEGER primary key autoincrement,$contactID INTEGER,$contactPhoneNumber INTEGER,FOREIGN KEY($contactID) REFERENCES $contactTableName($contactID))")
        database?.execSQL("CREATE TABLE $emailTableName($emailId INTEGER primary key autoincrement,$contactID INTEGER,$contactEmail TEXT,FOREIGN KEY($contactID) REFERENCES $contactTableName($contactID))")
        database?.execSQL("CREATE TABLE $addressTableName($addressId INTEGER primary key autoincrement,$contactID INTEGER,$contactAddress TEXT,FOREIGN KEY($contactID) REFERENCES $contactTableName($contactID))")
        if (database != null) {
            insertInitialValues(database)
        }
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("test1","Migration: inside on upgrade triggered")
        if(oldVersion<2){
            upgradeDataBaseVersion2(database)
        }

    }
    private fun upgradeDataBaseVersion2(database:SQLiteDatabase?){
        Log.d("test1","inside onUpgradeDataBaseVersion2 ")
        database?.execSQL("ALTER TABLE $addressTableName RENAME COLUMN _id TO $contactID")
        database?.execSQL("ALTER TABLE $contactTableName RENAME COLUMN _id TO $contactID")
        database?.execSQL("CREATE TABLE $tableDummy ($contactID INTEGER primary key autoincrement,$contactName TEXT)")
        database?.execSQL("CREATE TABLE $tableAddressDummy($addressId INTEGER primary key autoincrement,$contactID INTEGER,$contactAddress TEXT,FOREIGN KEY($contactID) REFERENCES $contactTableName($contactID))")
        database?.execSQL("CREATE TABLE $phoneTableName($phoneNumberId INTEGER primary key autoincrement,$contactID INTEGER,$contactPhoneNumber INTEGER,FOREIGN KEY($contactID) REFERENCES $contactTableName($contactID))")
        database?.execSQL("CREATE TABLE $emailTableName($emailId INTEGER primary key autoincrement,$contactID INTEGER,$contactEmail TEXT,FOREIGN KEY($contactID) REFERENCES $contactTableName($contactID))")
        val cursor=database?.query(contactTableName,null,null,null,null,null,null)
        cursor?.use {
            while (cursor.moveToNext()){
                val id = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.contactID))
                val name=if(cursor.getString(cursor.getColumnIndexOrThrow(contactName))!=null) cursor.getString(cursor.getColumnIndexOrThrow(contactName)) else null
                val contentValuesForContactTable=ContentValues().apply {
                    put(contactID,id)
                    put(contactName,name)
                }
                database.insert(tableDummy,null,contentValuesForContactTable)
                val phoneNumber =
                    if (cursor.getString(cursor.getColumnIndexOrThrow(contactPhoneNumber)) != null) cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            contactPhoneNumber
                        )
                    ).split(", ") else null
                val email =
                    if (cursor.getString(cursor.getColumnIndexOrThrow(contactEmail)) != null) cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            contactEmail
                        )
                    ).split(", ") else null
                phoneNumber?.let {
                    it.forEach {
                        val contentValues=ContentValues().apply {
                            put(contactID,id)
                            put(contactPhoneNumber,it)
                        }
                        database.insert(phoneTableName,null,contentValues)
                    }
                }
                email?.let {
                    it.forEach {
                        val contentValues=ContentValues().apply {
                            put(contactID,id)
                            put(contactEmail,it)
                        }
                        database.insert(emailTableName,null,contentValues)
                    }
                }
                val cursorForAddress = database.query(addressTableName, arrayOf(contactAddress),"$contactID = ?",
                    arrayOf(id.toString()),null,null,null)
                cursorForAddress?.use {
                    while (cursorForAddress.moveToNext()){
                        val address=cursorForAddress.getString(cursorForAddress.getColumnIndexOrThrow(
                            contactAddress))
                        val contentValues=ContentValues().apply {
                            put(contactID,id)
                            put(contactAddress,address)
                        }
                        database.insert(tableAddressDummy,null,contentValues)
                    }
                }
            }
        }
        database?.execSQL("DROP TABLE $contactTableName")
        database?.execSQL("DROP TABLE $addressTableName")
        database?.execSQL("ALTER TABLE $tableAddressDummy RENAME TO $addressTableName")
        database?.execSQL("ALTER TABLE $tableDummy RENAME TO $contactTableName")
        val contentValuesForDummy=ContentValues().apply {
            put(contactName,"Dummy")
        }
        database?.insert(contactTableName,null,contentValuesForDummy)
        Log.d("test1","finish of the migration")
    }

}