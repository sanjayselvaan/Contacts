package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),RecyclerItemClickListener {
    private lateinit var binding:ActivityMainBinding
    private val fetchResultFromAddContactActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d("test1","in fetch result"+DataBase.getContactsList())
                notifyRecyclerAdapter()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        supportActionBar?.title=getString(R.string.app_name)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.adapter=ContactsListRecyclerViewAdapter(this)
        (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(DataBase.getContactsList())
        val dividerItem=DividerItemDecoration(this,LinearLayoutManager.HORIZONTAL)
        dividerItem.setDrawable(ContextCompat.getDrawable(this,R.drawable.divider)!!)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.HORIZONTAL))
        binding.fab.setOnClickListener {
            val intent= Intent(this,AddContact::class.java)
            fetchResultFromAddContactActivity.launch(intent)
        }
    }
    private fun notifyRecyclerAdapter(){
        val list=DataBase.getContactsList()
        (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(list)
    }
    override fun itemOnClick(position: Int) {
        val intent= Intent(this,ShowContactDetails::class.java)
        intent.putExtra(ContactListFragment.positionOfDataItem,position)
        startActivity(intent)
    }
}