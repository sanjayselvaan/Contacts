package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.contacts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private val fetchResultFromAddContactActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                notifyRecyclerAdapter()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        supportActionBar?.title=getString(R.string.app_name)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState==null){
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer,ContactListFragment(),
                contactListFragmentTag).commit()
        }
        binding.fab.setOnClickListener {
            val intent= Intent(this,AddContact::class.java)
            fetchResultFromAddContactActivity.launch(intent)
        }
    }
    private fun notifyRecyclerAdapter(){
        val fragment=supportFragmentManager.findFragmentByTag(contactListFragmentTag)
        if (fragment!=null){
            (fragment as ContactListFragment).notifyRecyclerAdapterForNewContact()
        }
    }
    companion object{
        const val contactListFragmentTag="fragment_tag"
    }
}