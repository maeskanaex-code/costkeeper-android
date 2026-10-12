package com.mody.costkeeper.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.mody.costkeeper.ui.components.EditBudgetDialog
import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
import com.mody.costkeeper.data.local.ExpenseEntity
import com.mody.costkeeper.ui.components.*
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.utils.*
import kotlinx.coroutines.launch
import kotlin.math.*
import java.text.SimpleDateFormat
import java.util.*

/* ================= HELPER FUNCTIONS ================= */

fun loadBudget(context: Context): Double {
    val prefs = context.getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
    return prefs.getFloat("monthly_budget", 1000f).toDouble()
}

fun saveBudget(context: Context, budget: Double) {
    val prefs = context.getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
    prefs.edit().putFloat("monthly_budget", budget.toFloat()).apply()
}

fun loadCustomCategories(context: Context): List<String> {
    val prefs = context.getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
    val categoriesSet = prefs.getStringSet("custom_categories", emptySet())
    return categoriesSet?.toList()?.sorted() ?: emptyList()
}

fun saveCustomCategories(context: Context, categories: List<String>) {
    val prefs = context.getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
    prefs.edit().putStringSet("custom_categories", categories.toSet()).apply()
}

/* ================= SCREEN ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {

    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State variables
    var amount by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("EXPENSE") }
    var selectedSlice by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Currency
    var selectedCurrency by remember { mutableStateOf(CurrencyManager.getSelectedCurrency(context)) }
    var showCurrencySelector by remember { mutableStateOf(false) }

    // Search, Budget, Categories
    var searchQuery by remember { mutableStateOf("") }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var monthlyBudget by remember { mutableStateOf(loadBudget(context)) }
    var customCategories by remember { mutableStateOf(loadCustomCategories(context)) }
    var showCategoryManager by remember { mutableStateOf(false) }

    // Share Report
    var showShareReportDialog by remember { mutableStateOf(false) }
    var isGeneratingReport by remember { mutableStateOf(false) }

    // PDF Export
    var isGeneratingPDF by remember { mutableStateOf(false) }

    // Language
    val prefs = context.getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
    var selectedLanguage by remember { mutableStateOf(prefs.getString("app_language", "system") ?: "system") }

    // Get string resources
    val balanceText = stringResource(R.string.balance)
    val overviewText = stringResource(R.string.overview)
    val noDataText = stringResource(R.string.no_data)
    val amountText = stringResource(R.string.amount)
    val categoryText = stringResource(R.string.category)
    val saveText = stringResource(R.string.save)
    val expenseText = stringResource(R.string.expense)
    val incomeText = stringResource(R.string.income)
    val settingsText = stringResource(R.string.settings)
    val themeText = stringResource(R.string.theme)
    val languageText = stringResource(R.string.language_label)
    val currencyText = stringResource(R.string.currency)
    val categoriesText = stringResource(R.string.categories)
    val dataSectionText = stringResource(R.string.data_section)
    val exportCsvText = stringResource(R.string.export_csv)
    val exportExpensesText = stringResource(R.string.export_expenses)
    val dangerZoneText = stringResource(R.string.danger_zone)
    val deleteAllDataText = stringResource(R.string.delete_all_data)
    val aboutText = stringResource(R.string.about)
    val versionText = stringResource(R.string.version)
    val taglineText = stringResource(R.string.tagline)
    val closeText = stringResource(R.string.close)
    val cancelText = stringResource(R.string.cancel)
    val deleteEverythingText = stringResource(R.string.delete_everything)
    val deleteConfirmationTitleText = stringResource(R.string.delete_confirmation_title)
    val deleteConfirmationMessageText = stringResource(R.string.delete_confirmation_message)
    val shareReportText = stringResource(R.string.share_report)
    val exportToPdfText = stringResource(R.string.export_to_pdf)
    val generatingPdfText = stringResource(R.string.generating_pdf)
    val exportingText = stringResource(R.string.exporting)
    val systemDefaultText = stringResource(R.string.system_default)
    val customSelectedText = stringResource(R.string.custom_selected)
    val lightText = stringResource(R.string.light)
    val darkText = stringResource(R.string.dark)
    val pleaseEnterValidAmount = stringResource(R.string.please_enter_valid_amount)
    val noResultsText = stringResource(R.string.no_results)
    val noExpensesMatchSearchText = stringResource(R.string.no_expenses_match_search)

    // Category names
    val categoryFoodDining = stringResource(R.string.category_food_dining)
    val categoryTransportation = stringResource(R.string.category_transportation)
    val categoryShopping = stringResource(R.string.category_shopping)
    val categoryEntertainment = stringResource(R.string.category_entertainment)
    val categoryBillsUtilities = stringResource(R.string.category_bills_utilities)
    val categoryHealthcare = stringResource(R.string.category_healthcare)
    val categoryEducation = stringResource(R.string.category_education)
    val categoryRent = stringResource(R.string.category_rent)
    val categoryTravel = stringResource(R.string.category_travel)
    val categoryOther = stringResource(R.string.category_other)
    val categorySalary = stringResource(R.string.category_salary)
    val categoryOthersIncome = stringResource(R.string.category_others_income)

    val incomeCategories = listOf(categorySalary, categoryOthersIncome)

    val defaultExpenseCategories = listOf(
        categoryFoodDining, categoryTransportation, categoryShopping,
        categoryEntertainment, categoryBillsUtilities, categoryHealthcare,
        categoryEducation, categoryRent, categoryTravel, categoryOther
    )

    val allExpenseCategories = defaultExpenseCategories + customCategories

    // Initialize selected category based on mode
    LaunchedEffect(mode, incomeCategories, defaultExpenseCategories) {
        selectedCategory = if (mode == "INCOME") {
            incomeCategories.firstOrNull() ?: categorySalary
        } else {
            defaultExpenseCategories.firstOrNull() ?: categoryFoodDining
        }
    }

    // Calculate current month expenses for budget
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val currentMonthExpense = expenses
        .filter { it.amount < 0 }
        .filter { expense ->
            val expenseCalendar = Calendar.getInstance().apply { timeInMillis = expense.date }
            expenseCalendar.get(Calendar.MONTH) == currentMonth &&
                    expenseCalendar.get(Calendar.YEAR) == currentYear
        }
        .sumOf { abs(it.amount) }

    val income = expenses.filter { it.amount > 0 }.sumOf { it.amount }
    val expense = expenses.filter { it.amount < 0 }.sumOf { abs(it.amount) }
    val balance = income - expense

    val chartData = remember(expenses) {
        expenses
            .filter { it.amount < 0 }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { e -> abs(e.amount) } }
    }

    val pop by animateFloatAsState(
        targetValue = if (selectedSlice != null) 1f else 0f,
        animationSpec = tween(300),
        label = ""
    )

    val colors = listOf(
        Color(0xFF60A5FA),
        Color(0xFF34D399),
        Color(0xFFFBBF24),
        Color(0xFFFB7185),
        Color(0xFFA78BFA),
        Color(0xFF22D3EE),
        Color(0xFFF97316)
    )

    // Filter expenses based on search query
    val filteredExpenses = if (searchQuery.isEmpty()) expenses
    else expenses.filter {
        it.category.contains(searchQuery, ignoreCase = true) ||
                it.title.contains(searchQuery, ignoreCase = true)
    }

    // Light theme colors
    val lightBackground = Color(0xFFF8FAFC)
    val lightSurface = Color(0xFFFFFFFF)
    val lightTextPrimary = Color(0xFF1E293B)
    val lightTextSecondary = Color(0xFF475569)
    val lightDivider = Color(0xFFE2E8F0)

    Box(
        Modifier.fillMaxSize().background(if (isDarkTheme) Color(0xFF0F172A) else lightBackground)
    ) {

        LazyColumn(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.app_name),
                        color = if (isDarkTheme) Color.White else lightTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            Icons.Default.Settings,
                            null,
                            tint = if (isDarkTheme) Color.White else lightTextPrimary
                        )
                    }
                }
            }

            // Balance Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF2C4A6E) else Color(0xFF4A90C4)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(balanceText, color = Color(0xFFD4E4F5), fontSize = 14.sp)
                        Text(
                            text = "${selectedCurrency.symbol}${"%.2f".format(balance)}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Budget Card
            item {
                BudgetCard(
                    budget = monthlyBudget,
                    spent = currentMonthExpense,
                    onEditBudget = { showBudgetDialog = true },
                    isDarkTheme = isDarkTheme,
                    currency = selectedCurrency
                )
            }

            // Overview Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF1E293B) else lightSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 1.dp)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            overviewText,
                            color = if (isDarkTheme) Color.White else lightTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(10.dp))

                        if (chartData.isEmpty()) {
                            EmptyState(
                                title = noDataText,
                                message = stringResource(R.string.add_first_expense),
                                isDarkTheme = isDarkTheme
                            )
                        } else {
                            PieChart(
                                data = chartData,
                                selected = selectedSlice,
                                onSelect = { selectedSlice = it },
                                colors = colors,
                                pop = pop,
                                isDarkTheme = isDarkTheme,
                                currency = selectedCurrency
                            )
                            Spacer(Modifier.height(12.dp))
                            Legend(
                                data = chartData,
                                colors = colors,
                                selected = selectedSlice,
                                onSelect = { selectedSlice = it },
                                isDarkTheme = isDarkTheme,
                                currency = selectedCurrency
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    isDarkTheme = isDarkTheme
                )
            }

            // Toggle Mode
            item {
                ToggleMode(
                    mode = mode,
                    onChange = { mode = it },
                    isDarkTheme = isDarkTheme,
                    expenseText = expenseText,
                    incomeText = incomeText
                )
            }

            // Add Expense Form
            item {
                var expanded by remember { mutableStateOf(false) }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF1E293B) else lightSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 1.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                    amount = newValue
                                    showError = false
                                }
                            },
                            label = { Text(amountText) },
                            isError = showError,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = if (isDarkTheme) Color.White else lightTextPrimary,
                                unfocusedTextColor = if (isDarkTheme) Color.White else lightTextPrimary,
                                focusedLabelColor = if (isDarkTheme) Color.White else Color(0xFF3B82F6),
                                unfocusedLabelColor = if (isDarkTheme) Color(0xFFCBD5E1) else lightTextSecondary,
                                focusedBorderColor = if (showError) Color(0xFFF87171) else Color(0xFF3B82F6),
                                unfocusedBorderColor = if (showError) Color(0xFFF87171) else if (isDarkTheme) Color(0xFF334155) else lightDivider,
                                errorBorderColor = Color(0xFFF87171)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (showError) {
                            Text(
                                text = pleaseEnterValidAmount,
                                color = Color(0xFFF87171),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        DateSelectorButton(
                            selectedDate = selectedDate,
                            onClick = { showDatePicker = true },
                            isDarkTheme = isDarkTheme
                        )

                        Spacer(Modifier.height(10.dp))

                        val list = if (mode == "INCOME") incomeCategories else allExpenseCategories

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(categoryText) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = if (isDarkTheme) Color.White else lightTextPrimary,
                                    unfocusedTextColor = if (isDarkTheme) Color.White else lightTextPrimary,
                                    focusedLabelColor = if (isDarkTheme) Color.White else Color(0xFF3B82F6),
                                    unfocusedLabelColor = if (isDarkTheme) Color(0xFFCBD5E1) else lightTextSecondary,
                                    focusedBorderColor = Color(0xFF3B82F6),
                                    unfocusedBorderColor = if (isDarkTheme) Color(0xFF334155) else lightDivider
                                ),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(if (isDarkTheme) Color(0xFF1E293B) else lightSurface)
                            ) {
                                list.forEach { categoryItem ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                categoryItem,
                                                color = if (isDarkTheme) Color.White else lightTextPrimary
                                            )
                                        },
                                        onClick = {
                                            selectedCategory = categoryItem
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (amount.isBlank()) {
                                    showError = true
                                    return@Button
                                }
                                val v = amount.toDoubleOrNull()
                                if (v == null || v <= 0.0) {
                                    showError = true
                                    return@Button
                                }
                                showError = false
                                val final = if (mode == "EXPENSE") -v else v
                                val titleText = if (mode == "EXPENSE") expenseText else incomeText
                                viewModel.addExpenseWithDate(titleText, final, selectedCategory, selectedDate)
                                amount = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6)
                            )
                        ) {
                            Text(saveText, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Expense List
            if (filteredExpenses.isEmpty() && searchQuery.isNotEmpty()) {
                item {
                    EmptyState(
                        title = noResultsText,
                        message = noExpensesMatchSearchText,
                        isDarkTheme = isDarkTheme
                    )
                }
            } else if (filteredExpenses.isEmpty()) {
                item {
                    ExpensesEmptyState(isDarkTheme = isDarkTheme)
                }
            } else {
                items(filteredExpenses) { expenseItem ->
                    ExpenseItem(
                        exp = expenseItem,
                        onDelete = { viewModel.deleteExpense(expenseItem) },
                        onEdit = { selectedExpense = expenseItem },
                        isDarkTheme = isDarkTheme,
                        currency = selectedCurrency
                    )
                }
            }

            // Banner Ad
            item {
                YandexBannerAd(
                    modifier = Modifier.padding(8.dp),
                    adUnitId = "R-M-19253258-1"
                )
            }
        }

        // Settings Dialog
        if (showSettings) {
            SettingsDialog(
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                customCategories = customCategories,
                selectedCurrency = selectedCurrency,
                selectedLanguage = selectedLanguage,
                onLanguageChange = { newLanguage ->
                    selectedLanguage = newLanguage
                    prefs.edit().putString("app_language", newLanguage).apply()
                    (context as? androidx.activity.ComponentActivity)?.recreate()
                },
                onShowCurrencySelector = { showCurrencySelector = true },
                onShowShareReport = { showShareReportDialog = true },
                onExportToPDF = {
                    scope.launch {
                        isGeneratingPDF = true

                        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        val endDate = Date()
                        val startDate = Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000)
                        val dateRange = "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"

                        val expenseItems = expenses.map { expense ->
                            com.mody.costkeeper.utils.ExpenseItem(
                                date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(expense.date)),
                                category = expense.category,
                                title = expense.title,
                                amount = abs(expense.amount),
                                type = if (expense.amount > 0) incomeText else expenseText
                            )
                        }

                        val pdfData = PDFReportData(
                            totalIncome = income,
                            totalExpense = expense,
                            balance = balance,
                            expenses = expenseItems,
                            currencySymbol = selectedCurrency.symbol,
                            dateRange = dateRange
                        )

                        val uri = PDFGenerator.generatePDF(context, pdfData, isDarkTheme)
                        isGeneratingPDF = false

                        uri?.let {
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(android.content.Intent.EXTRA_STREAM, it)
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share PDF Report"))
                        }
                    }
                },
                isGeneratingPDF = isGeneratingPDF,
                onAddCategory = { newCategory ->
                    customCategories = (customCategories + newCategory).sorted()
                    saveCustomCategories(context, customCategories)
                },
                onDeleteCategory = { category ->
                    customCategories = customCategories.filter { it != category }
                    saveCustomCategories(context, customCategories)
                },
                onShowCategoryManager = { showCategoryManager = true },
                onDismiss = { showSettings = false },
                settingsText = settingsText,
                themeText = themeText,
                languageText = languageText,
                currencyText = currencyText,
                categoriesText = categoriesText,
                dataSectionText = dataSectionText,
                exportCsvText = exportCsvText,
                exportExpensesText = exportExpensesText,
                dangerZoneText = dangerZoneText,
                deleteAllDataText = deleteAllDataText,
                aboutText = aboutText,
                versionText = versionText,
                taglineText = taglineText,
                closeText = closeText,
                cancelText = cancelText,
                deleteEverythingText = deleteEverythingText,
                deleteConfirmationTitleText = deleteConfirmationTitleText,
                deleteConfirmationMessageText = deleteConfirmationMessageText,
                shareReportText = shareReportText,
                exportToPdfText = exportToPdfText,
                generatingPdfText = generatingPdfText,
                exportingText = exportingText,
                systemDefaultText = systemDefaultText,
                customSelectedText = customSelectedText,
                lightText = lightText,
                darkText = darkText
            )
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        CustomDatePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false },
            isDarkTheme = isDarkTheme
        )
    }

    // Edit Expense Dialog
    selectedExpense?.let { expense ->
        EditExpenseDialog(
            expense = expense,
            viewModel = viewModel,
            onDismiss = { selectedExpense = null },
            isDarkTheme = isDarkTheme,
            currency = selectedCurrency
        )
    }

    // Budget Dialog
    if (showBudgetDialog) {
        EditBudgetDialog(
            currentBudget = monthlyBudget,
            onBudgetSet = { newBudget ->
                monthlyBudget = newBudget
                saveBudget(context, newBudget)
            },
            onDismiss = { showBudgetDialog = false },
            isDarkTheme = isDarkTheme,
            currency = selectedCurrency
        )
    }

    // Category Manager Dialog
    if (showCategoryManager) {
        CategoryManagerDialog(
            customCategories = customCategories,
            onAddCategory = { newCategory ->
                customCategories = (customCategories + newCategory).sorted()
                saveCustomCategories(context, customCategories)
            },
            onDeleteCategory = { category ->
                customCategories = customCategories.filter { it != category }
                saveCustomCategories(context, customCategories)
            },
            onDismiss = { showCategoryManager = false },
            isDarkTheme = isDarkTheme
        )
    }

    // Currency Selector Dialog
    if (showCurrencySelector) {
        CurrencySelectorDialog(
            currentCurrency = selectedCurrency,
            onCurrencySelected = { newCurrency ->
                selectedCurrency = newCurrency
                CurrencyManager.saveSelectedCurrency(context, newCurrency)
            },
            onDismiss = { showCurrencySelector = false },
            isDarkTheme = isDarkTheme
        )
    }

    // Share Report Dialog
    if (showShareReportDialog) {
        ShareReportDialog(
            onShare = {
                scope.launch {
                    isGeneratingReport = true

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val endDate = Date()
                    val startDate = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
                    val dateRange = "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"

                    val topCategories = chartData.entries
                        .sortedByDescending { it.value }
                        .take(5)
                        .map { it.key to it.value }

                    val reportData = ExpenseReportData(
                        totalIncome = income,
                        totalExpense = expense,
                        balance = balance,
                        topCategories = topCategories,
                        weeklyData = emptyList(),
                        currencySymbol = selectedCurrency.symbol,
                        dateRange = dateRange
                    )

                    val bitmap = ReportGenerator.generateReportBitmap(
                        context = context,
                        data = reportData,
                        isDarkTheme = isDarkTheme
                    )

                    ReportGenerator.shareReport(context, bitmap)
                    isGeneratingReport = false
                    showShareReportDialog = false
                }
            },
            onDismiss = { showShareReportDialog = false },
            isGenerating = isGeneratingReport,
            isDarkTheme = isDarkTheme
        )
    }
}

/* ================= PIE CHART ================= */

