package com.pepsigo.admin.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.pepsigo.admin.constants.DateSelectionMode
import com.pepsigo.admin.model.PaymentUiModel
import com.pepsigo.admin.screens.commonComponents.ModalDatePicker
import com.pepsigo.admin.screens.commonComponents.SearchDropDown
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.toAppError


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentList(
    payments: LazyPagingItems<PaymentUiModel>,
    filterState: PaymentFilterState,
    modifier: Modifier = Modifier,
    onItemClick: (PaymentUiModel) -> Unit = {},
    onApplyFilter: (PaymentFilterState) -> Unit = {},
    onClearFilter: () -> Unit = {},
    onFetchUsers: () -> Unit = {}
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    // Calculate active filter count
    val activeFilterCount = listOfNotNull(
        filterState.date,
        filterState.transactionType,
        filterState.customerId?.toString()
    ).size

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        stickyHeader {
            PaymentListHeader(
                onFilterClick = { showFilterSheet = true },
                activeFilterCount = activeFilterCount
            )
        }
        items(count = payments.itemCount) { index ->
            val item = payments[index]
            if (item != null) {
                PaymentCard(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        // Loading states
        when (val state = payments.loadState.append) {
            is LoadState.Loading -> {
                item {
                    PaymentLoadingMoreItem()
                }
            }
            is LoadState.Error -> {
                val appError = state.error.toAppError()
                item {
                    PaymentLoadMoreRetry(
                        message = appError.userFriendlyMessage,
                        onRetry = { payments.retry() }
                    )
                }
            }
            else -> {}
        }

        // Handle initial empty state
        if (payments.loadState.refresh is LoadState.NotLoading && payments.itemCount == 0) {
            item { PaymentEmptyView() }
        }
    }

    if (showFilterSheet) {
        PaymentFilterBottomSheet(
            currentFilter = filterState,
            onDismiss = { showFilterSheet = false },
            onApplyFilter = { newFilter ->
                onApplyFilter(newFilter)
                showFilterSheet = false
            },
            onClearFilter = {
                onClearFilter()
                showFilterSheet = false
            },
            onFetchUsers = onFetchUsers
        )
    }
}

@Composable
fun PaymentListHeader(
    onFilterClick: () -> Unit,
    activeFilterCount: Int = 0
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Transactions",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
            Box(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                if (activeFilterCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .background(MaterialTheme.colorScheme.error, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeFilterCount.toString(),
                            color = MaterialTheme.colorScheme.onError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            style = LocalTextStyle.current.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    }
                }
            }
        }

    }
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFilterBottomSheet(
    currentFilter: PaymentFilterState,
    onDismiss: () -> Unit,
    onApplyFilter: (PaymentFilterState) -> Unit,
    onClearFilter: () -> Unit,
    onFetchUsers: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedDate by rememberSaveable { mutableStateOf(currentFilter.date) }
    var selectedTransactionType by rememberSaveable { mutableStateOf(currentFilter.transactionType) }
    var selectedCustomerId by rememberSaveable { mutableStateOf(currentFilter.customerId) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // User filter states
    var selectedUserType by rememberSaveable { mutableStateOf(currentFilter.selectedUserType ?: "customer") }
    var selectedUser by remember { mutableStateOf(currentFilter.selectedUser) }
    var userSearchQuery by rememberSaveable { mutableStateOf(currentFilter.selectedUser?.name ?: "") }

    // Track which filter section is expanded
    var expandedSection by rememberSaveable { mutableStateOf<String?>("date") }

    // Fetch users when user section is expanded
    LaunchedEffect(expandedSection) {
        if (expandedSection == "user" && currentFilter.customers.isEmpty() && currentFilter.vendors.isEmpty()) {
            onFetchUsers()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Filter Payments",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Two column layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left column - Clickable Labels
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.LightGray, Color.LightGray)
                            ),
                            alpha = 0.25f,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date section
                    FilterLabel(
                        text = "Date",
                        isSelected = expandedSection == "date",
                        hasValue = selectedDate != null,
                        onClick = { expandedSection = if (expandedSection == "date") null else "date" }
                    )

                    // Transaction Type section
                    FilterLabel(
                        text = "Txn Type",
                        isSelected = expandedSection == "transaction",
                        hasValue = selectedTransactionType != null,
                        onClick = { expandedSection = if (expandedSection == "transaction") null else "transaction" }
                    )

                    // User section (Customer/Vendor)
                    FilterLabel(
                        text = "User",
                        isSelected = expandedSection == "user",
                        hasValue = selectedUser != null,
                        onClick = { expandedSection = if (expandedSection == "user") null else "user" }
                    )
                }

                // Right column - Controls (shown based on expanded section)
                Column(
                    modifier = Modifier.weight(2f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (expandedSection) {
                        "date" -> {
                            // Date picker field
                            OutlinedTextField(
                                value = selectedDate ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Select Date") },
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = "Select Date"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true }
                            )
                            if (selectedDate != null) {
                                TextButton(onClick = { selectedDate = null }) {
                                    Text("Clear Date", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        "transaction" -> {
                            // Transaction Type radio buttons
                            Column {
                                TransactionTypeRadioOption(
                                    text = "Credit",
                                    selected = selectedTransactionType == "credit",
                                    onClick = { selectedTransactionType = "credit" }
                                )
                                TransactionTypeRadioOption(
                                    text = "Debit",
                                    selected = selectedTransactionType == "debit",
                                    onClick = { selectedTransactionType = "debit" }
                                )
                                if (selectedTransactionType != null) {
                                    TextButton(onClick = { selectedTransactionType = null }) {
                                        Text("Clear Selection", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                        "user" -> {
                            // User type selection row (Customer/Vendor)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                UserTypeRadioOption(
                                    text = "Customer",
                                    selected = selectedUserType == "customer",
                                    onClick = {
                                        selectedUserType = "customer"
                                        // Clear selection when switching type
                                        selectedUser = null
                                        userSearchQuery = ""
                                        selectedCustomerId = null
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                UserTypeRadioOption(
                                    text = "Vendor",
                                    selected = selectedUserType == "vendor",
                                    onClick = {
                                        selectedUserType = "vendor"
                                        // Clear selection when switching type
                                        selectedUser = null
                                        userSearchQuery = ""
                                        selectedCustomerId = null
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // Show loading or error state
                            if (currentFilter.isLoadingUsers) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(strokeWidth = 2.dp)
                                }
                            } else if (currentFilter.userError != null) {
                                Text(
                                    text = currentFilter.userError,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                // SearchDropDown based on selected user type
                                val userList = if (selectedUserType == "customer") {
                                    currentFilter.customers
                                } else {
                                    currentFilter.vendors
                                }

                                val filteredList = userList.filter {
                                    it.name.contains(userSearchQuery, ignoreCase = true)
                                }

                                SearchDropDown(
                                    filteredDropDown = filteredList,
                                    label = if (selectedUserType == "customer") "Search Customer" else "Search Vendor",
                                    searchQuery = userSearchQuery,
                                    selected = selectedUser,
                                    onSelected = { user ->
                                        selectedUser = user
                                        selectedCustomerId = user?.id
                                    },
                                    onSearchChange = { query ->
                                        userSearchQuery = query
                                    },
                                    labelExtractor = { it.name }
                                )
                            }

                            if (selectedUser != null) {
                                TextButton(onClick = {
                                    selectedUser = null
                                    userSearchQuery = ""
                                    selectedCustomerId = null
                                }) {
                                    Text("Clear User", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        null -> {
                            // No section selected - show hint
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "← Select a filter option",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onClearFilter,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
                Button(
                    onClick = {
                        onApplyFilter(
                            PaymentFilterState(
                                date = selectedDate,
                                transactionType = selectedTransactionType,
                                customerId = selectedCustomerId,
                                customers = currentFilter.customers,
                                vendors = currentFilter.vendors,
                                selectedUser = selectedUser,
                                selectedUserType = selectedUserType
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply Filter")
                }
            }
        }
    }

    if (showDatePicker) {
        ModalDatePicker(
            onDateSelected = { date ->
                selectedDate = date
            },
            onDismiss = { showDatePicker = false },
            mode = DateSelectionMode.PAST_OR_TODAY
        )
    }
}

@Composable
fun FilterLabel(
    text: String,
    isSelected: Boolean,
    hasValue: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        hasValue -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        hasValue -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected || hasValue) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TransactionTypeRadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(Modifier.width(8.dp))
        Text(text = text)
    }
}

@Composable
fun UserTypeRadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, Color.Gray) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun PaymentCard(item: PaymentUiModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Top row: Customer name + Transaction type & Payment method chips side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                ) {
                    Text(
                        text = item.customer?.businessName ?: item.customer?.name ?: "Unknown Customer",
                        fontWeight = FontWeight.SemiBold
                    )
                    if (item.sale != null) {
                        Text(
                            text = item.sale.invoiceNumber,
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PaymentTransactionChip(transactionType = item.transactionType)
                    PaymentMethodChip(paymentMethod = item.paymentMethod)
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // Bottom row: Amount details with Ref number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PaymentInfoRow(label = "Amount", value = item.amount)
                PaymentInfoRow(label = "Ref Num", value = item.refNumber)
                PaymentInfoRow(label = "Received By", value = item.receivedBy?.name?:"N/A")
            }
        }
    }
}

@Composable
fun PaymentInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier.wrapContentSize(),
    ) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun PaymentTransactionChip(transactionType: String) {
    val chipColor = when (transactionType.lowercase()) {
        "credit" -> Color(0xFF4CAF50) // Green
        "debit" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .background(chipColor.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = transactionType,
            color = chipColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PaymentMethodChip(paymentMethod: String) {
    val chipColor = when (paymentMethod.lowercase()) {
        "cash" -> Color(0xFF2196F3) // Blue
        "upi" -> Color(0xFF9C27B0) // Purple
        "card" -> Color(0xFFFF9800) // Orange
        "cheque" -> Color(0xFF795548) // Brown
        "net_banking" -> Color(0xFF00BCD4) // Cyan
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .background(chipColor.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = paymentMethod.replace("_", " "),
            color = chipColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PaymentLoadMoreRetry(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Color.Red, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun PaymentLoadingMoreItem() {
    Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
fun PaymentEmptyView() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("No payments found")
    }
}
