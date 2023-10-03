package com.example.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.example.contacts.databinding.ActivityContactDetailsBinding
import com.example.contacts.databinding.ExtraTextViewBinding

class ShowContactDetails : AppCompatActivity() {
    private lateinit var binding: ActivityContactDetailsBinding
    private lateinit var backPressed: OnBackPressedCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.contact_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getIntExtra(MainActivity.positionOfDataItem, 0)
        val contactItem=DataBase.getContact(id)
        val contactName = contactItem?.contactName
        val contactNumber = contactItem?.contactPhoneNumber
        val contactEmail = contactItem?.contactEmail
        val contactAddress = contactItem?.contactAddress
        contactName?.let {
            binding.nameLayout.visibility=View.VISIBLE
            binding.nameTextView.text = it
        }
        contactNumber?.let {
            if (it.isNotEmpty()) {
                binding.phoneNumberLayout.visibility = View.VISIBLE
                it.forEach {  text->
                    val extraTextViewBinding =ExtraTextViewBinding.inflate(layoutInflater,binding.contactPhoneNumberLinearLayout,false)
                    extraTextViewBinding.extraTextView.text = text
                    binding.contactPhoneNumberLinearLayout.addView(extraTextViewBinding.root)
                }
            }
        }
        contactEmail?.let {
            if (it.isNotEmpty()) {
                binding.emailLayout.visibility = View.VISIBLE
                it.forEach{text->
                    val extraTextViewBinding =ExtraTextViewBinding.inflate(layoutInflater,binding.contactEmailLinearLayout,false)
                    extraTextViewBinding.extraTextView.text = text
                    binding.contactEmailLinearLayout.addView(extraTextViewBinding.root)
                }
            }
        }
        contactAddress?.let {
            if (it.isNotEmpty()) {
                binding.addressLayout.visibility = View.VISIBLE
                it.forEach{text->
                    val extraTextViewBinding =
                        ExtraTextViewBinding.inflate(layoutInflater,binding.contactAddressLinearLayout,false)
                    extraTextViewBinding.extraTextView.text = text
                    binding.contactAddressLinearLayout.addView(extraTextViewBinding.root)
                }
            }
        }
        backPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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