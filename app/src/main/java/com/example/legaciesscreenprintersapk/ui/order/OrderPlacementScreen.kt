package com.example.legaciesscreenprintersapk.ui.order

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legaciesscreenprintersapk.data.MockRepository
import com.example.legaciesscreenprintersapk.data.OrderItem
import com.example.legaciesscreenprintersapk.ui.auth.CardBackground
import com.example.legaciesscreenprintersapk.ui.auth.DarkBackground
import com.example.legaciesscreenprintersapk.ui.auth.ErrorRed
import com.example.legaciesscreenprintersapk.ui.auth.PrimaryOrange
import com.example.legaciesscreenprintersapk.ui.auth.TextGray
import com.example.legaciesscreenprintersapk.ui.auth.TextWhite
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme
import com.example.legaciesscreenprintersapk.util.SecurityUtils

data class DesignRowState(
    var designName: String = "",
    var selectedMaterialName: String = "10oz Vinyl (Black, Gold)",
    var quantityString: String = "1",
    var artworkUri: Uri? = null,
    var designNameError: String? = null,
    var quantityError: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderPlacementScreen(
    onOrderSubmittedSuccessfully: (String) -> Unit
) {
    var clientName by remember { mutableStateOf("Sipho Dlamini") }
    var clientPhone by remember { mutableStateOf("+27 82 123 4567") }
    var clientEmail by remember { mutableStateOf("sipho@example.com") }

    var clientNameError by remember { mutableStateOf<String?>(null) }
    var clientPhoneError by remember { mutableStateOf<String?>(null) }
    var clientEmailError by remember { mutableStateOf<String?>(null) }

    val materials = remember { MockRepository.standardMaterials }

    // Dynamic list of multi-design specification rows
    val designRows = remember {
        mutableStateListOf(
            DesignRowState(
                designName = "Legacies Custom Print",
                selectedMaterialName = "10oz Vinyl (Black, Gold)",
                quantityString = "2"
            )
        )
    }

    val context = LocalContext.current

    // Real-time calculation of running order total
    val runningTotal = remember(designRows) {
        derivedStateOf {
            designRows.sumOf { row ->
                val qty = row.quantityString.toIntOrNull() ?: 0
                val mat = materials.find { it.name == row.selectedMaterialName }
                val rate = mat?.unitPrice ?: 120.0
                qty * rate
            }
        }
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
            // Screen Header
            Text(
                text = "ORDER PLACEMENT",
                color = PrimaryOrange,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Submit Print & Vinyl Cutting Specifications",
                color = TextGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Customer Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customer Information",
                        color = PrimaryOrange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Full Name
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = {
                            clientName = it
                            if (clientNameError != null) clientNameError = null
                        },
                        label = { Text("Full Name", color = TextGray) },
                        singleLine = true,
                        isError = clientNameError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = TextGray,
                            errorBorderColor = ErrorRed,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    clientNameError?.let { Text(it, color = ErrorRed, fontSize = 11.sp) }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contact Phone
                    OutlinedTextField(
                        value = clientPhone,
                        onValueChange = {
                            clientPhone = it
                            if (clientPhoneError != null) clientPhoneError = null
                        },
                        label = { Text("Contact Number", color = TextGray) },
                        singleLine = true,
                        isError = clientPhoneError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = TextGray,
                            errorBorderColor = ErrorRed,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    clientPhoneError?.let { Text(it, color = ErrorRed, fontSize = 11.sp) }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Address
                    OutlinedTextField(
                        value = clientEmail,
                        onValueChange = {
                            clientEmail = it
                            if (clientEmailError != null) clientEmailError = null
                        },
                        label = { Text("Email Address", color = TextGray) },
                        singleLine = true,
                        isError = clientEmailError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = TextGray,
                            errorBorderColor = ErrorRed,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    clientEmailError?.let { Text(it, color = ErrorRed, fontSize = 11.sp) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Dynamic Order Specifications Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Order Specifications (Multi-Design)",
                        color = PrimaryOrange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    designRows.forEachIndexed { index, row ->
                        DesignSpecificationRowItem(
                            index = index,
                            rowState = row,
                            materials = materials.map { it.name },
                            getMaterialUnitPrice = { name -> materials.find { it.name == name }?.unitPrice ?: 120.0 },
                            onRowChange = { updated -> designRows[index] = updated },
                            onDeleteRow = if (designRows.size > 1) {
                                { designRows.removeAt(index) }
                            } else null
                        )
                        if (index < designRows.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = TextGray.copy(alpha = 0.3f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // "+ Add Another Design" button
                    OutlinedButton(
                        onClick = {
                            designRows.add(
                                DesignRowState(
                                    designName = "Design Specification ${designRows.size + 1}",
                                    selectedMaterialName = "10oz Vinyl (Black, Gold)",
                                    quantityString = "1"
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Design", tint = PrimaryOrange)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ ADD ANOTHER DESIGN", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Running Order Total & Submit Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Running Order Total:", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "R %.2f".format(runningTotal.value),
                            color = PrimaryOrange,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            clientNameError = null
                            clientPhoneError = null
                            clientEmailError = null

                            var hasError = false
                            if (clientName.isBlank()) {
                                clientNameError = "Client full name is required"
                                hasError = true
                            }
                            if (!SecurityUtils.isValidPhone(clientPhone)) {
                                clientPhoneError = "Valid contact phone is required"
                                hasError = true
                            }
                            if (!SecurityUtils.isValidEmail(clientEmail)) {
                                clientEmailError = "Valid email address is required"
                                hasError = true
                            }

                            // Validate each row
                            designRows.forEachIndexed { i, row ->
                                val qty = row.quantityString.toIntOrNull()
                                if (row.designName.isBlank()) {
                                    designRows[i] = row.copy(designNameError = "Design name required")
                                    hasError = true
                                }
                                if (qty == null || qty <= 0) {
                                    designRows[i] = row.copy(quantityError = "Quantity must be at least 1")
                                    hasError = true
                                }
                            }

                            if (!hasError) {
                                val items = designRows.mapIndexed { idx, row ->
                                    val qty = row.quantityString.toIntOrNull() ?: 1
                                    val unitPrice = materials.find { it.name == row.selectedMaterialName }?.unitPrice ?: 120.0
                                    OrderItem(
                                        id = "ITM_${System.currentTimeMillis()}_$idx",
                                        designName = row.designName,
                                        materialName = row.selectedMaterialName,
                                        artworkUri = row.artworkUri?.toString(),
                                        quantity = qty,
                                        unitPrice = unitPrice,
                                        totalPrice = qty * unitPrice
                                    )
                                }

                                val createdOrder = MockRepository.createOrder(
                                    clientName = clientName,
                                    clientPhone = clientPhone,
                                    clientEmail = clientEmail,
                                    items = items,
                                    isRemoteOrder = true
                                )

                                Toast.makeText(
                                    context,
                                    "Order Submitted Successfully! Reference: ${createdOrder.id}",
                                    Toast.LENGTH_LONG
                                ).show()

                                onOrderSubmittedSuccessfully(createdOrder.id)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "SUBMIT PRINT ORDER", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignSpecificationRowItem(
    index: Int,
    rowState: DesignRowState,
    materials: List<String>,
    getMaterialUnitPrice: (String) -> Double,
    onRowChange: (DesignRowState) -> Unit,
    onDeleteRow: (() -> Unit)?
) {
    var expandedMaterialDropdown by remember { mutableStateOf(false) }

    // Launcher for standard image/artwork file picker
    val artworkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onRowChange(rowState.copy(artworkUri = uri))
        }
    }

    val currentQty = rowState.quantityString.toIntOrNull() ?: 0
    val currentRate = getMaterialUnitPrice(rowState.selectedMaterialName)
    val rowSubtotal = currentQty * currentRate

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Design #${index + 1}", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (onDeleteRow != null) {
                IconButton(onClick = onDeleteRow) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Row", tint = ErrorRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Design Name Input
        OutlinedTextField(
            value = rowState.designName,
            onValueChange = {
                onRowChange(rowState.copy(designName = it, designNameError = null))
            },
            label = { Text("Design Name / Reference", color = TextGray) },
            singleLine = true,
            isError = rowState.designNameError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = TextGray,
                errorBorderColor = ErrorRed,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )
        rowState.designNameError?.let { Text(it, color = ErrorRed, fontSize = 11.sp) }

        Spacer(modifier = Modifier.height(8.dp))

        // Material Selection Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedMaterialDropdown,
            onExpandedChange = { expandedMaterialDropdown = !expandedMaterialDropdown },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = rowState.selectedMaterialName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Material Selection", color = TextGray) },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = PrimaryOrange)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = TextGray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedMaterialDropdown,
                onDismissRequest = { expandedMaterialDropdown = false },
                modifier = Modifier.background(CardBackground)
            ) {
                materials.forEach { matName ->
                    DropdownMenuItem(
                        text = { Text(text = matName, color = TextWhite) },
                        onClick = {
                            onRowChange(rowState.copy(selectedMaterialName = matName))
                            expandedMaterialDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quantity Input
            OutlinedTextField(
                value = rowState.quantityString,
                onValueChange = { input ->
                    // Accept only digits safely
                    if (input.all { it.isDigit() }) {
                        onRowChange(rowState.copy(quantityString = input, quantityError = null))
                    }
                },
                label = { Text("Quantity", color = TextGray) },
                singleLine = true,
                isError = rowState.quantityError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = TextGray,
                    errorBorderColor = ErrorRed,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                modifier = Modifier.weight(1f)
            )

            // Dynamic Subtotal Box
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(text = "Rate: R %.2f".format(currentRate), color = TextGray, fontSize = 12.sp)
                Text(text = "Subtotal: R %.2f".format(rowSubtotal), color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        rowState.quantityError?.let { Text(it, color = ErrorRed, fontSize = 11.sp) }

        Spacer(modifier = Modifier.height(8.dp))

        // Upload Artwork File Button
        OutlinedButton(
            onClick = { artworkPickerLauncher.launch("image/*") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.UploadFile, contentDescription = "Upload Artwork", tint = TextWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (rowState.artworkUri != null) "Artwork Uploaded: ${rowState.artworkUri?.lastPathSegment ?: "Selected"}" else "Upload Artwork File",
                color = TextWhite,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderPlacementScreenPreview() {
    LegaciesScreenPrintersAPKTheme {
        OrderPlacementScreen(onOrderSubmittedSuccessfully = {})
    }
}
