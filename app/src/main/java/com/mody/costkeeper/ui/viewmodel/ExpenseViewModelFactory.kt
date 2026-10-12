package com.mody.costkeeper.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mody.costkeeper.data.repository.ExpenseRepository

class ExpenseViewModelFactory(
    private val context: Context,
    private val repo: ExpenseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = ExpenseViewModel(repo)
        vm.loadLanguage(context)
        return vm as T
    }
}