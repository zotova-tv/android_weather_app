package ru.gb.weather.view.contentprovider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_contact_list_adapter_item.view.*
import kotlinx.android.synthetic.main.fragment_history_recyclerview_item.view.*
import ru.gb.weather.R
import ru.gb.weather.model.Contact

class ContactListAdapter(private var onItemViewClickListener: ContactListFragment.OnItemViewClickListener?) :
    RecyclerView.Adapter<ContactListAdapter.RecyclerItemViewHolder>() {
    private var data: List<Contact> = arrayListOf()

    fun setData(data: List<Contact>) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contact: Contact) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.contact_name.text = contact.name
                itemView.contact_phone_number.text = contact.phone_number
                itemView.setOnClickListener {
                    onItemViewClickListener?.onItemViewClick(contact)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_contact_list_adapter_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    fun removeListener() {
        onItemViewClickListener = null
    }
}