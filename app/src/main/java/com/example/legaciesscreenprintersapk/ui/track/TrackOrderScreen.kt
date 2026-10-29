package com.example.legaciesscreenprintersapk.ui.track

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.legaciesscreenprintersapk.ui.remote.RetrofitClient
import com.example.legaciesscreenprintersapk.ui.remote.models.OrderResponse
import kotlinx.coroutines.launch

@Composable
fun TrackOrderScreen(modifier: Modifier = Modifier) {
    var trackingCodeInput by remember { mutableStateOf("") }
    var searchedOrder by remember { mutableStateOf<OrderResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Track Your Order",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = trackingCodeInput,
            onValueChange = { trackingCodeInput = it },
            label = { Text("Enter Tracking Code (e.g. LP-A1B2C3D4)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (trackingCodeInput.isNotBlank()) {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            val response = RetrofitClient.apiService.trackOrder(trackingCodeInput.trim())
                            if (response.isSuccessful && response.body() != null) {
                                searchedOrder = response.body()
                            } else {
                                searchedOrder = null
                                errorMessage = "Order not found. Please check your tracking number."
                            }
                        } catch (e: Exception) {
                            searchedOrder = null
                            errorMessage = "Network error: Unable to connect to backend API."
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && trackingCodeInput.isNotBlank()
        ) {
            Text(if (isLoading) "Searching..." else "Track Order")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMessage?.let { error ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        searchedOrder?.let { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tracking: ${order.trackingNumber}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Status: ${order.status}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = when (order.status.lowercase()) {
                            "completed", "delivered" -> Color(0xFF2E7D32)
                            "in production" -> Color(0xFFE65100)
                            else -> Color(0xFF1565C0)
                        }
                    )
                    Text(text = "Total: R${"%.2f".format(order.totalAmount)}")
                    if (!order.deliveryAddress.isNullOrBlank()) {
                        Text(text = "Delivery to: ${order.deliveryAddress}")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Items:", style = MaterialTheme.typography.titleSmall)

                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                        items(order.orderItems) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.quantity}x ${item.itemName} (${item.size ?: "Standard"})")
                                Text("R${"%.2f".format(item.unitPrice * item.quantity)}")
                            }
                        }
                    }
                }
            }
        }
    }
}