@Composable
fun PieChart(
    data: Map<String, Double>,
    selected: String?,
    onSelect: (String?) -> Unit,
    colors: List<Color>,
    pop: Float,
    isDarkTheme: Boolean,
    currency: AppCurrency
) {
    val total = data.values.sum().coerceAtLeast(1.0)
    val slices = remember(data) {
        var start = -90f
        data.map {
            val sweep = (it.value / total * 360f).toFloat()
            Triple(it.key, start, sweep).also { start += sweep }
        }
    }

    Box(Modifier.size(220.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            val c = Offset(size.width / 2, size.height / 2)

            slices.forEachIndexed { i, (cat, start, sweep) ->
                val mid = Math.toRadians((start + sweep / 2).toDouble())
                val isSel = cat == selected
                val offset = if (isSel) 20f * pop else 0f

                drawArc(
                    color = colors[i % colors.size],
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(
                        c.x - r + cos(mid).toFloat() * offset,
                        c.y - r + sin(mid).toFloat() * offset
                    ),
                    size = Size(r * 2, r * 2),
                    alpha = if (selected == null || isSel) 1f else 0.4f
                )
            }
            drawCircle(
                color = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                radius = r * 0.45f
            )
        }
    }
}

/* ================= LEGEND ================= */

@Composable
fun Legend(
    data: Map<String, Double>,
    colors: List<Color>,
    selected: String?,
    onSelect: (String?) -> Unit,
    isDarkTheme: Boolean,
    currency: AppCurrency
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        data.entries.forEachIndexed { i, entry ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(entry.key) }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(10.dp).background(colors[i % colors.size], CircleShape))
                Spacer(Modifier.width(10.dp))
                Text(
                    entry.key,
                    color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${currency.symbol}${"%.1f".format(entry.value)}",
                    color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                )
            }
        }
    }
}

