package com.pepsigo.admin.screens.specialPrices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialPricesTopAppBar(title: String = "SpecialPrices") {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
        },
//        elevation = 4.dp
    )
}

@Composable
fun SpecialPricesScreen( navController: NavController) {
    Scaffold(
        topBar = { SpecialPricesTopAppBar() }
    ) { innerPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
//                elevation =
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Special Prices", style = MaterialTheme.typography.titleLarge)

                }
            }
        }
    }
}