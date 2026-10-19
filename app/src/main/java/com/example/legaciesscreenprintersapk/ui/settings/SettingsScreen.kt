package com.example.legaciesscreenprintersapk.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DarkBackground = Color(0xFF121212)
val CardBackground = Color(0xFF1E1E1E)
val PrimaryOrange = Color(0xFFFF6F00)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFA0A0A0)

@Composable
fun SettingsScreen(
    onLogoutClicked: () -> Unit,
) {
    var pushNotifications by remember { mutableStateOf(value = true) }
    var emailAlerts by remember { mutableStateOf(value = true) }
    var darkMode by remember { mutableStateOf(value = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Text(
                text = "APP SETTINGS",
                color = PrimaryOrange,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Manage your preferences and workshop configurations",
                color = TextGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Group Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Notifications & Alerts",
                        color = PrimaryOrange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Push Notification Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Order Status Push Notifications", color = TextWhite, fontSize = 14.sp)
                        Switch(
                            checked = pushNotifications,
                            onCheckedChange = { pushNotifications = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Alerts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Low Stock Email Alerts", color = TextWhite, fontSize = 14.sp)
                        Switch(
                            checked = emailAlerts,
                            onCheckedChange = { emailAlerts = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // System Preferences for Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "System & Display",
                        color = PrimaryOrange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Dark Theme Mode", color = TextWhite, fontSize = 14.sp)
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { darkMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button for logging out
            Button(
                onClick = { onLogoutClicked() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = "SIGN OUT", color = TextWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}