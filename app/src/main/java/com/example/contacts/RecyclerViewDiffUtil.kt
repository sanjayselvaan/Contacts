package com.example.contacts


import androidx.recyclerview.widget.DiffUtil

class RecyclerViewDiffUtil(private val oldItems: List<Contact>,
                           private val newItems: List<Contact>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].contactID == newItems[newItemPosition].contactID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldItems[oldItemPosition].contactName.name != newItems[newItemPosition].contactName.name -> false
            oldItems[oldItemPosition].contactPhoneNumber?.phoneNumber != newItems[newItemPosition].contactPhoneNumber?.phoneNumber -> false
            oldItems[oldItemPosition].contactEmail?.email != newItems[newItemPosition].contactEmail?.email -> false
            oldItems[oldItemPosition].contactAddress?.address != newItems[newItemPosition].contactAddress?.address -> false
            else -> true
        }
    }
}