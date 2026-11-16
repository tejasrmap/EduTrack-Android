package com.edutrack.gti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.gti.ui.components.ModernSessionCard
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.DateRangeFilter
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel

@Composable
fun SessionHistoryScreen(
    viewModel: EduTrackViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val filteredSessions by viewModel.filteredSessions.collectAsState()
    val allSessions by viewModel.sessionsWithSubject.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val selectedFilterId by viewModel.selectedSubjectFilterId.collectAsState()
    val selectedDateRange by viewModel.selectedDateRangeFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showDateFilterMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkAuroraBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // 1. Header with Title and Session Count Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History & Logs",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color.White
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ElectricIndigo.copy(alpha = 0.18f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricIndigo.copy(alpha = 0.35f))
            ) {
                Text(
                    text = "${filteredSessions.size} of ${allSessions.size} sessions",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElectricIndigo
                )
            }
        }

        // 2. Sleek, Spacious Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, MidnightBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = MidnightSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search by subject, topic, or notes...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        cursorBrush = SolidColor(ElectricIndigo),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.setSearchQuery("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 3. Compact Unified Filter Strip (Single Row)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Range Dropdown Filter Pill
            item {
                Box {
                    Surface(
                        modifier = Modifier.clickable { showDateFilterMenu = true },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedDateRange != DateRangeFilter.ALL_TIME) ElectricIndigo else MidnightSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedDateRange != DateRangeFilter.ALL_TIME) ElectricIndigo else MidnightBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = if (selectedDateRange != DateRangeFilter.ALL_TIME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedDateRange.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (selectedDateRange != DateRangeFilter.ALL_TIME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "▾",
                                fontSize = 10.sp,
                                color = if (selectedDateRange != DateRangeFilter.ALL_TIME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showDateFilterMenu,
                        onDismissRequest = { showDateFilterMenu = false },
                        modifier = Modifier.background(MidnightSurfaceVariant)
                    ) {
                        DateRangeFilter.entries.forEach { filter ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = filter.label,
                                        fontWeight = if (selectedDateRange == filter) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedDateRange == filter) ElectricIndigo else Color.White
                                    )
                                },
                                onClick = {
                                    viewModel.setDateRangeFilter(filter)
                                    showDateFilterMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Divider Dot
            item {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MidnightBorder)
                )
            }

            // "All" Subject Filter
            item {
                val isSelected = selectedFilterId == null
                Surface(
                    modifier = Modifier.clickable { viewModel.setSubjectFilter(null) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) ElectricIndigo else MidnightSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) ElectricIndigo else MidnightBorder
                    )
                ) {
                    Text(
                        text = "All Subjects",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Individual Subject Chips
            items(subjects, key = { it.id }) { subject ->
                val isSelected = selectedFilterId == subject.id
                val color = parseHexColor(subject.colorHex)
                Surface(
                    modifier = Modifier.clickable {
                        viewModel.setSubjectFilter(if (selectedFilterId == subject.id) null else subject.id)
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) color.copy(alpha = 0.25f) else MidnightSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) color else MidnightBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Reset Button if filtered
            if (selectedFilterId != null || selectedDateRange != DateRangeFilter.ALL_TIME || searchQuery.isNotBlank()) {
                item {
                    TextButton(
                        onClick = {
                            viewModel.setSubjectFilter(null)
                            viewModel.setDateRangeFilter(DateRangeFilter.ALL_TIME)
                            viewModel.setSearchQuery("")
                        },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CrimsonRose
                        )
                    }
                }
            }
        }

        // 4. Clean Sessions List (Generous Content Padding)
        if (filteredSessions.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No study sessions found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing your search query or resetting filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredSessions, key = { it.session.id }) { sessionItem ->
                    ModernSessionCard(
                        sessionItem = sessionItem,
                        onClick = { onNavigateToDetail(sessionItem.session.id) }
                    )
                }
            }
        }
    }
}
