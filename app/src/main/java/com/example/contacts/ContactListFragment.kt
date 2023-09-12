package com.example.contacts

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.FragmentContactListBinding
import java.util.Collections

class ContactListFragment : Fragment(),RecyclerItemClickListener {
    private lateinit var binding:FragmentContactListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentContactListBinding.inflate(inflater,container,false)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=ContactsListRecyclerViewAdapter(this)
        (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(DataBase.getContactsList())
        return binding.root
    }

    override fun itemOnClick(position: Int) {
        val intent= Intent(requireActivity(),ContactDetails::class.java)
        intent.putExtra("position",position)
        startActivity(intent)
    }
    fun notifyRecyclerAdapterForNewContact(){
        binding.recyclerView.adapter?.notifyItemInserted(DataBase.getContactListSize())
        Log.d("test1","db"+DataBase.getContactsList().toString())
        (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter)
        Log.d("test1","db after changes"+DataBase.getContactsList().toString())
    }

}