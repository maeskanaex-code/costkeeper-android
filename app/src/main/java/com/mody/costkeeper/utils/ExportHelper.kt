package com.mody.costkeeper.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.mody.costkeeper.data.local.ExpenseEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ExportHelper {

    suspend fun exportToCsv(
        context: Context,
        expenses: List<ExpenseEntity>
    ): Intent? {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val fileName = "costkeeper_export_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)

            // Create CSV content
            val csvContent = buildString {
                appendLine("ID,Title,Amount,Category,Date,Type")
                expenses.forEach { expense ->
                    val type = if (expense.amount > 0) "Income" else "Expense"
                    val amount = kotlin.math.abs(expense.amount)
                    appendLine("${expense.id},${expense.title},$amount,${expense.category},${dateFormat.format(Date(expense.date))},$type")
                }
            }

            file.writeText(csvContent)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}