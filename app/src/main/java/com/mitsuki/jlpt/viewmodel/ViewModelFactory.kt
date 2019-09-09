package com.mitsuki.jlpt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mitsuki.jlpt.model.MainModel
import com.mitsuki.jlpt.model.SettingModel

class MainViewModelFactory(private val model: MainModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(model) as T
}

class SettingViewModelFactory(private val model: SettingModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = SettingViewModel(model) as T
}