/* ================= EXPENSE ITEM ================= */

@Composable
fun ExpenseItem(
    exp: ExpenseEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isDarkTheme: Boolean,
    currency: AppCurrency
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onEdit() }
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                exp.category,
                modifier = Modifier.weight(1f),
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${currency.symbol}${"%.2f".format(abs(exp.amount))}",
                color = if (exp.amount >= 0) Color(0xFF4ADE80) else Color(0xFFF87171),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/* ================= TOGGLE MODE ================= */

@Composable
fun ToggleMode(
    mode: String,
    onChange: (String) -> Unit,
    isDarkTheme: Boolean,
    expenseText: String,
    incomeText: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                RoundedCornerShape(14.dp)
            )
            .padding(4.dp)
    ) {
        Box(
            Modifier.weight(1f).clickable { onChange("EXPENSE") }
                .background(
                    if (mode == "EXPENSE") Color(0xFF3B82F6) else Color.Transparent,
                    RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                expenseText,
                color = if (mode == "EXPENSE") Color.White else if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            Modifier.weight(1f).clickable { onChange("INCOME") }
                .background(
                    if (mode == "INCOME") Color(0xFF22C55E) else Color.Transparent,
                    RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                incomeText,
                color = if (mode == "INCOME") Color.White else if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ================= SETTINGS DIALOG ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    viewModel: ExpenseViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    customCategories: List<String>,
    selectedCurrency: AppCurrency,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    onShowCurrencySelector: () -> Unit,
    onShowShareReport: () -> Unit,
    onExportToPDF: () -> Unit,
    isGeneratingPDF: Boolean,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onShowCategoryManager: () -> Unit,
    onDismiss: () -> Unit,
    settingsText: String,
    themeText: String,
    languageText: String,
    currencyText: String,
    categoriesText: String,
    dataSectionText: String,
    exportCsvText: String,
    exportExpensesText: String,
    dangerZoneText: String,
    deleteAllDataText: String,
    aboutText: String,
    versionText: String,
    taglineText: String,
    closeText: String,
    cancelText: String,
    deleteEverythingText: String,
    deleteConfirmationTitleText: String,
    deleteConfirmationMessageText: String,
    shareReportText: String,
    exportToPdfText: String,
    generatingPdfText: String,
    exportingText: String,
    systemDefaultText: String,
    customSelectedText: String,
    lightText: String,
    darkText: String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
        titleContentColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
        textContentColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
        icon = {
            Icon(
                Icons.Default.Settings,
                null,
                tint = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6)
            )
        },
        title = { Text(settingsText, fontWeight = FontWeight.Bold) },
        text = {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Section
                Text(
                    themeText,
                    color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                ThemeToggle(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    lightText = lightText,
                    darkText = darkText
                )

                HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))

                // Language Section
                Text(
                    languageText,
                    color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageOption(
                        name = "English",
                        isSelected = selectedLanguage == "en",
                        onClick = { onLanguageChange("en") },
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f)
                    )
                    LanguageOption(
                        name = "Русский",
                        isSelected = selectedLanguage == "ru",
                        onClick = { onLanguageChange("ru") },
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f)
                    )
                    LanguageOption(
                        name = "中文",
                        isSelected = selectedLanguage == "zh",
                        onClick = { onLanguageChange("zh") },
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = if (selectedLanguage == "system") systemDefaultText else customSelectedText,
                    color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )

                HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))

                // Currency Section
                Text(
                    currencyText,
                    color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedButton(
                    onClick = onShowCurrencySelector,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${selectedCurrency.flag} ", fontSize = 16.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("${selectedCurrency.symbol} ${selectedCurrency.code}", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(8.dp))
                    Text("▼", fontSize = 12.sp, color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B))
                }

                HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))

                // Categories Section
                Text(
                    categoriesText,
                    color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedButton(
                    onClick = onShowCategoryManager,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Manage Custom Categories")
                }

                if (customCategories.isNotEmpty()) {
                    Text(
                        "${customCategories.size} custom ${if (customCategories.size == 1) "category" else "categories"} added",
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))

                // Data Section
                Text(
                    dataSectionText,
                    color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isExporting = true
                            val intent = viewModel.exportToCsv(context)
                            isExporting = false
                            intent?.let {
                                context.startActivity(android.content.Intent.createChooser(it, exportExpensesText))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isExporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF60A5FA),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(exportingText)
                    } else {
                        Icon(Icons.Default.Share, null)
                        Spacer(Modifier.width(8.dp))
                        Text(exportCsvText)
                    }
                }

                // Share Report as Image button
                OutlinedButton(
                    onClick = onShowShareReport,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, null)
                    Spacer(Modifier.width(8.dp))
                    Text(shareReportText)
                }

                // Export to PDF button
                OutlinedButton(
                    onClick = onExportToPDF,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGeneratingPDF
                ) {
                    if (isGeneratingPDF) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF60A5FA),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(generatingPdfText)
                    } else {
                        Icon(Icons.Default.Description, null)
                        Spacer(Modifier.width(8.dp))
                        Text(exportToPdfText)
                    }
                }

                HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))

                // Danger Zone
                Text(
                    dangerZoneText,
                    color = Color(0xFFF87171),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                    border = BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(deleteAllDataText)
                }

                HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))

                // About Section
                Text(
                    aboutText,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("CostKeeper", color = if (isDarkTheme) Color.White else Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                    Text(versionText, color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B), fontSize = 13.sp)
                    Text(
                        taglineText,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(closeText, color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6))
            }
        },
        dismissButton = null
    )

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
            titleContentColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
            textContentColor = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF64748B),
            icon = { Icon(Icons.Default.Warning, null, tint = Color(0xFFF87171)) },
            title = { Text(deleteConfirmationTitleText, fontWeight = FontWeight.Bold) },
            text = { Text(deleteConfirmationMessageText) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllExpenses()
                        showDeleteConfirmation = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(deleteEverythingText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(cancelText, color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B))
                }
            }
        )
    }
}

/* ================= LANGUAGE OPTION ================= */

@Composable
fun LanguageOption(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) {
            Color(0xFF3B82F6)
        } else {
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
        }
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.White else if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

/* ================= THEME TOGGLE ================= */

@Composable
fun ThemeToggle(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    lightText: String = "Light",
    darkText: String = "Dark"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ThemeOption(
            title = lightText,
            icon = Icons.Default.LightMode,
            isSelected = !isDarkTheme,
            onClick = { onThemeChange(false) },
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )

        ThemeOption(
            title = darkText,
            icon = Icons.Default.DarkMode,
            isSelected = isDarkTheme,
            onClick = { onThemeChange(true) },
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ThemeOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) {
            Color(0xFF3B82F6)
        } else {
            Color.Transparent
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) Color.White else if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 13.sp
            )
        }
    }
}

/* ================= EXPENSES EMPTY STATE ================= */

@Composable
fun ExpensesEmptyState(isDarkTheme: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💰",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_expenses_yet),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.add_first_expense),
            fontSize = 14.sp,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
    }
}