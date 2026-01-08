package com.pepsigo.admin.screens.promotions



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.domainLayer.OfferUi
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@Composable
fun PromotionsScreen(
    viewModel: PromotionalOfferViewModel,
    onNavigateBack: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.promotions),
                icon = Icons.Default.Campaign,
                desc = "Promotions",
                onBackClick = onNavigateBack,
            )
        },
        snackbarHost = { },
        containerColor = inversePrimaryLight.copy(alpha = 0.35f)
    ) {innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ){
            //customer dropdown
            item {
                Text(
                    text = stringResource(id = R.string.customer_dropdown),

                    style = MaterialTheme.typography.bodyLarge
                )
                DropDown(
                    dropDown = state.customerDropDown,
                    error = false,
                    label = stringResource(id = R.string.select_customer),
                    selected = state.selectedCustomer,
                    onSelected = { viewModel.updateSelectedCustomer(it) },
                    labelExtractor = { it.name },
                    isCreatePurchase = true
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    OutlinedButton(
                        onClick = { viewModel.updateSelectedCustomer(null) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }
                    Spacer(modifier = Modifier.weight(0.1f))

                    Button(
                        onClick = { viewModel.getOffers(state.selectedCustomer?.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Get Offers")
                    }

                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item{
                Text("Offers for ${state.selectedCustomer?.name?:"N/A"}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier =Modifier.height(16.dp))
            }

            // 1️⃣ Loading state
            if (state.isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                return@LazyColumn   // ⬅ prevents rendering further items
            }

            // 2️⃣ No customer selected
            if (state.selectedCustomer == null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Text(
                            "Select a customer to view offers",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                return@LazyColumn
            }

            // 3️⃣ No offers found
            if (state.offerList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Text(
                            "No offers available for this customer",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                return@LazyColumn
            }

            // 4️⃣ Show offer cards
            items(state.offerList) { offer ->
                OfferCard(offer)

            }

            }
        }

    }

@Composable
fun OfferCard(offer: OfferUi) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder()
    ){
        Column( modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ){
            Text("${offer.itemName} \u2022 ${offer.itemQuantity} ${offer.unit}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Qty: ${offer.itemQuantity} \u2022  ",style = MaterialTheme.typography.bodyMedium)
                Text("Unit: ${offer.unit} \u2022  ",style = MaterialTheme.typography.bodyMedium)
                Text("Price: ${offer.itemPrice} ",style = MaterialTheme.typography.bodyMedium)
            }
            Row(modifier = Modifier.fillMaxWidth()){
                Text("Auto Add: ${if (offer.autoAdd) "Yes" else "No"} \u2022  ",
                    style = MaterialTheme.typography.bodyMedium)
                Text("Can Edit: ${if (offer.canEdit) "Yes" else "No"}",
                    style = MaterialTheme.typography.bodyMedium)

            }

        }

    }

}






