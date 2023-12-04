package com.technogenis.expensior.bottomsheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// task model class to observe data in runtime
class ViewTaskModel : ViewModel(){

    var name = MutableLiveData<String>()
    var dialogStatus = MutableLiveData<String>()
    var category = MutableLiveData<String>()
    var bankCheck = MutableLiveData<String>()
    var fromName = MutableLiveData<String>()
    var toName = MutableLiveData<String>()
}