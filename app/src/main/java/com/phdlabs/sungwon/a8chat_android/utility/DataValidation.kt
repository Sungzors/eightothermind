package com.phdlabs.sungwon.a8chat_android.utility

/**
 * Created by paix on 12/21/17.
 * Form data validation
 */
class DataValidation {

    /*Singleton*/
    companion object {
        var instance: DataValidation = DataValidation()
        private set
    }

    fun emptyData(dataAsString: String): Boolean {
        if(dataAsString.isEmpty()){
            return true
        }
        return false
    }

}