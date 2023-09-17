package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.example.contacts.databinding.ActivityAddContactBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AddContact : AppCompatActivity() {
    private lateinit var backPressed: OnBackPressedCallback
    private lateinit var binding: ActivityAddContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_contact)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val alertDialog = AlertDialog.Builder(this)
        backPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val name = binding.nameEditText.text
                val phoneNumber = binding.phoneNumberEditText.text
                val email = binding.emailEditText.text
                val address = binding.addressEditText.text
                val newContactEmail = if (!email.isNullOrEmpty()) email.toString() else null
                val newContactPhoneNumber =
                    if (!phoneNumber.isNullOrEmpty()) phoneNumber.toString() else null
                val newContactAddress = if (!address.isNullOrEmpty()) address.toString() else null
                alertDialog.setMessage(getString(R.string.your_changes_have_not_been_saved_warning))
                alertDialog.setPositiveButton(R.string.save) { _, _ ->
                    saveButtonOnClickAction()
                }
                alertDialog.setNegativeButton(R.string.discard) { _, _ ->
                    finish()
                }
                alertDialog.setNeutralButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                if (!name.isNullOrEmpty() || !newContactPhoneNumber.isNullOrEmpty() || !newContactEmail.isNullOrEmpty() || !newContactAddress.isNullOrEmpty()) {
                    alertDialog.show()
                } else {
                    finish()
                }

            }
        }
        onBackPressedDispatcher.addCallback(backPressed)
        binding.nameEditText.requestFocus()
        binding.addPhoneNumber.setOnClickListener {
            val extraView = layoutInflater.inflate(
                R.layout.extra_phone_number,
                binding.phoneNumberLinearLayout,
                false
            )
            extraView.id=View.generateViewId()
            val editText=extraView.findViewById<TextInputEditText>(R.id.phoneNumberEditText)
            val example=extraView.findViewById<TextInputLayout>(R.id.example1)
            example.id=View.generateViewId()
            editText.id=View.generateViewId()
            val childCount = binding.phoneNumberLinearLayout.childCount
            binding.phoneNumberLinearLayout.addView(extraView, childCount - 1)
        }
        binding.addEmail.setOnClickListener {
            val extraView =
                layoutInflater.inflate(R.layout.extra_email, binding.emailLinearLayout, false)
            val childCount = binding.emailLinearLayout.childCount
            binding.emailLinearLayout.addView(extraView, childCount - 1)
        }
        binding.addAddress.setOnClickListener {
            val extraView =
                layoutInflater.inflate(R.layout.extra_address, binding.addressLinearLayout, false)
            val childCount = binding.addressLinearLayout.childCount
            binding.addressLinearLayout.addView(extraView, childCount - 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_contact_menu, menu)
        return true
    }

    private fun saveButtonOnClickAction() {
        val intent = Intent(this, MainActivity::class.java)
        val name = binding.nameEditText.text
        val phoneNumber = binding.phoneNumberEditText.text
        val email = binding.emailEditText.text
        val address = binding.addressEditText.text
        val newId = DataBase.getContactListSize()
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
        val newContactPhoneNumber =
            if (!phoneNumber.isNullOrEmpty()) phoneNumber.toString() else null
        val newContactAddress = if (!address.isNullOrEmpty()) address.toString() else null
        val dbList = DataBase.getContactsList()
        Log.d("test1", "b4" + dbList.toString())
        when {
            newContactName != getString(R.string.unknown_name) && newContactPhoneNumber.isNullOrEmpty() && newContactEmail.isNullOrEmpty() && newContactAddress.isNullOrEmpty() -> {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        null,
                        null,
                        null
                    )
                )
                setResult(RESULT_OK, intent)
                Log.d("test1", "after" + dbList.toString())
                finish()

            }

            newContactPhoneNumber.isNullOrEmpty() && newContactEmail.isNullOrEmpty() && newContactAddress != null -> {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        null,
                        null,
                        newContactAddress
                    )
                )
                setResult(RESULT_OK, intent)
                Log.d("test1", "after" + dbList.toString())
                finish()
            }

            newContactPhoneNumber.isNullOrEmpty() && newContactEmail != null && newContactAddress.isNullOrEmpty() -> {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        null,
                        newContactEmail,
                        null
                    )
                )
                setResult(RESULT_OK, intent)
                Log.d("test1", "after" + dbList.toString())
                finish()
            }

            newContactPhoneNumber.isNullOrEmpty() && newContactEmail != null && newContactAddress != null -> {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        null,
                        newContactEmail,
                        newContactAddress
                    )
                )
                setResult(RESULT_OK, intent)
                Log.d("test1", "after" + dbList.toString())
                finish()
            }

            newContactPhoneNumber != null && newContactEmail.isNullOrEmpty() && newContactAddress.isNullOrEmpty() -> {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        newContactPhoneNumber,
                        null,
                        null
                    )
                )
                setResult(RESULT_OK, intent)
                Log.d("test1", "after" + dbList.toString())
                finish()
            }

            newContactPhoneNumber != null && newContactEmail.isNullOrEmpty() && newContactAddress != null -> {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        null,
                        null,
                        newContactAddress
                    )
                )
                setResult(RESULT_OK, intent)
                Log.d("test1", "after" + dbList.toString())
                finish()
            }

            newContactPhoneNumber != null && newContactEmail != null && newContactAddress.isNullOrEmpty() -> {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        newContactPhoneNumber,
                        newContactEmail,
                        null
                    )
                )
                setResult(RESULT_OK, intent)
                Log.d("test1", "after" + dbList.toString())
                finish()
            }

            newContactPhoneNumber != null && newContactEmail != null && newContactAddress != null -> {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        newContactPhoneNumber,
                        newContactEmail,
                        newContactAddress
                    )
                )
                setResult(RESULT_OK, intent)
                Log.d("test1", "after" + dbList.toString())
                finish()
            }
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        val phoneNumberDataList: ArrayList<String>
//        val emailDataList: ArrayList<String>
//        val addressDataList: ArrayList<String>
//        if (binding.phoneNumberLinearLayout.childCount > 2) {
//            phoneNumberDataList = collectTextFromEditText(
//                binding.phoneNumberLinearLayout.id,
//                binding.phoneNumberEditText.id
//            )
//            outState.putStringArrayList("phoneNumberDataList", phoneNumberDataList)
//        }
//        if (binding.emailLinearLayout.childCount > 2) {
//            emailDataList =
//                collectTextFromEditText(binding.emailLinearLayout.id, binding.emailEditText.id)
//            outState.putStringArrayList("emailDataList", emailDataList)
//        }
//        if (binding.addressLinearLayout.childCount > 2) {
//            addressDataList =
//                collectTextFromEditText(binding.emailLinearLayout.id, binding.addressEditText.id)
//            outState.putStringArrayList("addressDataList", addressDataList)
//        }
//
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        val phoneNumberDataList = savedInstanceState.getStringArrayList("phoneNumberDataList")
//        val emailDataList = savedInstanceState.getStringArrayList("emailDataList")
//        val addressDataList = savedInstanceState.getStringArrayList("addressDataList")
//        Log.d("test2",phoneNumberDataList.toString())
//        phoneNumberDataList?.let {
//            for (i in 1 until it.size) {
//                val extraView = layoutInflater.inflate(
//                    R.layout.extra_phone_number,
//                    binding.phoneNumberLinearLayout,
//                    false
//                )
//                val childCount = binding.phoneNumberLinearLayout.childCount
//                Log.d("test2",childCount.toString())
//                binding.phoneNumberLinearLayout.addView(extraView, childCount-1)
//            }
//            setTextForEditText(binding.phoneNumberLinearLayout.id,binding.phoneNumberEditText.id,it)
//        }
//        emailDataList?.let {
//            val extraView = layoutInflater.inflate(
//                R.layout.extra_email,
//                binding.emailLinearLayout,
//                false
//            )
//            for (i in 1 until it.size) {
//                val childCount = binding.emailLinearLayout.childCount
//                binding.emailLinearLayout.addView(extraView, childCount - 1)
//            }
//            setTextForEditText(binding.emailLinearLayout.id,binding.emailEditText.id,it)
//        }
//        addressDataList?.let {
//            val extraView = layoutInflater.inflate(
//                R.layout.extra_address,
//                binding.addressLinearLayout,
//                false
//            )
//            for (i in 1 until it.size) {
//                val childCount = binding.addressLinearLayout.childCount
//                binding.addressLinearLayout.addView(extraView, childCount - 1)
//            }
//            setTextForEditText(binding.addressLinearLayout.id,binding.addressEditText.id,it)
//        }
//
//    }

    private fun setTextForEditText(
        linearLayoutId: Int,
        editTextId: Int,
        dataList: ArrayList<String>
    ) {
        val layout=findViewById<LinearLayout>(linearLayoutId)
        for (i in 0 until layout.childCount - 1) {
            val view = layout.getChildAt(i)
            val editText = view.findViewById<TextInputEditText>(editTextId)
            editText.setText(dataList[i])
        }
    }

    private fun collectTextFromEditText(
        linearLayoutId: Int,
        editTextId: Int
    ): ArrayList<String> {
        val dataList = arrayListOf<String>()
        val layout = findViewById<LinearLayout>(linearLayoutId)
        for (i in 0 until layout.childCount - 1) {
            val view = layout.getChildAt(i)
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (!editText.text.isNullOrEmpty()) {
                dataList.add(editText.text.toString())
            }
        }
        return dataList
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                backPressed.handleOnBackPressed()
                return true
            }

            R.id.action_save -> {
                saveButtonOnClickAction()
            }
        }
        return super.onOptionsItemSelected(item)

    }
}
