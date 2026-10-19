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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legaciesscreenprintersapk.data.MockRepository
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme

@Composable
fun HomeScreen(
    onNavigateToOrder: () -> Unit,
    onNavigateToTrack: () -> Unit,
) {
    var showOrderHistoryDialog by remember { mutableStateOf(value = false) }
    var showHelpSupportDialog by remember { mutableStateOf(value = false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                color = MaterialTheme.colorScheme.primary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Welcome to Legacies Screen Printers Workshop Hub",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    modifier = Modifier.weight(1f)
                ) {
                    showOrderHistoryDialog = true
                }
                QuickActionCard(
                    title = "Help & Support",
                    subtitle = "User Guide & Workshop FAQ",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    modifier = Modifier.weight(1f)
                ) {
                    showHelpSupportDialog = true
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Workshop Status Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WORKSHOP STATUS & NOTICES",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Standard Production Turnaround: 24-48 Hours\n" +
                                "• Express Vinyl Cutting available for walk-in counter clients\n" +
                                "• Workshop Pickup Counter Open: Mon-Fri 08:00 - 17:00",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // Order History Dialog
    if (showOrderHistoryDialog) {
        OrderHistoryDialog { showOrderHistoryDialog = false }
    }

    // Help & Support Dialog
    if (showHelpSupportDialog) {
        HelpSupportDialog { showHelpSupportDialog = false }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun OrderHistoryDialog(onDismiss: () -> Unit) {
    val orders = remember { MockRepository.getOrders() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(text = "Past Order History", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = order.id, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text(text = "R %.2f".format(order.totalAmount), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Client: ${order.clientName}", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                            Text(text = "Date: ${order.createdAt}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            Text(text = order.statusText, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun HelpSupportDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(text = "Help & Support Guide", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                Text(text = "Frequently Asked Questions", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "1. Walk-in vs Remote Orders", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Remote orders can be submitted directly via this app with attached artwork file references. Walk-in jobs are verified at our main counter.",
                    color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "2. Order Stage Timeline", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Track your order in real-time through 4 stages: Order Received -> Vinyl Cutting -> Screen Printing -> Ready for Collection.",
                    color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "3. Workshop Collection Guidelines", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Please present your Order Reference ID (e.g. ORD5824) at the collection counter when picking up finished materials.",
                    color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Contact Support:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(text = "Phone: +27 11 800 1234 | Email: support@legacies.co.za", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "GOT IT", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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
            onNavigateToTrack = {},
        )
    }
}
