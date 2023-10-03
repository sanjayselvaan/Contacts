package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),RecyclerItemClickListener{
    private lateinit var binding:ActivityMainBinding
    private var searchQuery:String?=null
    private var searchQueryDuplicate:String?=null
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
        if (savedInstanceState!=null){
            searchQueryDuplicate=savedInstanceState.getString(searchQueryKey)
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
        const val searchQueryKey="search_query"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        val searchItem=menu?.findItem(R.id.action_search)
        val searchView=searchItem?.actionView as SearchView
        searchView.imeOptions= EditorInfo.IME_FLAG_NO_EXTRACT_UI
        searchView.maxWidth= Int.MAX_VALUE
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("test1","OnTextChange")
                val queryText=newText?.lowercase()
                queryText?.let { query ->
                    val filterList=DataBase.getContactsList().filter { contact ->
                        contact.contactName?.contains(query, ignoreCase = true) ?: false || contact.contactPhoneNumber?.any {
                            it.contains(query, ignoreCase = true)
                        } ?: false  || contact.contactEmail?.any { it ->
                            it.contains(query, ignoreCase = true)
                        } ?:false || contact.contactAddress?.any{
                            it.contains(query, ignoreCase = true)
                        } ?: false
                    }
                    searchQuery=query
                    (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(filterList)
                }
                return true
            }
        })
        if (searchQueryDuplicate!=null){
            searchItem.expandActionView()
            searchView.setQuery(searchQueryDuplicate,true)
            searchQueryDuplicate=null
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (searchQuery!=null){
            outState.putString(searchQueryKey,searchQuery)
        }
    }



}