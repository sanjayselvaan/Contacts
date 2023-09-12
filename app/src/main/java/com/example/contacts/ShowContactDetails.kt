package com.example.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.example.contacts.databinding.ActivityContactDetailsBinding

class ShowContactDetails : AppCompatActivity() {
    private lateinit var binding: ActivityContactDetailsBinding
    private lateinit var backPressed: OnBackPressedCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.contact_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val position = intent.getIntExtra(ContactListFragment.positionOfDataItem, 0)
        val contactName = DataBase.getContactName(position)
        val contactNumber = DataBase.getContactNumber(position)
        val contactEmail = DataBase.getContactEmail(position)
        val contactAddress = DataBase.getContactAddress(position)
        binding.nameTextView.text = contactName.name
        contactNumber?.let {
            if (it.phoneNumber.isNotEmpty()) {
                binding.phoneNumberLayout.visibility = View.VISIBLE
                binding.phoneNumberTextView.text = it.phoneNumber
            }
        }
        contactEmail?.let {
            if (it.email.isNotEmpty()) {
                binding.emailLayout.visibility = View.VISIBLE
                binding.emailTextView.text = it.email
            }
        }
        contactAddress?.let {
            if (it.address.isNotEmpty()) {
                binding.addressLayout.visibility = View.VISIBLE
                binding.addressTextView.text = it.address
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