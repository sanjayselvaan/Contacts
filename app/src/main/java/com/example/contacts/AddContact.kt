package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import com.example.contacts.databinding.ActivityAddContactBinding

class AddContact : AppCompatActivity() {
    private lateinit var backPressed: OnBackPressedCallback
    private lateinit var binding: ActivityAddContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_contact)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        backPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        binding.saveButton.setOnClickListener {
            saveButtonOnClickAction()
        }
        binding.cancelButton.setOnClickListener {
            finish()
        }
    }
    private fun saveButtonOnClickAction(){
        val intent = Intent(this, MainActivity::class.java)
        val name = binding.nameEditText.text
        val phoneNumber = binding.phoneNumberEditText.text
        val email = binding.emailEditText.text
        val address = binding.addressEditText.text
        val newId=DataBase.getContactListSize()
        val newContactName = when {
            !name.isNullOrEmpty() -> {
                name.toString()
            }

            !phoneNumber.isNullOrEmpty() -> {
                phoneNumber.toString()
            }

            !email.isNullOrEmpty() -> {
                email.toString()
            }

            else -> {
                getString(R.string.unknown_name)
            }
        }
        val newContactEmail = if (!email.isNullOrEmpty()) email.toString() else null
        val newContactPhoneNumber = if (!phoneNumber.isNullOrEmpty()) phoneNumber.toString() else null
        val newContactAddress = if (!address.isNullOrEmpty()) address.toString() else null
        when {
            newContactName != getString(R.string.unknown_name)&&newContactPhoneNumber.isNullOrEmpty() && newContactEmail.isNullOrEmpty() && newContactAddress.isNullOrEmpty() -> {
                DataBase.addContact(
                    Contact(newId,
                        Name(newContactName),
                        null,
                        null,
                        null
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }

            newContactPhoneNumber.isNullOrEmpty() && newContactEmail.isNullOrEmpty() && newContactAddress != null -> {
                DataBase.addContact(
                    Contact(newId,
                        Name(newContactName),
                        null,
                        null,
                        Address(newContactAddress)
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }

            newContactPhoneNumber.isNullOrEmpty() && newContactEmail != null && newContactAddress.isNullOrEmpty() -> {
                DataBase.addContact(
                    Contact(newId,
                        Name(newContactName),
                        null,
                        Email(newContactEmail),
                        null
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }

            newContactPhoneNumber.isNullOrEmpty() && newContactEmail != null && newContactAddress != null -> {
                DataBase.addContact(
                    Contact(newId,
                        Name(newContactName),
                        null,
                        Email(newContactEmail),
                        Address(newContactAddress)
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }

            newContactPhoneNumber != null && newContactEmail.isNullOrEmpty() && newContactAddress.isNullOrEmpty() -> {
                DataBase.addContact(
                    Contact(newId,
                        Name(newContactName),
                        PhoneNumber(newContactPhoneNumber),
                        null,
                        null
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }

            newContactPhoneNumber != null && newContactEmail.isNullOrEmpty() && newContactAddress != null -> {
                DataBase.addContact(
                    Contact(newId,
                        Name(newContactName),
                        null,
                        null,
                        Address(newContactAddress)
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }

            newContactPhoneNumber != null && newContactEmail != null && newContactAddress.isNullOrEmpty() -> {
                DataBase.addContact(
                    Contact(newId,
                        Name(newContactName),
                        PhoneNumber(newContactPhoneNumber),
                        Email(newContactEmail),
                        null
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }

            newContactPhoneNumber != null && newContactEmail != null && newContactAddress != null -> {
                DataBase.addContact(
                    Contact(newId,
                        Name(newContactName),
                        PhoneNumber(newContactPhoneNumber),
                        Email(newContactEmail),
                        Address(newContactAddress)
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                backPressed.handleOnBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }
}
