package com.phdlabs.sungwon.a8chat_android.db

import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact

/**
 * Created by jpam on 2/24/18.
 * [EightQueries]
 * Used for common used queries, filtering & complex comparators
 */
object EightQueries {

    /**
     * [Comparators]
     * Used for ordering
     * */
    object Comparators {
        //Alphabetical comparator
         val alphabetComparator = object : Comparator<Contact> {
            override fun compare(a: Contact?, b: Contact?): Int {
                a?.first_name?.let {
                    b?.first_name?.let {
                        return a.first_name!!.compareTo(b.first_name!!)
                    }
                }
                return 0
            }
        }

        val alphabetComparatorGroupCreate = Comparator<Triple<Int, String, String>> { a, b ->
            return@Comparator a.second.compareTo(b.second)
        }
    }

}