package com.mody.costkeeper.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class ExpenseReportData(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val topCategories: List<Pair<String, Double>>,
    val weeklyData: List<Pair<String, Double>>,
    val currencySymbol: String,
    val dateRange: String
)

object ReportGenerator {

    fun generateReportBitmap(
        context: Context,
        data: ExpenseReportData,
        isDarkTheme: Boolean
    ): Bitmap {
        val width = 1080
        val height = 1600  // Reduced height for better fit

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val backgroundColor = if (isDarkTheme) Color.parseColor("#0F172A") else Color.parseColor("#F8FAFC")
        canvas.drawColor(backgroundColor)

        var yOffset = 80f

        // App Title
        val titlePaint = Paint().apply {
            color = if (isDarkTheme) Color.WHITE else Color.parseColor("#1E293B")
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("CostKeeper", width / 2f, yOffset, titlePaint)
        yOffset += 50f

        // Subtitle
        val subtitlePaint = Paint().apply {
            color = if (isDarkTheme) Color.parseColor("#94A3B8") else Color.parseColor("#64748B")
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Expense Report", width / 2f, yOffset, subtitlePaint)
        yOffset += 40f

        // Date range
        val datePaint = Paint().apply {
            color = if (isDarkTheme) Color.parseColor("#94A3B8") else Color.parseColor("#64748B")
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(data.dateRange, width / 2f, yOffset, datePaint)
        yOffset += 60f

        // Divider
        val dividerPaint = Paint().apply {
            color = if (isDarkTheme) Color.parseColor("#334155") else Color.parseColor("#E2E8F0")
            strokeWidth = 2f
        }
        canvas.drawLine(80f, yOffset, width - 80f, yOffset, dividerPaint)
        yOffset += 50f

        // Balance Card
        val cardPaint = Paint().apply {
            color = if (isDarkTheme) Color.parseColor("#2C4A6E") else Color.parseColor("#4A90C4")
        }
        canvas.drawRoundRect(60f, yOffset, width - 60f, yOffset + 160f, 24f, 24f, cardPaint)

        val balanceLabelPaint = Paint().apply {
            color = Color.parseColor("#D4E4F5")
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Total Balance", width / 2f, yOffset + 55f, balanceLabelPaint)

        val balanceValuePaint = Paint().apply {
            color = Color.WHITE
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "${data.currencySymbol}${String.format("%.2f", data.balance)}",
            width / 2f,
            yOffset + 120f,
            balanceValuePaint
        )
        yOffset += 200f

        // Income vs Expense Row
        val rowHeight = 120f

        // Income box
        canvas.drawRoundRect(60f, yOffset, width / 2f - 20f, yOffset + rowHeight, 20f, 20f,
            Paint().apply { color = if (isDarkTheme) Color.parseColor("#1E293B") else Color.WHITE })

        val incomePaint = Paint().apply {
            color = Color.parseColor("#4ADE80")
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Income", width / 2f - 150f, yOffset + 40f, incomePaint)

        val incomeValuePaint = Paint().apply {
            color = Color.parseColor("#4ADE80")
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "${data.currencySymbol}${String.format("%.2f", data.totalIncome)}",
            width / 2f - 150f,
            yOffset + 85f,
            incomeValuePaint
        )

        // Expense box
        canvas.drawRoundRect(width / 2f + 20f, yOffset, width - 60f, yOffset + rowHeight, 20f, 20f,
            Paint().apply { color = if (isDarkTheme) Color.parseColor("#1E293B") else Color.WHITE })

        val expensePaint = Paint().apply {
            color = Color.parseColor("#E8913E")
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Expense", width / 2f + 150f, yOffset + 40f, expensePaint)

        val expenseValuePaint = Paint().apply {
            color = Color.parseColor("#E8913E")
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "${data.currencySymbol}${String.format("%.2f", data.totalExpense)}",
            width / 2f + 150f,
            yOffset + 85f,
            expenseValuePaint
        )
        yOffset += rowHeight + 30f

        // Top Categories Section
        val sectionTitlePaint = Paint().apply {
            color = if (isDarkTheme) Color.WHITE else Color.parseColor("#1E293B")
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Top Spending Categories", 60f, yOffset, sectionTitlePaint)
        yOffset += 50f

        if (data.topCategories.isNotEmpty()) {
            data.topCategories.take(5).forEachIndexed { index, (category, amount) ->
                val percent = if (data.totalExpense > 0) (amount / data.totalExpense * 100).toFloat() else 0f

                // Category name
                val categoryPaint = Paint().apply {
                    color = if (isDarkTheme) Color.WHITE else Color.parseColor("#1E293B")
                    textSize = 26f
                }
                canvas.drawText("${index + 1}. $category", 60f, yOffset, categoryPaint)

                // Amount
                val amountPaint = Paint().apply {
                    color = Color.parseColor("#E8913E")
                    textSize = 26f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.RIGHT
                }
                canvas.drawText(
                    "${data.currencySymbol}${String.format("%.2f", amount)}",
                    width - 60f,
                    yOffset,
                    amountPaint
                )

                // Progress bar background
                val progressBgPaint = Paint().apply {
                    color = if (isDarkTheme) Color.parseColor("#334155") else Color.parseColor("#E2E8F0")
                }
                canvas.drawRoundRect(60f, yOffset + 12f, width - 60f, yOffset + 32f, 12f, 12f, progressBgPaint)

                // Progress bar fill
                val progressFillPaint = Paint().apply {
                    color = Color.parseColor("#E8913E")
                }
                val progressWidth = (width - 120f) * (percent / 100f)
                canvas.drawRoundRect(60f, yOffset + 12f, 60f + progressWidth, yOffset + 32f, 12f, 12f, progressFillPaint)

                // Percentage
                val percentPaint = Paint().apply {
                    color = if (isDarkTheme) Color.parseColor("#94A3B8") else Color.parseColor("#64748B")
                    textSize = 22f
                    textAlign = Paint.Align.RIGHT
                }
                canvas.drawText(String.format("%.1f%%", percent), width - 60f, yOffset + 30f, percentPaint)

                yOffset += 55f

                if (yOffset > height - 120f) {
                    yOffset = height - 100f
                    return@forEachIndexed
                }
            }
        } else {
            val noDataPaint = Paint().apply {
                color = if (isDarkTheme) Color.parseColor("#94A3B8") else Color.parseColor("#64748B")
                textSize = 26f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("No expense data available", width / 2f, yOffset, noDataPaint)
            yOffset += 60f
        }

        // Footer
        yOffset = height - 60f
        val footerPaint = Paint().apply {
            color = if (isDarkTheme) Color.parseColor("#64748B") else Color.parseColor("#94A3B8")
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Generated by CostKeeper", width / 2f, yOffset, footerPaint)

        return bitmap
    }

    suspend fun shareReport(
        context: Context,
        bitmap: Bitmap,
        fileName: String = "costkeeper_report_${System.currentTimeMillis()}.png"
    ) {
        try {
            val cachePath = File(context.cacheDir, "images")
            if (!cachePath.exists()) {
                cachePath.mkdirs()
            }

            val file = File(cachePath, fileName)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Expense Report"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}