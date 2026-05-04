package com.kaede.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaede.app.util.MemoryLevel

/**
 * Performance Settings Screen - Allows users to tune AI performance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceSettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Settings state (would be loaded from preferences in real app)
    var contextSize by remember { mutableStateOf(2048) }
    var threadCount by remember { mutableStateOf(4) }
    var maxTokens by remember { mutableStateOf(128) }
    var enableBatteryOptimization by remember { mutableStateOf(true) }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Model Performance Section
            item {
                SettingsSection(
                    title = "Model Performance",
                    icon = Icons.Default.Speed
                ) {
                    // Context Size
                    SettingsSlider(
                        label = "Context Size",
                        value = contextSize.toFloat(),
                        onValueChange = { contextSize = it.toInt() },
                        valueRange = 512f..4096f,
                        steps = 7,
                        valueText = { "$contextSize tokens" },
                        description = "Larger context = more memory but better conversation memory"
                    )
                    
                    // Thread Count
                    SettingsSlider(
                        label = "CPU Threads",
                        value = threadCount.toFloat(),
                        onValueChange = { threadCount = it.toInt() },
                        valueRange = 1f..8f,
                        steps = 6,
                        valueText = { "$threadCount threads" },
                        description = "More threads = faster but more battery usage"
                    )
                    
                    // Max Tokens
                    SettingsSlider(
                        label = "Max Response Length",
                        value = maxTokens.toFloat(),
                        onValueChange = { maxTokens = it.toInt() },
                        valueRange = 32f..256f,
                        steps = 7,
                        valueText = { "$maxTokens tokens" },
                        description = "Maximum tokens per response"
                    )
                }
            }
            
            // Battery Section
            item {
                SettingsSection(
                    title = "Battery & Power",
                    icon = Icons.Default.BatteryStd
                ) {
                    SettingsSwitch(
                        label = "Battery Optimization",
                        description = "Reduce performance when battery is low",
                        checked = enableBatteryOptimization,
                        onCheckedChange = { enableBatteryOptimization = it }
                    )
                    
                    // Battery info card
                    BatteryInfoCard()
                }
            }
            
            // Memory Section
            item {
                SettingsSection(
                    title = "Memory Management",
                    icon = Icons.Default.Memory
                ) {
                    MemoryInfoCard()
                    
                    SettingsButton(
                        label = "Clear Memory Cache",
                        description = "Free up memory by clearing cached data",
                        icon = Icons.Default.CleaningServices,
                        onClick = { /* Clear cache action */ }
                    )
                }
            }
            
            // Advanced Settings
            item {
                SettingsSection(
                    title = "Advanced",
                    icon = Icons.Default.Tune,
                    isExpanded = showAdvancedSettings,
                    onExpandToggle = { showAdvancedSettings = !showAdvancedSettings }
                ) {
                    if (showAdvancedSettings) {
                        SettingsButton(
                            label = "Reset All Settings",
                            description = "Restore default performance settings",
                            icon = Icons.Default.Refresh,
                            onClick = { /* Reset action */ }
                        )
                        
                        SettingsButton(
                            label = "Export Performance Log",
                            description = "Save performance data for debugging",
                            icon = Icons.Default.Share,
                            onClick = { /* Export action */ }
                        )
                    }
                }
            }
            
            // Footer
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Changes take effect on next generation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * Settings Section - Collapsible section with title and content
 */
@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onExpandToggle: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Expand/collapse button
                if (onExpandToggle != null) {
                    IconButton(onClick = onExpandToggle) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

/**
 * Settings Slider - Slider with label and description
 */
@Composable
private fun SettingsSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueText: () -> String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = valueText(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Settings Switch - Toggle switch with label and description
 */
@Composable
private fun SettingsSwitch(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Settings Button - Clickable button with icon
 */
@Composable
private fun SettingsButton(
    label: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Battery Info Card - Shows current battery status
 */
@Composable
private fun BatteryInfoCard() {
    // In real app, this would observe BatteryOptimizer state
    val batteryLevel = 75
    val isCharging = false
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                batteryLevel <= 10 -> MaterialTheme.colorScheme.errorContainer
                batteryLevel <= 20 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCharging) Icons.Default.ChargingStation else Icons.Default.BatteryStd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(32.dp)
            )
            
            Column {
                Text(
                    text = "Battery: $batteryLevel%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = if (isCharging) "Charging" else "Not charging",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Memory Info Card - Shows current memory usage
 */
@Composable
private fun MemoryInfoCard() {
    // In real app, this would observe PerformanceMonitor state
    val memoryUsed = 850
    val memoryMax = 2048
    val memoryLevel = MemoryLevel.MEDIUM
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (memoryLevel) {
                MemoryLevel.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                MemoryLevel.HIGH -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Memory Usage",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$memoryUsed / $memoryMax MB",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            // Progress bar
            LinearProgressIndicator(
                progress = memoryUsed.toFloat() / memoryMax,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when (memoryLevel) {
                    MemoryLevel.CRITICAL -> MaterialTheme.colorScheme.error
                    MemoryLevel.HIGH -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Text(
                text = "Level: ${memoryLevel.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}
