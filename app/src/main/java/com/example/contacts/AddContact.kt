package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.contacts.databinding.ActivityAddContactBinding
import com.example.contacts.databinding.ExtraInputViewBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AddContact : AppCompatActivity() {
    private lateinit var backPressed: OnBackPressedCallback
    private lateinit var binding: ActivityAddContactBinding
    private lateinit var addContactsViewModel: AddContactsViewModel
    private lateinit var dataBase:DataBase
    private var exitAlertDialogVisibility:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataBase= DataBase(this)
        supportActionBar?.title = getString(R.string.add_contact)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addContactsViewModel = ViewModelProvider(this)[AddContactsViewModel::class.java]
        if (savedInstanceState == null) {
            binding.nameTextInputEditText.requestFocus()
            setUpFirstButtons()
        }

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage(getString(R.string.your_changes_have_not_been_saved_warning))
        alertDialog.setPositiveButton(R.string.save) { _, _ ->
            saveButtonOnClickAction()
        }
        alertDialog.setNegativeButton(R.string.discard) { _, _ ->
            finish()
        }
        alertDialog.setNeutralButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
            exitAlertDialogVisibility=false
        }
        backPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val name = binding.nameTextInputEditText.text
                val phoneNumberList=collectAndReturnTextFromTextInputEditText(R.id.phoneNumberLinearLayout,R.id.textInputEditText)
                val emailList=collectAndReturnTextFromTextInputEditText(R.id.emailLinearLayout,R.id.textInputEditText)
                val addressList=collectAndReturnTextFromTextInputEditText(R.id.addressLinearLayout,R.id.textInputEditText)
                if (!name.isNullOrEmpty() || phoneNumberList.isNotEmpty() || emailList.isNotEmpty() || addressList.isNotEmpty()) {
                    alertDialog.show()
                    exitAlertDialogVisibility=true
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(backPressed)
        if (savedInstanceState!=null){
            exitAlertDialogVisibility=savedInstanceState.getBoolean(exitAlertDialogKey)
        }
        if (exitAlertDialogVisibility){
            alertDialog.show()
        }
        alertDialog.setOnCancelListener {
            exitAlertDialogVisibility=false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_contact_menu, menu)
        return true
    }

    private fun saveButtonOnClickAction() {
        val name = binding.nameTextInputEditText.text.toString()
        var phoneNumberList: List<String>?
        var emailList: List<String>?
        var addressList: List<String>?
        val newId:Long
        val alertDialog=AlertDialog.Builder(this)
        if (checkForPhoneNumberValidation(
                binding.phoneNumberLinearLayout.id,
                R.id.textInputEditText
            ) && checkForEmailValidation(binding.emailLinearLayout.id, R.id.textInputEditText)
        ) {
            phoneNumberList = collectAndReturnTextFromTextInputEditText(
                binding.phoneNumberLinearLayout.id,
                R.id.textInputEditText
            )
            emailList = collectAndReturnTextFromTextInputEditText(
                binding.emailLinearLayout.id,
                R.id.textInputEditText
            )
            addressList = collectAndReturnTextFromTextInputEditText(
                binding.addressLinearLayout.id,
                R.id.textInputEditText
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
            if (!newContactName.isNullOrEmpty()||!newContactName.isNullOrBlank() || !phoneNumberList.isNullOrEmpty() || !emailList.isNullOrEmpty() || !addressList.isNullOrEmpty()) {
                newId=dataBase.addContact(newContactName, phoneNumberList, emailList, addressList)
                setResult(RESULT_OK)
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
                    Toast.makeText(this, "$it ${getString(R.string.is_saved)}", Toast.LENGTH_SHORT)
                        .show()
                }
                val intentForShowDetails = Intent(this, ShowContactDetails::class.java)
                intentForShowDetails.putExtra(MainActivity.idOfDataItem, newId)
                startActivity(intentForShowDetails)
                finish()
            } else {
                alertDialog.setMessage(getString(R.string.nothing_to_save))
                alertDialog.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    finish()
                }
                alertDialog.setNegativeButton(getString(R.string.cancel)){dialog,_->
                    dialog.dismiss()

                }
                alertDialog.show()
            }
        } else {
            Toast.makeText(this, getString(R.string.enter_valid_inputs), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        collectAndStoreTextFromTextInputEditText(
            binding.phoneNumberLinearLayout.id, R.id.textInputEditText
        )
        collectAndStoreTextFromTextInputEditText(
            binding.emailLinearLayout.id, R.id.textInputEditText
        )

        collectAndStoreTextFromTextInputEditText(
            binding.addressLinearLayout.id, R.id.textInputEditText
        )
        outState.putBoolean(exitAlertDialogKey,exitAlertDialogVisibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setUpFirstButtons()
        addContactsViewModel.getPhoneNumberDataList().let {
            it.forEach { text->
                if (text.isNotEmpty()){
                    val view=binding.phoneNumberLinearLayout.getChildAt(binding.phoneNumberLinearLayout.childCount-1)
                    val textInputLayout=view.findViewById<TextInputLayout>(R.id.textInputLayout)
                    textInputLayout.error = if (validateForPhoneNumber(text)) null else getString(R.string.please_enter_a_valid_phone_number)
                    val textInputEditText= view.findViewById<TextInputEditText>(R.id.textInputEditText)
                    textInputEditText.setText(text)
                }
            }
            val hasFocus=addContactsViewModel.getFocus()
            hasFocus?.let { pair ->
                if (pair.first==R.id.phoneNumberLinearLayout){
                    val view=binding.phoneNumberLinearLayout.getChildAt(pair.second)
                    val textInputLayout=view.findViewById<TextInputLayout>(R.id.textInputLayout)
                    textInputLayout.error=null
                    val textInputEditText=view.findViewById<TextInputEditText>(R.id.textInputEditText)
                    textInputEditText.requestFocus()
                }
            }
            addContactsViewModel.removeAllItemsInPhoneNumberDataList()
        }

        addContactsViewModel.getEmailDataList().let {
            it.forEach { text->
                if (text.isNotEmpty()){
                    val view=binding.emailLinearLayout.getChildAt(binding.emailLinearLayout.childCount-1)
                    val textInputLayout=view.findViewById<TextInputLayout>(R.id.textInputLayout)
                    textInputLayout.error=if(validateForEmail(text)) null else getString(R.string.please_enter_a_valid_email)
                    val textInputEditText=view.findViewById<TextInputEditText>(R.id.textInputEditText)
                    textInputEditText.setText(text)
                }
            }
            val hasFocus=addContactsViewModel.getFocus()
            hasFocus?.let { pair ->
                if (pair.first==R.id.emailLinearLayout){
                    val view=binding.emailLinearLayout.getChildAt(pair.second)
                    val textInputLayout=view.findViewById<TextInputLayout>(R.id.textInputLayout)
                    textInputLayout.error=null
                    val textInputEditText=view.findViewById<TextInputEditText>(R.id.textInputEditText)
                    textInputEditText.requestFocus()
                }
            }
            addContactsViewModel.removeAllItemsInEmailDataList()
        }

        addContactsViewModel.getAddressDataList().let {
            it.forEach { text->
                if (text.isNotEmpty()){
                    val view=binding.addressLinearLayout.getChildAt(binding.addressLinearLayout.childCount-1)
                    val textInputEditText=view.findViewById<TextInputEditText>(R.id.textInputEditText)
                    textInputEditText.setText(text)
                }
            }
            val hasFocus=addContactsViewModel.getFocus()
            hasFocus?.let { pair ->
                if (pair.first==R.id.addressLinearLayout){
                    val view=binding.addressLinearLayout.getChildAt(pair.second)
                    val textInputEditText=view.findViewById<TextInputEditText>(R.id.textInputEditText)
                    textInputEditText.requestFocus()
                }
            }

            addContactsViewModel.removeAllItemsInAddressDataList()
        }
        addContactsViewModel.clearFocus()
    }


    private fun collectAndStoreTextFromTextInputEditText(
        linearLayoutId: Int, editTextId: Int
    ) {
        val layout = findViewById<LinearLayout>(linearLayoutId)
        var count=0
        layout.forEach {childView->
            val editText=childView.findViewById<TextInputEditText>(editTextId)
            if (editText.hasFocus()){
                addContactsViewModel.setFocus(Pair(linearLayoutId,count))
            }
            if (editText != null) {
                when (linearLayoutId) {
                    binding.phoneNumberLinearLayout.id -> {
                        addContactsViewModel.addItemInPhoneNumberList(editText.text.toString())
                    }
                    binding.emailLinearLayout.id -> {
                        addContactsViewModel.addItemInEmailList(editText.text.toString())
                    }
                    binding.addressLinearLayout.id -> {
                        addContactsViewModel.addItemInAddressList(editText.text.toString())
                    }
                }
            }
            count++
        }
    }


    private fun addExtraPhoneNumberView() {
        val extraPhoneViewBinding =
            ExtraInputViewBinding.inflate(layoutInflater, binding.phoneNumberLinearLayout, false)
        setViewForPhoneNumber(extraPhoneViewBinding)
        binding.phoneNumberLinearLayout.addView(
            extraPhoneViewBinding.root
        )
    }
    private fun setViewForPhoneNumber(phoneViewBinding: ExtraInputViewBinding) {
        phoneViewBinding.removeButton.visibility=View.INVISIBLE
        phoneViewBinding.textInputLayout.hint=getString(R.string.phone)
        phoneViewBinding.textInputEditText.inputType=InputType.TYPE_CLASS_PHONE
        phoneViewBinding.removeButton.setOnClickListener {
            val position=binding.phoneNumberLinearLayout.indexOfChild(phoneViewBinding.root)
            binding.phoneNumberLinearLayout.removeView(phoneViewBinding.root)
            binding.phoneNumberLinearLayout.getChildAt(position).requestFocus()
        }
        var toAddExtraPhoneView=true
        phoneViewBinding.textInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                phoneViewBinding.textInputLayout.error=if (validateForPhoneNumber(phoneViewBinding.textInputEditText.text.toString())) null else getString(R.string.please_enter_a_valid_phone_number)
            }
            else{
                if (phoneViewBinding.textInputLayout.error != null) {
                    phoneViewBinding.textInputLayout.error = null
                }
            }
        }
        phoneViewBinding.textInputEditText.addTextChangedListener { editable ->
            if (editable != null) {
                    if(toAddExtraPhoneView && editable.isNotEmpty()){
                        addExtraPhoneNumberView()
                        phoneViewBinding.removeButton.visibility=View.VISIBLE
                        toAddExtraPhoneView=false
                    }
                     else if (editable.isEmpty() && binding.phoneNumberLinearLayout.childCount>1){
                        binding.phoneNumberLinearLayout.removeViewAt(binding.phoneNumberLinearLayout.childCount-1)
                        phoneViewBinding.textInputLayout.error=null
                        toAddExtraPhoneView=true
                        phoneViewBinding.removeButton.visibility=View.INVISIBLE
                    }
                }
        }
    }
    private fun addExtraEmailView(){
        val extraEmailViewBinding =
            ExtraInputViewBinding.inflate(layoutInflater, binding.emailLinearLayout, false)
        setViewForEmail(extraEmailViewBinding)
        binding.emailLinearLayout.addView(
            extraEmailViewBinding.root
        )
    }
    private fun setViewForEmail(emailViewBinding: ExtraInputViewBinding) {
        emailViewBinding.removeButton.visibility=View.INVISIBLE
        emailViewBinding.textInputLayout.hint=getString(R.string.email)
        emailViewBinding.textInputEditText.inputType=InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailViewBinding.removeButton.setOnClickListener {
            val position=binding.emailLinearLayout.indexOfChild(emailViewBinding.root)
            binding.emailLinearLayout.removeView(emailViewBinding.root)
            binding.emailLinearLayout.getChildAt(position).requestFocus()
        }
        var toAddExtraEmailView=true
        emailViewBinding.textInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                emailViewBinding.textInputLayout.error=if (validateForEmail(emailViewBinding.textInputEditText.text.toString())) null else getString(R.string.please_enter_a_valid_email)
            }
            else{
                if (emailViewBinding.textInputLayout.error!=null){
                    emailViewBinding.textInputLayout.error=null
                }
            }
        }
        emailViewBinding.textInputEditText.addTextChangedListener { text ->
                if(toAddExtraEmailView && text.toString().isNotEmpty()){
                    addExtraEmailView()
                    emailViewBinding.removeButton.visibility=View.VISIBLE
                    toAddExtraEmailView=false
                }
                else if (text.toString().isEmpty() && binding.emailLinearLayout.childCount>1){
                    binding.emailLinearLayout.removeViewAt(binding.emailLinearLayout.childCount-1)
                    emailViewBinding.textInputLayout.error=null
                    toAddExtraEmailView=true
                    emailViewBinding.removeButton.visibility=View.INVISIBLE
                }
            }
    }

    private fun addExtraAddressView(){
        val extraAddressViewBinding=ExtraInputViewBinding.inflate(layoutInflater,binding.addressLinearLayout,false)
        setViewForAddress(extraAddressViewBinding)
        binding.addressLinearLayout.addView(extraAddressViewBinding.root)
    }
    private fun setViewForAddress(addressViewBinding: ExtraInputViewBinding) {
        addressViewBinding.removeButton.visibility=View.INVISIBLE
        addressViewBinding.removeButton.setOnClickListener {
            val position=binding.addressLinearLayout.indexOfChild(addressViewBinding.root)
            binding.addressLinearLayout.removeView(addressViewBinding.root)
            binding.addressLinearLayout.getChildAt(position).requestFocus()
        }
        addressViewBinding.textInputEditText.inputType=EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
        addressViewBinding.textInputLayout.hint=getString(R.string.address)
        var toAddExtraAddressView=true
        addressViewBinding.textInputEditText.addTextChangedListener { text->
            if (toAddExtraAddressView && text.toString().isNotEmpty()){
                addExtraAddressView()
                addressViewBinding.removeButton.visibility=View.VISIBLE
                toAddExtraAddressView=false
                addressViewBinding.textInputEditText.imeOptions=EditorInfo.IME_ACTION_NEXT
            }
            else if(text.toString().isEmpty() && binding.addressLinearLayout.childCount>1){
                binding.addressLinearLayout.removeViewAt(binding.addressLinearLayout.childCount-1)
                toAddExtraAddressView=true
                addressViewBinding.removeButton.visibility=View.INVISIBLE
            }

        }

    }

    private fun setUpFirstButtons() {
        setViewForPhoneNumber(binding.phoneView)
        setViewForEmail(binding.emailView)
        setViewForAddress(binding.addressView)
    }

    private fun collectAndReturnTextFromTextInputEditText(
        linearLayoutId: Int,
        editTextId: Int
    ): List<String> {
        val dataList = mutableListOf<String>()
        val layout = findViewById<LinearLayout>(linearLayoutId)
       layout.forEach{ view->
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (editText.text.toString().isNotEmpty() && editText.text.toString().isNotBlank()) {
                dataList.add(editText.text.toString())
            }
        }
        return dataList
    }

    private fun checkForPhoneNumberValidation(linearLayoutId: Int, editTextId: Int): Boolean {
        val layout = findViewById<LinearLayout>(linearLayoutId)
        layout.forEach { view->
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (editText.text.toString().isNotEmpty()) {
                if (!validateForPhoneNumber(editText.text.toString()) && editText.text.toString().isNotBlank()) {
                    view.requestFocus()
                    return false
                }
            }
        }
        return true
    }

    private fun checkForEmailValidation(linearLayoutId: Int, editTextId: Int): Boolean {
        val layout = findViewById<LinearLayout>(linearLayoutId)
        layout.forEach{ view->
            val editText = view.findViewById<TextInputEditText>(editTextId)
            if (editText.text.toString().isNotEmpty() && editText.text.toString().isNotBlank()) {
                if (!validateForEmail(editText.text.toString())) {
                    view.requestFocus()
                    return false
                }
            }
        }
        return true
    }

    private fun validateForPhoneNumber(text: String): Boolean {
        if (text.isNotEmpty()) {
            val pattern = Regex("^\\d{10}$")
            return pattern.matches(text)
        }
        return true
    }

    private fun validateForEmail(text: String): Boolean {
        val pattern=Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")
        if (text.isNotEmpty()) {
            return pattern.matches(text)
        }
        return true
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
    companion object{
        private const val exitAlertDialogKey="exit_alert_dialog_key"
    }
}
