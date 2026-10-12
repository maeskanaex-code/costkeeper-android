package com.mody.costkeeper.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.core.content.FileProvider
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class PDFReportData(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val expenses: List<ExpenseItem>,
    val currencySymbol: String,
    val dateRange: String
)

data class ExpenseItem(
    val date: String,
    val category: String,
    val title: String,
    val amount: Double,
    val type: String // "Income" or "Expense"
)

object PDFGenerator {

    suspend fun generatePDF(
        context: Context,
        data: PDFReportData,
        isDarkTheme: Boolean
    ): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "CostKeeper_Report_$timeStamp.pdf"
            val file = File(context.cacheDir, fileName)

            val writer = PdfWriter(FileOutputStream(file))
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            // Set margins
            document.setMargins(50f, 50f, 50f, 50f)

            // Title
            val title = Paragraph("CostKeeper Expense Report")
                .setFontSize(24f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE)
            document.add(title)

            // Date range
            val dateRange = Paragraph(data.dateRange)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY)
            document.add(dateRange)

            document.add(Paragraph(" ")) // Empty line

            // Summary Section
            val summaryTitle = Paragraph("Financial Summary")
                .setFontSize(16f)
                .setBold()
                .setFontColor(ColorConstants.DARK_GRAY)
            document.add(summaryTitle)

            document.add(Paragraph(" "))

            // Summary Table
            val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
            summaryTable.setWidth(UnitValue.createPercentValue(100f))

            // Income row
            val incomeLabel = Cell().add(Paragraph("Total Income").setBold())
            val incomeValue = Cell().add(Paragraph("${data.currencySymbol}${String.format("%.2f", data.totalIncome)}").setFontColor(ColorConstants.GREEN))
            summaryTable.addCell(incomeLabel)
            summaryTable.addCell(incomeValue)

            // Expense row
            val expenseLabel = Cell().add(Paragraph("Total Expense").setBold())
            val expenseValue = Cell().add(Paragraph("${data.currencySymbol}${String.format("%.2f", data.totalExpense)}").setFontColor(ColorConstants.RED))
            summaryTable.addCell(expenseLabel)
            summaryTable.addCell(expenseValue)

            // Balance row
            val balanceLabel = Cell().add(Paragraph("Balance").setBold())
            val balanceColor = if (data.balance >= 0) ColorConstants.GREEN else ColorConstants.RED
            val balanceValue = Cell().add(Paragraph("${data.currencySymbol}${String.format("%.2f", data.balance)}").setFontColor(balanceColor))
            summaryTable.addCell(balanceLabel)
            summaryTable.addCell(balanceValue)

            document.add(summaryTable)
            document.add(Paragraph(" ")) // Empty line
            document.add(Paragraph(" ")) // Empty line

            // Transactions Section
            val transactionsTitle = Paragraph("Transaction History")
                .setFontSize(16f)
                .setBold()
                .setFontColor(ColorConstants.DARK_GRAY)
            document.add(transactionsTitle)

            document.add(Paragraph(" "))

            // Transactions Table
            val transactionTable = Table(UnitValue.createPercentArray(floatArrayOf(20f, 25f, 35f, 20f)))
            transactionTable.setWidth(UnitValue.createPercentValue(100f))

            // Headers
            val dateHeader = Cell().add(Paragraph("Date").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
            val categoryHeader = Cell().add(Paragraph("Category").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
            val titleHeader = Cell().add(Paragraph("Title").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
            val amountHeader = Cell().add(Paragraph("Amount").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)

            transactionTable.addCell(dateHeader)
            transactionTable.addCell(categoryHeader)
            transactionTable.addCell(titleHeader)
            transactionTable.addCell(amountHeader)

            // Add transactions
            if (data.expenses.isNotEmpty()) {
                data.expenses.take(100).forEach { expense ->
                    transactionTable.addCell(expense.date)
                    transactionTable.addCell(expense.category)
                    transactionTable.addCell(expense.title)

                    val amountText = "${data.currencySymbol}${String.format("%.2f", expense.amount)}"
                    val amountCell = Cell().add(Paragraph(amountText))
                    if (expense.type == "Income") {
                        amountCell.setFontColor(ColorConstants.GREEN)
                    } else {
                        amountCell.setFontColor(ColorConstants.RED)
                    }
                    transactionTable.addCell(amountCell)
                }
            } else {
                val emptyCell = Cell(1, 4).add(Paragraph("No transactions found").setTextAlignment(TextAlignment.CENTER))
                transactionTable.addCell(emptyCell)
            }

            document.add(transactionTable)

            // Footer
            document.add(Paragraph(" "))
            val footer = Paragraph("Generated by CostKeeper App")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY)
            document.add(footer)

            document.close()
            pdf.close()

            // Get URI for sharing
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}