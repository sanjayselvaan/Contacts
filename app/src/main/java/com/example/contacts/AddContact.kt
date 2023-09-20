package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.contacts.databinding.ActivityAddContactBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AddContact : AppCompatActivity() {
    private lateinit var backPressed: OnBackPressedCallback
    private lateinit var binding: ActivityAddContactBinding
    private lateinit var addContactsViewModel: AddContactsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_contact)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addContactsViewModel = ViewModelProvider(this)[AddContactsViewModel::class.java]
        if (savedInstanceState == null) {
            binding.nameEditText.requestFocus()
        }
        loadFirstButtons()
        val alertDialog = AlertDialog.Builder(this)
        backPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val name = binding.nameEditText.text
                val phoneNumberList=collectAndReturnTextFromTextInputEditText(R.id.phoneNumberLinearLayout,R.id.phoneNumberTextInputEditText)
                val emailList=collectAndReturnTextFromTextInputEditText(R.id.emailLinearLayout,R.id.emailTextInputEditText)
                val addressList=collectAndReturnTextFromTextInputEditText(R.id.addressLinearLayout,R.id.addressTextInputEditText)
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
                if (!name.isNullOrEmpty() || phoneNumberList.isNotEmpty() || emailList.isNotEmpty() || addressList.isNotEmpty()) {
                    alertDialog.show()
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(backPressed)

        binding.addPhoneNumber.setOnClickListener {
            val extraPhoneView = layoutInflater.inflate(
                R.layout.extra_phone_number,
                binding.phoneNumberLinearLayout,
                false
            )
            val phoneNumberTextInputLayout =
                extraPhoneView.findViewById<TextInputLayout>(R.id.phoneNumberTextInputLayout)
            val removeButton = extraPhoneView.findViewById<ImageButton>(R.id.removeButton)
            removeButton.setOnClickListener {
                binding.phoneNumberLinearLayout.removeView(extraPhoneView)
            }
            val phoneNumberTextInputEditText =
                extraPhoneView.findViewById<TextInputEditText>(R.id.phoneNumberTextInputEditText)
            phoneNumberTextInputEditText.addTextChangedListener { test ->
                if (validateForPhoneNumber(test.toString())) {
                    phoneNumberTextInputLayout.error = null
                } else {
                    phoneNumberTextInputLayout.error = getString(R.string.please_enter_a_valid_phone_number)
                }
            }
            val childCount = binding.phoneNumberLinearLayout.childCount
            binding.phoneNumberLinearLayout.addView(extraPhoneView, childCount - 1)
            extraPhoneView.requestFocus()
        }
        binding.addEmail.setOnClickListener {
            val extraEmailView =
                layoutInflater.inflate(R.layout.extra_email_view, binding.emailLinearLayout, false)
            val emailTextInputLayout =
                extraEmailView.findViewById<TextInputLayout>(R.id.emailTextInputLayout)
            val removeButton = extraEmailView.findViewById<ImageButton>(R.id.removeButton)
            removeButton.setOnClickListener {
                binding.emailLinearLayout.removeView(extraEmailView)
            }
            val emailTextInputEditText =
                extraEmailView.findViewById<TextInputEditText>(R.id.emailTextInputEditText)
            emailTextInputEditText.addTextChangedListener { text ->
                if (validateForEmail(text.toString())) {
                    emailTextInputLayout.error = null
                } else {
                    emailTextInputLayout.error = getString(R.string.please_enter_a_valid_phone_number)
                }

            }
            val childCount = binding.emailLinearLayout.childCount
            binding.emailLinearLayout.addView(extraEmailView, childCount - 1)
            extraEmailView.requestFocus()
        }
        binding.addAddress.setOnClickListener {
            val extraAddressView =
                layoutInflater.inflate(R.layout.extra_address, binding.addressLinearLayout, false)
            val childCount = binding.addressLinearLayout.childCount
            val removeButton = extraAddressView.findViewById<ImageButton>(R.id.removeButton)
            removeButton.setOnClickListener {
                binding.addressLinearLayout.removeView(extraAddressView)
            }
            binding.addressLinearLayout.addView(extraAddressView, childCount - 1)
            extraAddressView.requestFocus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_contact_menu, menu)
        return true
    }

    private fun saveButtonOnClickAction() {
        val intent = Intent(this, MainActivity::class.java)
        val name = binding.nameEditText.text.toString()
        var phoneNumberList: MutableList<String>?
        var emailList: MutableList<String>?
        var addressList: MutableList<String>?
        val newId = DataBase.getContactListSize()
        if (checkForPhoneNumberValidation(
                binding.phoneNumberLinearLayout.id,
                R.id.phoneNumberTextInputEditText
            ) && checkForEmailValidation(binding.emailLinearLayout.id, R.id.emailTextInputEditText)
        ) {
            phoneNumberList = collectAndReturnTextFromTextInputEditText(
                binding.phoneNumberLinearLayout.id,
                R.id.phoneNumberTextInputEditText
            )
            emailList = collectAndReturnTextFromTextInputEditText(
                binding.emailLinearLayout.id,
                R.id.emailTextInputEditText
            )
            addressList = collectAndReturnTextFromTextInputEditText(
                binding.addressLinearLayout.id,
                R.id.addressTextInputEditText
            )

            val newContactName = when {
                name.isNotBlank() && name.isNotEmpty() -> {
                    name
                }

                name.isBlank() && phoneNumberList.isEmpty() && emailList.isEmpty() && addressList.isNotEmpty() -> {
                    getString(R.string.unknown_name)
                }

                else -> {
                    null
                }
            }
            if (phoneNumberList.isEmpty()) {
                phoneNumberList = null
            }
            if (emailList.isEmpty()) {
                emailList = null
            }
            if (addressList.isEmpty()) {
                addressList = null
            }
            if (!newContactName.isNullOrBlank() || !phoneNumberList.isNullOrEmpty() || !emailList.isNullOrEmpty() || !addressList.isNullOrEmpty()) {
                DataBase.addContact(
                    Contact(
                        newId,
                        newContactName,
                        phoneNumberList,
                        emailList,
                        addressList
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
                val toastText = when {
                    newContactName?.isNotBlank() == true -> {
                        newContactName
                    }

                    phoneNumberList?.isNotEmpty() == true -> {
                        phoneNumberList[0]
                    }

                    emailList?.isNotEmpty() == true -> {
                        emailList[0]
                    }

                    else -> {
                        null
                    }
                }
                toastText?.let {
                    Toast.makeText(this, it + getString(R.string.is_saved), Toast.LENGTH_SHORT)
                        .show()
                }
                val intentForShowDetails = Intent(this, ShowContactDetails::class.java)
                intentForShowDetails.putExtra(ContactListFragment.positionOfDataItem, newId)
                startActivity(intentForShowDetails)
                finish()
            } else {
                finish()
            }
        } else {
            Toast.makeText(this, getString(R.string.enter_valid_inputs), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        collectAndStoreTextFromTextInputEditText(
            binding.phoneNumberLinearLayout.id, R.id.phoneNumberTextInputEditText
        )
        collectAndStoreTextFromTextInputEditText(
            binding.emailLinearLayout.id, R.id.emailTextInputEditText
        )

        collectAndStoreTextFromTextInputEditText(
            binding.addressLinearLayout.id, R.id.addressTextInputEditText
        )

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        addContactsViewModel.getPhoneNumberDataList().let {
            for (i in 1 until it.size) {
                val extraPhoneNumberView = layoutInflater.inflate(
                    R.layout.extra_phone_number,
                    binding.phoneNumberLinearLayout,
                    false
                )
                binding.phoneNumberLinearLayout.addView(
                    extraPhoneNumberView,
                    binding.phoneNumberLinearLayout.childCount - 1
                )
                val phoneNumberTextInputLayout =
                    extraPhoneNumberView.findViewById<TextInputLayout>(R.id.phoneNumberTextInputLayout)
                val phoneNumberTextInputEditText =
                    extraPhoneNumberView.findViewById<TextInputEditText>(R.id.phoneNumberTextInputEditText)
                val removeButton = extraPhoneNumberView.findViewById<ImageButton>(R.id.removeButton)
                removeButton.setOnClickListener {
                    binding.phoneNumberLinearLayout.removeView(extraPhoneNumberView)
                }
                phoneNumberTextInputEditText.addTextChangedListener { text ->
                    if (validateForPhoneNumber(text.toString())) {
                        phoneNumberTextInputLayout.error = null
                    } else {
                        phoneNumberTextInputLayout.error = getString(R.string.please_enter_a_valid_phone_number)
                    }
                }
            }
            setTextForEditText(
                binding.phoneNumberLinearLayout.id,
                it, R.id.phoneNumberTextInputEditText
            )
            addContactsViewModel.removeAllItemsInPhoneNumberDataList()
            addContactsViewModel.removeAllItemsInPhoneNumberValidityList()
        }

        addContactsViewModel.getEmailDataList().let {
            for (i in 1 until it.size) {
                val extraEmailView = layoutInflater.inflate(
                    R.layout.extra_email_view,
                    binding.emailLinearLayout,
                    false
                )
                binding.emailLinearLayout.addView(
                    extraEmailView,
                    binding.emailLinearLayout.childCount - 1
                )
                val emailTextInputLayout =
                    extraEmailView.findViewById<TextInputLayout>(R.id.emailTextInputLayout)
                val emailTextInputEditText =
                    extraEmailView.findViewById<TextInputEditText>(R.id.emailTextInputEditText)
                val removeButton = extraEmailView.findViewById<ImageButton>(R.id.removeButton)
                removeButton.setOnClickListener {
                    binding.emailLinearLayout.removeView(extraEmailView)
                }
                emailTextInputEditText.addTextChangedListener { text ->
                    if (validateForEmail(text.toString())) {
                        emailTextInputLayout.error = null
                    } else {
                        emailTextInputLayout.error = getString(R.string.please_enter_a_valid_email)
                    }
                }
            }
            setTextForEditText(
                binding.emailLinearLayout.id,
                it, R.id.emailTextInputEditText
            )
            addContactsViewModel.removeAllItemsInEmailDataList()
            addContactsViewModel.removeAllItemsInEmailValidityList()
        }

        addContactsViewModel.getAddressDataList().let {
            for (i in 1 until it.size) {
                val extraAddressView = layoutInflater.inflate(
                    R.layout.extra_address,
                    binding.addressLinearLayout,
                    false
                )
                binding.addressLinearLayout.addView(
                    extraAddressView,
                    binding.addressLinearLayout.childCount - 1
                )
                val removeButton = extraAddressView.findViewById<ImageButton>(R.id.removeButton)
                removeButton.setOnClickListener {
                    binding.addressLinearLayout.removeView(extraAddressView)
                }
                setTextForEditText(
                    binding.addressLinearLayout.id,
                    it, R.id.addressTextInputEditText
                )
                addContactsViewModel.removeAllItemsInAddressDataList()
            }
        }
    }


    private fun setTextForEditText(
        linearLayoutId: Int,
        dataList: List<String>,
        editTextId: Int
    ) {
        val layout = findViewById<LinearLayout>(linearLayoutId)
        val validityList:List<Boolean> = if (linearLayoutId==binding.phoneNumberLinearLayout.id){
            addContactsViewModel.getPhoneNumberValidityList()
        } else{
            addContactsViewModel.getEmailValidityList()
        }
        for (i in 0 until layout.childCount - 1) {
            val view = layout.getChildAt(i)
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (dataList.isNotEmpty()) {
                editText.setText(dataList[i])
            }
            if (linearLayoutId == binding.phoneNumberLinearLayout.id) {
                val textInputLayout =
                    view.findViewById<TextInputLayout>(R.id.phoneNumberTextInputLayout)
                if (!validityList[i]) {
                    textInputLayout.error = null
                } else {
                    textInputLayout.error = getString(R.string.please_enter_a_valid_phone_number)
                }
            } else if (linearLayoutId == binding.emailLinearLayout.id) {
                val textInputLayout = view.findViewById<TextInputLayout>(R.id.emailTextInputLayout)
                if (!validityList[i]) {
                    textInputLayout.error = null
                } else {
                    textInputLayout.error = getString(R.string.please_enter_a_valid_email)
                }
            }
        }
    }

    private fun collectAndStoreTextFromTextInputEditText(
        linearLayoutId: Int, editTextId: Int
    ) {
        val layout = findViewById<LinearLayout>(linearLayoutId)
        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (editText != null) {
                when (linearLayoutId) {
                    binding.phoneNumberLinearLayout.id -> {
                        addContactsViewModel.addItemInPhoneNumberList(editText.text.toString())
                        val textInputLayout=view.findViewById<TextInputLayout>(R.id.phoneNumberTextInputLayout)
                        if (textInputLayout.error!=null){
                            addContactsViewModel.addItemInPhoneNumberValidityList(true)
                        }
                        else{
                            addContactsViewModel.addItemInPhoneNumberValidityList(false)
                        }
                    }
                    binding.emailLinearLayout.id -> {
                        addContactsViewModel.addItemInEmailList(editText.text.toString())
                        val textInputLayout=view.findViewById<TextInputLayout>(R.id.emailTextInputLayout)
                        if (textInputLayout.error!=null){
                            addContactsViewModel.addItemInEmailValidityList(true)
                        }
                        else{
                            addContactsViewModel.addItemInEmailValidityList(false)
                        }
                    }
                    binding.addressLinearLayout.id -> {
                        addContactsViewModel.addItemInAddressList(editText.text.toString())
                    }
                }
            }
        }
    }

    private fun loadFirstButtons() {
        val extraPhoneView = layoutInflater.inflate(
            R.layout.extra_phone_number,
            binding.phoneNumberLinearLayout,
            false
        )
        val phoneNumberTextInputLayout =
            extraPhoneView.findViewById<TextInputLayout>(R.id.phoneNumberTextInputLayout)
        val removeButton = extraPhoneView.findViewById<ImageButton>(R.id.removeButton)
        removeButton.visibility = View.INVISIBLE
        val phoneNumberTextInputEditText =
            extraPhoneView.findViewById<TextInputEditText>(R.id.phoneNumberTextInputEditText)
        phoneNumberTextInputEditText.addTextChangedListener { text ->
            if (validateForPhoneNumber(text.toString())) {
                phoneNumberTextInputLayout.error = null
            } else {
                phoneNumberTextInputLayout.error = "Please enter a valid phone number"
            }

        }
        val childCount = binding.phoneNumberLinearLayout.childCount
        binding.phoneNumberLinearLayout.addView(extraPhoneView, childCount - 1)

        val extraEmailView =
            layoutInflater.inflate(R.layout.extra_email_view, binding.emailLinearLayout, false)
        val emailTextInputLayout =
            extraEmailView.findViewById<TextInputLayout>(R.id.emailTextInputLayout)
        val removeButtonForEmail = extraEmailView.findViewById<ImageButton>(R.id.removeButton)
        removeButtonForEmail.visibility = View.INVISIBLE
        val emailTextInputEditText =
            extraEmailView.findViewById<TextInputEditText>(R.id.emailTextInputEditText)
        emailTextInputEditText.addTextChangedListener { text ->
            if (validateForEmail(text.toString())) {
                emailTextInputLayout.error = null
            } else {
                emailTextInputLayout.error = "Please enter a valid email"
            }
        }
        val childCountForEmail = binding.emailLinearLayout.childCount
        binding.emailLinearLayout.addView(extraEmailView, childCountForEmail - 1)

        val extraAddressView =
            layoutInflater.inflate(R.layout.extra_address, binding.addressLinearLayout, false)
        val removeButtonForAddress = extraAddressView.findViewById<ImageButton>(R.id.removeButton)
        removeButtonForAddress.visibility = View.INVISIBLE
        val childCountForAddress = binding.addressLinearLayout.childCount
        binding.addressLinearLayout.addView(extraAddressView, childCountForAddress - 1)
    }

    private fun collectAndReturnTextFromTextInputEditText(
        linearLayoutId: Int,
        editTextId: Int
    ): MutableList<String> {
        val dataList = mutableListOf<String>()
        val layout = findViewById<LinearLayout>(linearLayoutId)
        for (i in 0 until layout.childCount - 1) {
            val view = layout.getChildAt(i)
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (editText.text.toString().isNotEmpty()) {
                dataList.add(editText.text.toString())
            }
        }
        return dataList
    }

    private fun checkForPhoneNumberValidation(linearLayoutId: Int, editTextId: Int): Boolean {
        val layout = findViewById<LinearLayout>(linearLayoutId)
        for (i in 0 until layout.childCount - 1) {
            val view = layout.getChildAt(i)
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (editText.text.toString().isNotEmpty()) {
                if (!validateForPhoneNumber(editText.text.toString())) {
                    return false
                }
            }
        }
        return true
    }

    private fun checkForEmailValidation(linearLayoutId: Int, editTextId: Int): Boolean {
        val layout = findViewById<LinearLayout>(linearLayoutId)
        for (i in 0 until layout.childCount - 1) {
            val view = layout.getChildAt(i)
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (editText.text.toString().isNotEmpty()) {
                if (!validateForEmail(editText.text.toString())) {
                    return false
                }
            }
        }
        return true
    }

    private fun validateForPhoneNumber(text: String): Boolean {
        val pattern = Regex("^\\d{10}$")
        return pattern.matches(text)
    }

    private fun validateForEmail(text: String): Boolean {
//        val pattern=Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,4}\"")
        return Patterns.EMAIL_ADDRESS.matcher(text).matches()

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
