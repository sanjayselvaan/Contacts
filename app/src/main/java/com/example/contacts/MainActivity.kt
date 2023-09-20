package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),RecyclerItemClickListener {
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
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.adapter=ContactsListRecyclerViewAdapter(this)
        (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(DataBase.getContactsList())
        val dividerItem= DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItem)
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
        intent.putExtra(positionOfDataItem,position)
        startActivity(intent)
    }
    companion object{
        const val positionOfDataItem="position_of_data_item"
    }
}