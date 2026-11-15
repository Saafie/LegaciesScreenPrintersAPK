package com.example.legaciesscreenprintersapk.ui.track

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legaciesscreenprintersapk.data.TrackingStage
import com.example.legaciesscreenprintersapk.ui.auth.CardBackground
import com.example.legaciesscreenprintersapk.ui.auth.DarkBackground
import com.example.legaciesscreenprintersapk.ui.auth.PrimaryOrange
import com.example.legaciesscreenprintersapk.ui.auth.TextGray
import com.example.legaciesscreenprintersapk.ui.auth.TextWhite
import com.example.legaciesscreenprintersapk.ui.remote.ApiResult
import com.example.legaciesscreenprintersapk.ui.remote.OrderRepository
import android.util.Log
import com.example.legaciesscreenprintersapk.ui.remote.models.OrderTrackingDto
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme
import kotlinx.coroutines.launch
import com.example.legaciesscreenprintersapk.util.DateFormatUtils

/** Maps an order status to the timeline step (0-3). */
private const val TAG = "LSP/Track"

internal fun mapStatusToStageIndex(status: String): Int {
    when (status.trim()) {
        "Order Received & Verified" -> return 0
        "Vinyl Cutting & Screen Prep" -> return 1
        "Screen Printing" -> return 2
        "Ready for Collection", "Completed" -> return 3
    }
    // Fallback for older offline sample data such as "Stage 2: Vinyl Cutting & Screen Prep".
    val lower = status.lowercase()
    return when {
        lower.contains("stage 4") || lower.contains("collection") || lower.contains("completed") -> 3
        lower.contains("stage 3") || lower.contains("printing") -> 2
        lower.contains("stage 2") || lower.contains("cutting") || lower.contains("prep") -> 1
        else -> 0
    }
}

/** Track an order by its reference. */
@Composable
fun TrackOrderScreen(
    initialSearchQuery: String = ""
) {
    var searchQuery by remember { mutableStateOf(initialSearchQuery) }
    var searchedOrder by remember { mutableStateOf<OrderTrackingDto?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val repository = remember { OrderRepository() }

    /** Looks up [ref] on the API and updates the screen state. */
    suspend fun search(ref: String) {
        val reference = ref.trim().uppercase()
        if (!Regex("""^ORD\d{3,10}$""").matches(reference)) {
            searchedOrder = null
            errorMessage = "Enter an order reference like ORD123456."
            return
        }
        isSearching = true
        errorMessage = null
        Log.d(TAG, "Tracking $reference")
        when (val result = repository.trackOrder(reference)) {
            is ApiResult.Success -> {
                searchedOrder = result.data
                Log.i(TAG, "$reference is at: ${result.data.status}")
            }
            is ApiResult.HttpError -> {
                searchedOrder = null
                errorMessage = if (result.code == 404) "No order found with reference $reference." else result.message
            }
            is ApiResult.NetworkError -> {
                searchedOrder = null
                errorMessage = result.message
            }
        }
        isSearching = false
    }

    // Search straight away when opened with a reference (e.g. after placing an order).
    LaunchedEffect(initialSearchQuery) {
        if (initialSearchQuery.isNotBlank()) search(initialSearchQuery)
    }

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
                text = "LIVE ORDER TRACKING",
                color = PrimaryOrange,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Real-time Production Progress & Stage Timeline",
                color = TextGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Search Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Find Order Progress",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Order reference (e.g. ORD123456)", color = TextGray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                unfocusedBorderColor = TextGray,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { scope.launch { search(searchQuery) } },
                            enabled = !isSearching,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextWhite)
                            }
                        }
                    }

                    errorMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = msg, color = Color(0xFFCF6679), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Order Details & Timeline Section
            searchedOrder?.let { order ->
                OrderDetailsCard(order = order)

                Spacer(modifier = Modifier.height(16.dp))

                StatusBadgeCard(statusText = order.status, createdAt = order.createdAt)

                Spacer(modifier = Modifier.height(16.dp))

                FourStageTimelineCard(currentStageIndex = mapStatusToStageIndex(order.status))
            } ?: run {
                if (errorMessage == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Enter an Order Reference (e.g. ORD5824) or Phone Number above to track status.",
                                color = TextGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailsCard(order: OrderTrackingDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Order Ref: ${order.orderRef}", color = PrimaryOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = PrimaryOrange.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = when (order.orderType) {
                            "web" -> "Website Order"
                            "walk_in" -> "Walk-in Order"
                            else -> "App Order"
                        },
                        color = PrimaryOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = TextGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Client Name:", color = TextGray, fontSize = 13.sp)
                Text(text = order.clientName, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Contact Phone:", color = TextGray, fontSize = 13.sp)
                Text(text = order.contact, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = TextGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Job Summary Items:", color = PrimaryOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // weight(1f) stops long item names squashing the price.
                    Text(
                        text = "• ${item.designName} (${item.materialName ?: "Vinyl Substrate"}) x${item.quantity}",
                        color = TextWhite,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    Text(text = "R %.2f".format(item.subtotal), color = TextGray, fontSize = 12.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Order Value:", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "R %.2f".format(order.totalAmount), color = PrimaryOrange, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatusBadgeCard(statusText: String, createdAt: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CURRENT STATUS: ${statusText.uppercase()}",
                color = PrimaryOrange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Order Placed: ${DateFormatUtils.formatOrderDate(createdAt)}",
                color = TextWhite,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun FourStageTimelineCard(currentStageIndex: Int) {
    val stageDefinitions = listOf(
        Pair("Order Received & Verified", "Artwork and dimensions verified by prepress"),
        Pair("Vinyl Cutting & Screen Prep", "Precision plotter cutting vinyl & burning screens"),
        Pair("Screen Printing", "Multi-pass ink application & tunnel curing"),
        Pair("Ready for Collection", "Quality checked and packaged at workshop counter")
    )

    val stages = stageDefinitions.mapIndexed { index, (title, desc) ->
        TrackingStage(
            stageNumber = index + 1,
            title = title,
            description = desc,
            isCompleted = index <= currentStageIndex,
            isCurrent = index == currentStageIndex
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Production Stage Timeline",
                color = PrimaryOrange,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            stages.forEachIndexed { index, stage ->
                TimelineStageRowItem(
                    stage = stage,
                    isLast = index == stages.size - 1
                )
            }
        }
    }
}

@Composable
fun TimelineStageRowItem(
    stage: TrackingStage,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = when {
                            stage.isCurrent -> PrimaryOrange
                            stage.isCompleted -> Color(0xFF4CAF50)
                            else -> Color(0xFF333333)
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = if (stage.isCurrent) 2.dp else 0.dp,
                        color = TextWhite,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (stage.isCompleted && !stage.isCurrent) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = stage.stageNumber.toString(),
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(
                            color = if (stage.isCompleted) Color(0xFF4CAF50) else Color(0xFF333333)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(top = 2.dp)) {
            Text(
                text = "Stage ${stage.stageNumber}: ${stage.title}",
                color = if (stage.isCurrent) PrimaryOrange else if (stage.isCompleted) TextWhite else TextGray,
                fontSize = 14.sp,
                fontWeight = if (stage.isCurrent) FontWeight.Bold else FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stage.description,
                color = TextGray,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackOrderScreenPreview() {
    LegaciesScreenPrintersAPKTheme {
        TrackOrderScreen(initialSearchQuery = "ORD5824")
    }
}
