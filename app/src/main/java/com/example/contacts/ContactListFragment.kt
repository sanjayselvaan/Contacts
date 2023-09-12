package com.example.contacts

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.FragmentContactListBinding

class ContactListFragment : Fragment(),RecyclerItemClickListener {
    private lateinit var binding:FragmentContactListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= FragmentContactListBinding.inflate(inflater,container,false)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=ContactsListRecyclerViewAdapter(this)
        (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(DataBase.getContactsList())
        return binding.root
    }

    override fun itemOnClick(position: Int) {
        val intent= Intent(requireActivity(),ShowContactDetails::class.java)
        intent.putExtra(positionOfDataItem,position)
        startActivity(intent)
    }
    fun notifyRecyclerAdapterForNewContact(){
        (binding.recyclerView.adapter as ContactsListRecyclerViewAdapter).setDataList(DataBase.getContactsList())
    }
    companion object{
        const val positionOfDataItem="position_of_data_item"
    }

}