package com.example.contacts

import androidx.lifecycle.ViewModel

class AddContactsViewModel:ViewModel() {
    private var phoneNumberDataList= mutableListOf<String>()
    private var phoneNumberValidityList= mutableListOf<Boolean>()
    private var emailDataList= mutableListOf<String>()
    private var emailValidityList= mutableListOf<Boolean>()
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
    fun addItemInPhoneNumberValidityList(isValid:Boolean){
        phoneNumberValidityList.add(isValid)
    }
    fun addItemInEmailValidityList(isValid:Boolean){
        emailValidityList.add(isValid)
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
    fun getPhoneNumberValidityList(): List<Boolean> {
        return phoneNumberValidityList
    }
    fun getEmailValidityList(): MutableList<Boolean> {
        return emailValidityList
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
    fun removeAllItemsInPhoneNumberValidityList(){
        phoneNumberValidityList.clear()
    }
    fun removeAllItemsInEmailValidityList(){
        emailValidityList.clear()
    }

}