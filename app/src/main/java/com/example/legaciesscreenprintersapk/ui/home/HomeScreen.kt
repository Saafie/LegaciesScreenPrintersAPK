package com.example.legaciesscreenprintersapk.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legaciesscreenprintersapk.ui.auth.CardBackground
import com.example.legaciesscreenprintersapk.ui.auth.DarkBackground
import com.example.legaciesscreenprintersapk.ui.auth.PrimaryOrange
import com.example.legaciesscreenprintersapk.ui.auth.TextGray
import com.example.legaciesscreenprintersapk.ui.auth.TextWhite
import com.example.legaciesscreenprintersapk.ui.remote.ApiResult
import com.example.legaciesscreenprintersapk.ui.remote.OrderRepository
import com.example.legaciesscreenprintersapk.ui.remote.OrdersResult
import android.util.Log
import com.example.legaciesscreenprintersapk.ui.auth.ErrorRed
import kotlinx.coroutines.launch
import com.example.legaciesscreenprintersapk.ui.remote.models.OrderTrackingDto
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme
import com.example.legaciesscreenprintersapk.util.DateFormatUtils
import com.example.legaciesscreenprintersapk.util.SessionManager

@Composable
fun HomeScreen(
    onNavigateToOrder: () -> Unit,
    onNavigateToTrack: () -> Unit
) {
    var showOrderHistoryDialog by remember { mutableStateOf(false) }
    var showHelpSupportDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "CUSTOMER PORTAL",
                color = PrimaryOrange,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Welcome to Legacies Screen Printers Workshop Hub",
                color = TextGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Cards Grid (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Place New Order",
                    subtitle = "Custom Vinyl & Screen Printing",
                    icon = Icons.Default.AddCircle,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToOrder
                )
                QuickActionCard(
                    title = "Track Order Status",
                    subtitle = "Real-time 4-Stage Progress",
                    icon = Icons.Default.Search,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTrack
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Order History",
                    subtitle = "View Past Print Jobs",
                    icon = Icons.Default.History,
                    modifier = Modifier.weight(1f),
                    onClick = { showOrderHistoryDialog = true }
                )
                QuickActionCard(
                    title = "Help & Support",
                    subtitle = "User Guide & Workshop FAQ",
                    icon = Icons.Default.HelpOutline,
                    modifier = Modifier.weight(1f),
                    onClick = { showHelpSupportDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Workshop Status Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WORKSHOP STATUS & NOTICES",
                        color = PrimaryOrange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Standard Production Turnaround: 24-48 Hours\n" +
                                "• Express Vinyl Cutting available for walk-in counter clients\n" +
                                "• Workshop Pickup Counter Open: Mon-Fri 08:00 - 17:00",
                        color = TextWhite,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // Order History Dialog
    if (showOrderHistoryDialog) {
        OrderHistoryDialog(onDismiss = { showOrderHistoryDialog = false })
    }

    // Help & Support Dialog
    if (showHelpSupportDialog) {
        HelpSupportDialog(onDismiss = { showHelpSupportDialog = false })
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryOrange,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/** The signed-in customer's orders (from the app and the website). */
@Composable
fun OrderHistoryDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { OrderRepository(cache = SessionManager(context).ordersCache) }

    var result by remember { mutableStateOf<ApiResult<OrdersResult>?>(null) } // null = loading

    fun load() {
        result = null
        scope.launch {
            result = repository.getMyOrders()
            // A fixed label, not the class name: R8 renames classes in release builds.
            val outcome = when (val r = result) {
                is ApiResult.Success -> if (r.data.offlineSavedAtMillis != null) "offline copy (${r.data.orders.size})" else "${r.data.orders.size} order(s)"
                is ApiResult.HttpError -> "HTTP ${r.code}"
                is ApiResult.NetworkError -> "server unreachable"
                null -> "nothing"
            }
            Log.d("LSP/Order", "Order history loaded: $outcome")
        }
    }
    LaunchedEffect(Unit) { load() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text(text = "Past Order History", color = PrimaryOrange, fontWeight = FontWeight.Bold)
        },
        text = {
            when (val r = result) {
                null -> Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = PrimaryOrange) }

                is ApiResult.NetworkError, is ApiResult.HttpError -> {
                    val message = if (r is ApiResult.NetworkError) r.message else (r as ApiResult.HttpError).message
                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = message, color = ErrorRed, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = { load() }) { Text("Retry", color = PrimaryOrange) }
                    }
                }

                is ApiResult.Success -> Column {
                    r.data.offlineSavedAtMillis?.let { savedAt ->
                        OfflineBanner(savedAt)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (r.data.orders.isEmpty()) {
                        Text(
                            text = "You haven't placed any orders yet.",
                            color = TextGray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                            items(r.data.orders) { order -> OrderHistoryCard(order) }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE", color = PrimaryOrange, fontWeight = FontWeight.Bold)
            }
        }
    )
}

/** One order in the history list. */
@Composable
private fun OrderHistoryCard(order: OrderTrackingDto) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = order.orderRef, color = PrimaryOrange, fontWeight = FontWeight.Bold)
                Text(text = "R %.2f".format(order.totalAmount), color = TextWhite, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Client: ${order.clientName}", color = TextWhite, fontSize = 12.sp)
            Text(text = "Date: ${DateFormatUtils.formatOrderDate(order.createdAt)}", color = TextGray, fontSize = 11.sp)
            Text(text = order.status, color = PrimaryOrange, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

/** Banner shown when the list is the saved offline copy. */
@Composable
fun OfflineBanner(savedAtMillis: Long) {
    val savedAt = DateFormatUtils.formatOrderDate(java.time.Instant.ofEpochMilli(savedAtMillis).toString())
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3A2A12)), modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Offline - showing your orders as saved on $savedAt. Statuses may have changed.",
            color = Color(0xFFFFB74D),
            fontSize = 12.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun HelpSupportDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text(text = "Help & Support Guide", color = PrimaryOrange, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                Text(text = "Frequently Asked Questions", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "1. Walk-in vs Remote Orders", color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Remote orders can be submitted directly via this app with attached artwork file references. Walk-in jobs are verified at our main counter.",
                    color = TextWhite, fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "2. Order Stage Timeline", color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Track your order in real-time through 4 stages: Order Received -> Vinyl Cutting -> Screen Printing -> Ready for Collection.",
                    color = TextWhite, fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "3. Workshop Collection Guidelines", color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Please present your Order Reference ID (e.g. ORD5824) at the collection counter when picking up finished materials.",
                    color = TextWhite, fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Contact Support:", color = TextGray, fontSize = 11.sp)
                Text(text = "Phone: +27 11 800 1234 | Email: support@legacies.co.za", color = TextWhite, fontSize = 11.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "GOT IT", color = PrimaryOrange, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LegaciesScreenPrintersAPKTheme {
        HomeScreen(
            onNavigateToOrder = {},
            onNavigateToTrack = {}
        )
    }
}
