package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivityMainBinding
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity(), RecyclerItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private var searchQuery: String? = null
    private var searchQueryDuplicate: String? = null
    private lateinit var dataBaseHelper: DataBaseHelper
    private val fetchResultFromAddContactActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                notifyRecyclerAdapter()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.app_name)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataBaseHelper = DataBaseHelper(contentResolver)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ContactsListRecyclerViewAdapter(this)
        lifecycleScope.launch{
            val contactList = dataBaseHelper.getContactsList()
            (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(contactList)
        }
        val dividerItem = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItem)
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddContact::class.java)
            fetchResultFromAddContactActivity.launch(intent)
        }
        if (savedInstanceState != null) {
            searchQueryDuplicate = savedInstanceState.getString(searchQueryKey)
        }

    }

    private fun notifyRecyclerAdapter() {
        lifecycleScope.launch {
            val contactList = dataBaseHelper.getContactsList()
            (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(contactList)
        }
    }

    override fun itemOnClick(id: Long) {
        val intent = Intent(this, ShowContactDetails::class.java)
        intent.putExtra(idOfDataItem, id)
        fetchResultFromAddContactActivity.launch(intent)
    }

    companion object {
        const val idOfDataItem = "position_of_data_item"
        const val searchQueryKey = "search_query"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    searchQuery = if (query.isNotEmpty()) {
                        lifecycleScope.launch {
                            val filterList = dataBaseHelper.searchContact(query)
                            (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(filterList)
                        }
                        query

                    } else {
                        lifecycleScope.launch {
                            val list = dataBaseHelper.getContactsList()
                            (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(list)
                        }
                        query
                    }
                }
                return true
            }
        })
        if (searchQueryDuplicate != null) {
            searchItem.expandActionView()
            searchView.setQuery(searchQueryDuplicate, true)
            searchQueryDuplicate = null
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (searchQuery != null) {
            outState.putString(searchQueryKey, searchQuery)
        }
    }


}