package com.example.contacts

import androidx.lifecycle.ViewModel

class AddContactsViewModel:ViewModel() {
    private var phoneNumberDataList= mutableListOf<String>()
    private var emailDataList= mutableListOf<String>()
    private var addressDataList= mutableListOf<String>()
    fun addItemInPhoneNumberList(newText:String){
        phoneNumberDataList.add(newText)
    }
    fun addItemInEmailList(newText: String){
        emailDataList.add(newText)
    }
    fun addItemInAddressList(newText: String){
        addressDataList.add(newText)
    }
    fun getPhoneNumberDataList():List<String> {
        return phoneNumberDataList
    }
    fun getEmailDataList():List<String> {
        return emailDataList
    }
    fun getAddressDataList():List<String> {
        return addressDataList
    }
    fun removeAllItemsInPhoneNumberDataList(){
       phoneNumberDataList.clear()
    }
    fun removeAllItemsInEmailDataList(){
        emailDataList.clear()
    }
    fun removeAllItemsInAddressDataList(){
        addressDataList.clear()
    }
}