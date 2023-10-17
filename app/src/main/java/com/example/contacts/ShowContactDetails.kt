package com.example.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.example.contacts.databinding.ActivityContactDetailsBinding
import com.example.contacts.databinding.ExtraTextViewBinding

class ShowContactDetails : AppCompatActivity() {
    private lateinit var binding: ActivityContactDetailsBinding
    private lateinit var backPressed: OnBackPressedCallback
    private lateinit var dataBase:DataBase
    private var deleteAlertVisibility=false
    private lateinit var alertDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.contact_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataBase=DataBase(this)
        val id = intent.getLongExtra(MainActivity.idOfDataItem, 0)
        val contactItem=dataBase.getContact(id)
        val contactName = contactItem?.contactName
        val contactNumber = contactItem?.contactPhoneNumber
        val contactEmail = contactItem?.contactEmail
        val contactAddress = contactItem?.contactAddress
        alertDialog= AlertDialog.Builder(this)
        alertDialog.setPositiveButton(R.string.delete){_,_->
            dataBase.deleteContact(id)
            setResult(RESULT_OK)
            finish()
        }
        alertDialog.setNegativeButton(R.string.cancel){dialog,_->
            dialog.dismiss()
            deleteAlertVisibility=false
        }
        alertDialog.setTitle(R.string.delete)
        alertDialog.setMessage(R.string.delete_contact_alert_message)
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
        if (savedInstanceState!=null){
            deleteAlertVisibility=savedInstanceState.getBoolean(deleteAlertKey)
        }
        if (deleteAlertVisibility){
            alertDialog.show()
        }
        alertDialog.setOnCancelListener {
            Log.d("test1","dimiss")
            deleteAlertVisibility=false
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                backPressed.handleOnBackPressed()
                return true
            }
            R.id.action_delete->{
                alertDialog.show()
                deleteAlertVisibility=true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.show_contact_details_menu,menu)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(deleteAlertKey,deleteAlertVisibility)
    }

    companion object{
        private const val deleteAlertKey="delete_alert_key"
    }
}