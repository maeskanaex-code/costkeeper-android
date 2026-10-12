package com.mody.costkeeper.data.repository

import com.mody.costkeeper.data.local.ExpenseDao
import com.mody.costkeeper.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val dao: ExpenseDao
) {
    fun getAllExpenses(): Flow<List<ExpenseEntity>> = dao.getAllExpenses()

    suspend fun insert(expense: ExpenseEntity) = dao.insert(expense)

    suspend fun delete(expense: ExpenseEntity) = dao.delete(expense)

    suspend fun clearAll() = dao.clearAll()
}