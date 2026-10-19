package com.example.legaciesscreenprintersapk.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.legaciesscreenprintersapk.ui.home.HomeScreen
import com.example.legaciesscreenprintersapk.ui.order.OrderPlacementScreen
import com.example.legaciesscreenprintersapk.ui.settings.SettingsScreen
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme
import com.example.legaciesscreenprintersapk.ui.track.TrackOrderScreen

enum class BottomTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    ORDER("Order", Icons.Default.ShoppingCart),
    TRACK("Track", Icons.Default.LocalShipping),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun MainScreen(
    isDarkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    onLogout: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var trackSearchQuery by remember { mutableStateOf("ORD5824") }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                BottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = (selectedTab == tab),
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(text = tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = Color.Transparent
        ) {
            when (selectedTab) {
                BottomTab.HOME -> HomeScreen(
                    onNavigateToOrder = { selectedTab = BottomTab.ORDER },
                    onNavigateToTrack = { selectedTab = BottomTab.TRACK },
                )
                BottomTab.ORDER -> OrderPlacementScreen { orderId ->
                    trackSearchQuery = orderId
                    selectedTab = BottomTab.TRACK
                }
                BottomTab.TRACK -> TrackOrderScreen(
                    initialSearchQuery = trackSearchQuery
                )
                BottomTab.SETTINGS -> SettingsScreen(
                    isDarkMode = isDarkMode,
                    onDarkModeChange = onDarkModeChange,
                    onLogoutClicked = onLogout
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LegaciesScreenPrintersAPKTheme {
        MainScreen(isDarkMode = false, onLogout = {})
    }
}
