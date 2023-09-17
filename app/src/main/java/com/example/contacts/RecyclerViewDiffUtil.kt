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
            oldItems[oldItemPosition].contactName != newItems[newItemPosition].contactName -> false
            oldItems[oldItemPosition].contactPhoneNumber != newItems[newItemPosition].contactPhoneNumber -> false
            oldItems[oldItemPosition].contactEmail != newItems[newItemPosition].contactEmail -> false
            oldItems[oldItemPosition].contactAddress != newItems[newItemPosition].contactAddress -> false
            else -> true
        }
    }
}