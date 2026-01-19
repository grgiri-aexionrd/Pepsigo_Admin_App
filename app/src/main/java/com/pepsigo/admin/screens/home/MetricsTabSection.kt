package com.pepsigo.admin.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.MetricCardState
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@Composable
fun MetricsTabsSection(
    salesMetrics: List<MetricCardState>,
    inventoryMetrics: List<MetricCardState>,
    usersMetrics: List<MetricCardState>
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Sales", "Inventory", "Users")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        PrimaryTabRow (
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(tabTitle,style = MaterialTheme.typography.bodyLarge,
                        ) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> HorizontalMetricCards(salesMetrics)
            1 -> HorizontalMetricCards(inventoryMetrics)
            2 -> HorizontalMetricCards(usersMetrics)
        }
    }
}

@Composable
fun HorizontalMetricCards(metrics: List<MetricCardState>) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(metrics) { item ->
            when (item) {
                is MetricCardState.Loading -> MetricCardLoading()
                is MetricCardState.Loaded -> MetricCard(item)
//                is MetricCardState.Error -> MetricCardError(item.message)
            }
        }
    }
}

@Composable
fun MetricCard(item: MetricCardState.Loaded) {
    Card(
        modifier = Modifier
            .width(144.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
//            containerColor = item.color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
//                tint = item.color,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = item.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MetricCardLoading() {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(130.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(0.6f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
            )

            Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.4f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}