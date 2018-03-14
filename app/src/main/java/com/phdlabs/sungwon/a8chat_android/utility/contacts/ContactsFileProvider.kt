package com.phdlabs.sungwon.a8chat_android.utility.contacts

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.support.v4.content.FileProvider
import android.telephony.PhoneNumberUtils
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber

/**
 * Created by JPAM on 2/13/18.
 * Phone contacts file provider
 */
class ContactsFileProvider : FileProvider() {

    /*Singleton Holder*/
    private object Holder {
        var INSTANCE = ContactsFileProvider()
    }

    /*Companion*/
    companion object {
        val instance: ContactsFileProvider by lazy { Holder.INSTANCE }
    }

    /*LOCAL CONTACTS*/

    /**
     * [loadLocalContacts]
     * - Retrieves a list of contacts available on android phone
     * */
    fun loadLocalContacts(context: Context): List<LocalContact> {
        //Contacts Query Projection
        val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        val contentResolver: ContentResolver = context.contentResolver
        val cursor: Cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                android.provider.ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )
        val result: ArrayList<LocalContact> = ArrayList<LocalContact>(cursor.count)
        if (cursor.moveToFirst()) {
            do {
                //Retrieve contact Name
                val contactName = loadLocalContactsName(cursor, context) //Not used currently
                //Retrieve contact Phone Number
                val contactPhoneNumber = loadLocalContactsPhoneNumber(cursor, context)
                val contact = LocalContact(PhoneNumberUtils.normalizeNumber(contactPhoneNumber), contactName)
                //Only add contacts with phone
                if (!contact.phone.isBlank()) {
                    result.add(contact)
                }
            } while (cursor.moveToNext())
        }
        //De
        cursor.close()
        return result
    }

    /**
     * [loadLocalContactsName]
     * */
    private fun loadLocalContactsName(cursor: Cursor, context: Context): String {
        val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
        //Request contact
        val contactNameColumn = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
        val contactCursor = context.contentResolver?.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                contactNameColumn,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?",
                arrayOf(contactId),
                null)
        return if (contactCursor != null && contactCursor.moveToFirst()) {
            val columnIndex = contactCursor.getColumnIndex(contactNameColumn[0])
            val contactName = contactCursor.getString(columnIndex)
            contactCursor.close()
            contactName.toString().trim()
        } else {
            contactCursor?.close()
            ""
        }
    }

    /**
     * [loadLocalContactsPhoneNumber]
     * */
    private fun loadLocalContactsPhoneNumber(cursor: Cursor, context: Context): String {
        //Check if the user has phone number
        val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
        var phoneNumber = ""
        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
            val phoneCursor: Cursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(contactId),
                    null
            )
            while (phoneCursor.moveToNext()) {
                phoneNumber = phoneCursor.getString(
                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
            }
            phoneCursor.close()
            return phoneNumber
        } else {
            return ""
        }
    }

}