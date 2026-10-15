package com.mody.costkeeper.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
import com.mody.costkeeper.data.local.ExpenseEntity
import com.mody.costkeeper.ui.components.*
import com.mody.costkeeper.ui.theme.*
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.utils.*
import com.mody.costkeeper.util.BiometricHelper
import com.mody.costkeeper.util.SecurityManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/* ================= STORAGE HELPERS ================= */

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
    return prefs.getStringSet("custom_categories", emptySet())?.toList()?.sorted() ?: emptyList()
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

    var amount by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("EXPENSE") }
    var showSettings by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var selectedCurrency by remember { mutableStateOf(CurrencyManager.getSelectedCurrency(context)) }
    var showCurrencySelector by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var monthlyBudget by remember { mutableStateOf(loadBudget(context)) }
    var customCategories by remember { mutableStateOf(loadCustomCategories(context)) }
    var showCategoryManager by remember { mutableStateOf(false) }

    var showShareReportDialog by remember { mutableStateOf(false) }
    var isGeneratingReport by remember { mutableStateOf(false) }
    var isGeneratingPDF by remember { mutableStateOf(false) }

    val prefs = context.getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
    var selectedLanguage by remember { mutableStateOf(prefs.getString("app_language", "system") ?: "system") }

    // String resources
    val appNameText = stringResource(R.string.app_name)
    val balanceText = stringResource(R.string.balance)
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

    // Newly added localization
    val headerSubtitleText = stringResource(R.string.header_subtitle)
    val transactionsLabelText = stringResource(R.string.transactions_label)
    val noTransactionsYetText = stringResource(R.string.no_transactions_yet)
    val noTransactionsSubtitleText = stringResource(R.string.no_transactions_subtitle)
    val manageCategoriesText = stringResource(R.string.manage_categories)
    val deleteDescText = stringResource(R.string.delete_desc)
    val dialogOkText = stringResource(R.string.dialog_ok)

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

    LaunchedEffect(mode, incomeCategories, defaultExpenseCategories) {
        selectedCategory = if (mode == "INCOME") {
            incomeCategories.firstOrNull() ?: categorySalary
        } else {
            defaultExpenseCategories.firstOrNull() ?: categoryFoodDining
        }
    }

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

    val filteredExpenses = if (searchQuery.isEmpty()) expenses
    else expenses.filter {
        it.category.contains(searchQuery, ignoreCase = true) ||
                it.title.contains(searchQuery, ignoreCase = true)
    }

    val background = if (isDarkTheme) DarkBackground else LightBackground
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Box(
        Modifier
            .fillMaxSize()
            .background(background)
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp)
        ) {

            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = appNameText,
                            color = textPrimary,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = headerSubtitleText,
                            color = textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) DarkSurface else LightSurface)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = settingsText,
                            tint = PrimaryBlue
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = balanceText.uppercase(),
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "${selectedCurrency.symbol}${"%.2f".format(balance)}",
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(16.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Column {
                                Text(
                                    text = incomeText,
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = "${selectedCurrency.symbol}${"%.2f".format(income)}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    text = expenseText,
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = "${selectedCurrency.symbol}${"%.2f".format(expense)}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                BudgetCard(
                    budget = monthlyBudget,
                    spent = currentMonthExpense,
                    onEditBudget = { showBudgetDialog = true },
                    isDarkTheme = isDarkTheme,
                    currency = selectedCurrency
                )
            }

            item {
                ToggleMode(
                    mode = mode,
                    onChange = { mode = it },
                    isDarkTheme = isDarkTheme,
                    expenseText = expenseText,
                    incomeText = incomeText
                )
            }

            item {
                AddExpenseForm(
                    amount = amount,
                    onAmountChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amount = newValue
                            showError = false
                        }
                    },
                    showError = showError,
                    selectedDate = selectedDate,
                    onDateClick = { showDatePicker = true },
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    categories = if (mode == "INCOME") incomeCategories else allExpenseCategories,
                    mode = mode,
                    onSave = {
                        if (amount.isBlank()) {
                            showError = true
                            return@AddExpenseForm
                        }
                        val v = amount.toDoubleOrNull()
                        if (v == null || v <= 0.0) {
                            showError = true
                            return@AddExpenseForm
                        }
                        showError = false
                        val final = if (mode == "EXPENSE") -v else v
                        val titleText = if (mode == "EXPENSE") expenseText else incomeText
                        viewModel.addExpenseWithDate(titleText, final, selectedCategory, selectedDate)
                        amount = ""
                    },
                    isDarkTheme = isDarkTheme,
                    currency = selectedCurrency,
                    amountText = amountText,
                    categoryText = categoryText,
                    saveText = saveText,
                    errorText = pleaseEnterValidAmount
                )
            }

            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    isDarkTheme = isDarkTheme
                )
            }

            item {
                Text(
                    text = transactionsLabelText.uppercase(),
                    color = textMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )
            }

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
                    ExpensesEmptyState(
                        isDarkTheme = isDarkTheme,
                        title = noTransactionsYetText,
                        subtitle = noTransactionsSubtitleText
                    )
                }
            } else {
                items(filteredExpenses) { expenseItem ->
                    ExpenseItemRow(
                        expense = expenseItem,
                        onDelete = { viewModel.deleteExpense(expenseItem) },
                        onEdit = { selectedExpense = expenseItem },
                        isDarkTheme = isDarkTheme,
                        currency = selectedCurrency,
                        deleteDesc = deleteDescText
                    )
                }
            }

            item {
                YandexBannerAd(
                    modifier = Modifier.padding(top = 8.dp),
                    adUnitId = "R-M-19253258-1"
                )
            }
        }

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
                darkText = darkText,
                manageCategoriesText = manageCategoriesText
            )
        }
    }

    if (showDatePicker) {
        CustomDatePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false },
            isDarkTheme = isDarkTheme
        )
    }

    selectedExpense?.let { expense ->
        EditExpenseDialog(
            expense = expense,
            viewModel = viewModel,
            onDismiss = { selectedExpense = null },
            isDarkTheme = isDarkTheme,
            currency = selectedCurrency
        )
    }

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

    if (showShareReportDialog) {
        ShareReportDialog(
            onShare = {
                scope.launch {
                    isGeneratingReport = true
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val endDate = Date()
                    val startDate = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
                    val dateRange = "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"

                    val chartData = expenses
                        .filter { it.amount < 0 }
                        .groupBy { it.category }
                        .mapValues { it.value.sumOf { e -> abs(e.amount) } }

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

/* ================= ADD EXPENSE FORM ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseForm(
    amount: String,
    onAmountChange: (String) -> Unit,
    showError: Boolean,
    selectedDate: Long,
    onDateClick: () -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
    mode: String,
    onSave: () -> Unit,
    isDarkTheme: Boolean,
    currency: AppCurrency,
    amountText: String,
    categoryText: String,
    saveText: String,
    errorText: String
) {
    var expanded by remember { mutableStateOf(false) }

    val cardBg = if (isDarkTheme) DarkSurface else LightSurface
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted
    val inputBorder = if (isDarkTheme) BorderDark else LightBorder

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = amountText.uppercase(),
                color = textMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                placeholder = {
                    Text(
                        "0.00",
                        color = textMuted,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                leadingIcon = {
                    Text(
                        text = currency.symbol,
                        color = PrimaryBlue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                isError = showError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = inputBorder,
                    cursorColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (showError) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = errorText,
                    color = ExpenseRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            DateSelectorButton(
                selectedDate = selectedDate,
                onClick = onDateClick,
                isDarkTheme = isDarkTheme
            )

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(categoryText) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = inputBorder,
                        focusedLabelColor = PrimaryBlue,
                        unfocusedLabelColor = textMuted
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(cardBg)
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, color = textPrimary) },
                            onClick = {
                                onCategorySelected(cat)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mode == "INCOME") IncomeGreen else PrimaryBlue
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = saveText,
                    color = TextOnAccent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* ================= EXPENSE ITEM ROW ================= */

@Composable
fun ExpenseItemRow(
    expense: ExpenseEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isDarkTheme: Boolean,
    currency: AppCurrency,
    deleteDesc: String
) {
    val isIncome = expense.amount > 0

    val cardBg = if (isDarkTheme) DarkSurface else LightSurface
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    val categoryEmoji = when {
        expense.category.contains("Food", true) -> "🍔"
        expense.category.contains("Transport", true) -> "🚗"
        expense.category.contains("Shopping", true) -> "🛍️"
        expense.category.contains("Entertainment", true) -> "🎬"
        expense.category.contains("Bills", true) -> "💡"
        expense.category.contains("Health", true) -> "💊"
        expense.category.contains("Education", true) -> "📚"
        expense.category.contains("Rent", true) -> "🏠"
        expense.category.contains("Travel", true) -> "✈️"
        expense.category.contains("Salary", true) -> "💼"
        else -> "💵"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isIncome) IncomeGreen.copy(alpha = 0.15f)
                        else ExpenseRed.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(categoryEmoji, fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category,
                    color = textPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.date)),
                    color = textMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = "${if (isIncome) "+" else "-"}${currency.symbol}${"%.2f".format(abs(expense.amount))}",
                color = if (isIncome) IncomeGreen else ExpenseRed,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = deleteDesc,
                    tint = textMuted,
                    modifier = Modifier.size(18.dp)
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
    val containerBg = if (isDarkTheme) DarkSurfaceHighest else LightSurfaceHighest

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(containerBg)
            .padding(4.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(999.dp))
                .background(if (mode == "EXPENSE") ExpenseRed else Color.Transparent)
                .clickable { onChange("EXPENSE") }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                expenseText,
                color = if (mode == "EXPENSE") Color.White else if (isDarkTheme) TextMuted else LightTextMuted,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(999.dp))
                .background(if (mode == "INCOME") IncomeGreen else Color.Transparent)
                .clickable { onChange("INCOME") }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                incomeText,
                color = if (mode == "INCOME") Color.White else if (isDarkTheme) TextMuted else LightTextMuted,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ================= EMPTY STATE ================= */

@Composable
fun ExpensesEmptyState(
    isDarkTheme: Boolean,
    title: String,
    subtitle: String
) {
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(PrimaryBlueContainer.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Text("💰", fontSize = 44.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = textMuted,
            textAlign = TextAlign.Center
        )
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
    darkText: String,
    manageCategoriesText: String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var lockEnabled by remember { mutableStateOf(SecurityManager.isLockEnabled(context)) }
    var biometricEnabled by remember { mutableStateOf(SecurityManager.isBiometricEnabled(context)) }
    var autoLockMinutes by remember { mutableStateOf(SecurityManager.getAutoLockMinutes(context)) }
    var showPinSetup by remember { mutableStateOf(false) }
    val biometricAvailable = remember { BiometricHelper.canUseBiometric(context) }

    val surfaceBg = if (isDarkTheme) DarkSurfaceElevated else LightSurfaceElevated
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surfaceBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = settingsText,
                color = textPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionLabel(themeText, isDarkTheme)
                ThemeToggle(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    lightText = lightText,
                    darkText = darkText
                )
                HorizontalDivider(color = if (isDarkTheme) DividerDark else LightDivider)

                SectionLabel(stringResource(R.string.security_section), isDarkTheme)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.lock_enabled),
                            color = textPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = lockEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                showPinSetup = true
                            } else {
                                SecurityManager.clearPin(context)
                                lockEnabled = false
                                biometricEnabled = false
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextOnAccent,
                            checkedTrackColor = PrimaryBlue
                        )
                    )
                }

                if (lockEnabled && biometricAvailable) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.biometric_enabled),
                                color = textPrimary,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = { enabled ->
                                biometricEnabled = enabled
                                SecurityManager.setBiometricEnabled(context, enabled)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextOnAccent,
                                checkedTrackColor = PrimaryBlue
                            )
                        )
                    }
                }

                if (lockEnabled) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.auto_lock_label),
                            color = textMuted,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))

                        val options = listOf(0, 1, 5, 15, 30)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            options.forEach { minutes ->
                                val selected = autoLockMinutes == minutes
                                val label = if (minutes == 0) stringResource(R.string.auto_lock_never)
                                else "${minutes}m"

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            autoLockMinutes = minutes
                                            SecurityManager.setAutoLockMinutes(context, minutes)
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) PrimaryBlue
                                    else if (isDarkTheme) DarkSurfaceHighest else LightSurfaceHighest
                                ) {
                                    Text(
                                        text = label,
                                        color = if (selected) TextOnAccent else textMuted,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = if (isDarkTheme) DividerDark else LightDivider)

                SectionLabel(languageText, isDarkTheme)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageOption("English", selectedLanguage == "en", { onLanguageChange("en") }, isDarkTheme, Modifier.weight(1f))
                    LanguageOption("Русский", selectedLanguage == "ru", { onLanguageChange("ru") }, isDarkTheme, Modifier.weight(1f))
                    LanguageOption("中文", selectedLanguage == "zh", { onLanguageChange("zh") }, isDarkTheme, Modifier.weight(1f))
                }

                HorizontalDivider(color = if (isDarkTheme) DividerDark else LightDivider)

                SectionLabel(currencyText, isDarkTheme)

                OutlinedButton(
                    onClick = onShowCurrencySelector,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDarkTheme) BorderDark else LightBorder)
                ) {
                    Text("${selectedCurrency.flag} ", fontSize = 16.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${selectedCurrency.symbol} ${selectedCurrency.code}",
                        color = textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                HorizontalDivider(color = if (isDarkTheme) DividerDark else LightDivider)

                SectionLabel(categoriesText, isDarkTheme)

                OutlinedButton(
                    onClick = onShowCategoryManager,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDarkTheme) BorderDark else LightBorder)
                ) {
                    Icon(Icons.Default.Edit, null, tint = PrimaryBlue)
                    Spacer(Modifier.width(8.dp))
                    Text(manageCategoriesText, color = textPrimary)
                }

                HorizontalDivider(color = if (isDarkTheme) DividerDark else LightDivider)

                SectionLabel(dataSectionText, isDarkTheme)

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
                    enabled = !isExporting,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDarkTheme) BorderDark else LightBorder)
                ) {
                    Icon(Icons.Default.Share, null, tint = PrimaryBlue)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isExporting) exportingText else exportCsvText, color = textPrimary)
                }

                OutlinedButton(
                    onClick = onShowShareReport,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDarkTheme) BorderDark else LightBorder)
                ) {
                    Icon(Icons.Default.Image, null, tint = PrimaryBlue)
                    Spacer(Modifier.width(8.dp))
                    Text(shareReportText, color = textPrimary)
                }

                OutlinedButton(
                    onClick = onExportToPDF,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGeneratingPDF,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDarkTheme) BorderDark else LightBorder)
                ) {
                    if (isGeneratingPDF) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = PrimaryBlue,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(generatingPdfText, color = textPrimary)
                    } else {
                        Icon(Icons.Default.Description, null, tint = PrimaryBlue)
                        Spacer(Modifier.width(8.dp))
                        Text(exportToPdfText, color = textPrimary)
                    }
                }

                HorizontalDivider(color = if (isDarkTheme) DividerDark else LightDivider)

                SectionLabel(dangerZoneText, isDarkTheme, ExpenseRed)

                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed),
                    border = BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(deleteAllDataText)
                }

                HorizontalDivider(color = if (isDarkTheme) DividerDark else LightDivider)

                SectionLabel(aboutText, isDarkTheme)

                Column {
                    Text(
                        "CostKeeper",
                        color = textPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(versionText, color = textMuted, style = MaterialTheme.typography.bodySmall)
                    Text(taglineText, color = textMuted, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = closeText,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )

    if (showPinSetup) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showPinSetup = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkTheme) DarkBackground else LightBackground)
            ) {
                LockScreen(
                    isDarkTheme = isDarkTheme,
                    isSetupMode = true,
                    onSuccess = {
                        showPinSetup = false
                        lockEnabled = true
                        SecurityManager.setLockEnabled(context, true)
                        if (BiometricHelper.canUseBiometric(context)) {
                            biometricEnabled = true
                            SecurityManager.setBiometricEnabled(context, true)
                        }
                    },
                    onCancel = { showPinSetup = false }
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            containerColor = surfaceBg,
            shape = RoundedCornerShape(24.dp),
            icon = { Icon(Icons.Default.Warning, null, tint = ExpenseRed) },
            title = {
                Text(
                    deleteConfirmationTitleText,
                    color = textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(deleteConfirmationMessageText, color = textMuted)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllExpenses()
                        showDeleteConfirmation = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(deleteEverythingText, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(cancelText, color = textMuted)
                }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String, isDarkTheme: Boolean, color: Color? = null) {
    Text(
        text = text.uppercase(),
        color = color ?: if (isDarkTheme) TextMuted else LightTextMuted,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold
    )
}

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
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryBlue else if (isDarkTheme) DarkSurfaceHighest else LightSurfaceHighest
    ) {
        Text(
            text = name,
            color = if (isSelected) TextOnAccent else if (isDarkTheme) TextMuted else LightTextMuted,
            modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}