package com.example.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.databinding.ContactListItemBinding


class ContactsListRecyclerViewAdapter(private val itemClick:RecyclerItemClickListener):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataList=mutableListOf<Contact>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding=ContactListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ContactItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem=dataList[position]
        (holder as ContactItemViewHolder).bind(dataItem)
        holder.itemView.setOnClickListener {
            itemClick.itemOnClick(dataItem.contactID)
        }

    }
    class ContactItemViewHolder(private val binding: ContactListItemBinding):RecyclerView.ViewHolder(binding.root){
       fun bind(dataItem:Contact){
           binding.contactDisplayName.text=dataItem.contactName
           if (!dataItem.contactName.isNullOrBlank()) {
               binding.contactDisplayName.text = dataItem.contactName
           } else if (!dataItem.contactPhoneNumber.isNullOrEmpty()) {
               binding.contactDisplayName.text = dataItem.contactPhoneNumber[0]
           } else if (!dataItem.contactEmail.isNullOrEmpty()) {
               binding.contactDisplayName.text = dataItem.contactEmail[0]
           }
       }
    }
    fun setDataList(newDataList: List<Contact>){
        val diffUtilCallBack=RecyclerViewDiffUtil(this.dataList,newDataList)
        val diffUtilResult=DiffUtil.calculateDiff(diffUtilCallBack)
        dataList.clear()
        dataList.addAll(newDataList)
        diffUtilResult.dispatchUpdatesTo(this)
    }

}