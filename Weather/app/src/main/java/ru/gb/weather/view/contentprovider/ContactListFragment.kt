package ru.gb.weather.view.contentprovider

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_contact_list.view.*
import ru.gb.weather.databinding.FragmentContactListBinding
import ru.gb.weather.model.Contact


const val REQUEST_CODE = 42
const val REQUEST_CONTACT_LIST_ACCESS_REASON = "Для отображения списка контактов в приложении Weather необходим доступ к списку контактов на устройстве"
const val REQUEST_CONTACT_LIST_ACCESS_TITLE = "Доступ к списку контактов"
const val REQUEST_CONTACT_LIST_ACCESS_AGREE = "Предоставить доступ"
const val REQUEST_CONTACT_LIST_ACCESS_NOT_AGREE = "Нет"

const val CLOSE = "Закрыть"
const val CALL_PHONE_URI_STARTSWITH = "tel:"

class ContactListFragment : Fragment() {
    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!
    private val contacts: MutableList<Contact> = mutableListOf()
    private val adapter: ContactListAdapter = ContactListAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(contact: Contact) {

            context?.let {
                when {
                    ContextCompat.checkSelfPermission(it, Manifest.permission.CALL_PHONE) ==
                            PackageManager.PERMISSION_GRANTED -> {
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse(CALL_PHONE_URI_STARTSWITH + contact.phone_number)
                        startActivity(intent)
                    }else -> {
                        requestPermission(Manifest.permission.CALL_PHONE)
                    }
                }
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setData(contacts)
        view.contactsRecycler.adapter = adapter
        checkPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContactListFragment()
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getContacts()
                }
                else -> {
                    requestPermission(Manifest.permission.READ_CONTACTS)
                }
            }
        }
    }

    private fun requestPermission(permission: String) {
         requestPermissions(arrayOf(permission), REQUEST_CODE)
    }

    private fun getContacts() {
        context?.let {context ->
            val contentResolver: ContentResolver = context.contentResolver
            val handler = Handler()
            Thread{

                val cursorWithContacts: Cursor? = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC"
                )
                cursorWithContacts?.let { cursor ->
                    for (i in 0..cursor.count) {
                        var contact: Contact? = null
                        val namePos = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val idPos = cursor.getColumnIndex((ContactsContract.Contacts._ID))
                        val hasPhoneNumber = cursor.getColumnIndex((ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        if (cursor.moveToPosition(i)) {
                            val name = cursor.getString(namePos)
                            val phoneId = cursor.getString(idPos)
                            val hasPhoneNumber = cursor.getInt(hasPhoneNumber)
                            if(hasPhoneNumber > 0){
                                val cursorWithPhone: Cursor? = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    arrayOf<String>(phoneId),
                                    null
                                )
                                cursorWithPhone?.let{cursorWithPhone ->
                                    cursorWithPhone.moveToFirst()
                                    val phonePos = cursorWithPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val phone = cursorWithPhone.getString(phonePos)
                                    contact = Contact(name, phone)
                                    cursorWithPhone.close()
                                }
                            }
                            contact?.let {contact ->
                                handler.post {
                                    contacts.add(contact)
                                    adapter.notifyItemInserted(contacts.size)
                                }
                            }

                        }
                    }
                    cursor.close()
                }

            }.start()
        }
    }

    interface OnItemViewClickListener {
        fun onItemViewClick(contact: Contact)
    }

    override fun onDestroy() {
        adapter.removeListener()
        super.onDestroy()
    }
}