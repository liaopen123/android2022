package com.hhz.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class HomeViewModel (application: Application) : AndroidViewModel(application){
    fun request() {
        getPersonalHomepageInfoObs.value = "廖鹏辉"
    }

    val getPersonalHomepageInfoObs = MutableLiveData<String>()

}