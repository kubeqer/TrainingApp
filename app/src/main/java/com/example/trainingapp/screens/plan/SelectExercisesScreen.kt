package com.example.trainingapp.screens.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingapp.data.entity.Exercise
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.viewmodels.ExerciseViewModel
import com.example.trainingapp.viewmodels.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectExercisesScreen(
    allExercises: List<Exercise>,
    planViewModel: PlanViewModel,
    day: Int,
    onSave: () -> Unit
) {
    // 1) bodyParts z ExerciseViewModel (do filtra)
    val exerciseViewModel: ExerciseViewModel = viewModel()
    val bodyParts by exerciseViewModel.bodyParts.collectAsState()

    // lista partii z "All"
    val bodyPartsList = listOf(0L to "All") +
            bodyParts.entries.map { it.key to it.value }

    // stan zaznaczonej partii
    var selectedPart by remember { mutableStateOf(0L) }

    // dni do wyświetlenia (posortowane)
    val daysSorted = planViewModel.selectedDays.sorted()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── Filtr partii mięśni ───
        item {
            val rowState = rememberLazyListState()
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                LazyRow(
                    state = rowState,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bodyPartsList) { (partId, partName) ->
                        val isSelected = partId == selectedPart
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPart = partId },
                            label = { Text(partName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SportRed,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
                if (rowState.firstVisibleItemIndex > 0) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(16.dp)
                    )
                }
                val info = rowState.layoutInfo
                if (info.totalItemsCount > info.visibleItemsInfo.size + rowState.firstVisibleItemIndex) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(16.dp)
                    )
                }
            }
        }

        // ─── Ćwiczenia wg kolejnych dni ───
        daysSorted.forEach { d ->
            // Nagłówek dnia
            item {
                val label = listOf(
                    "", "Monday", "Tuesday", "Wednesday",
                    "Thursday", "Friday", "Saturday", "Sunday"
                ).getOrElse(d) { "" }
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Lista ćwiczeń dla dnia d
            item {
                val filtered = allExercises.filter {
                    selectedPart == 0L || it.bodyPartId == selectedPart
                }
                val vScroll = rememberScrollState()
                Box(
                    Modifier
                        .fillMaxWidth()
                        // pokaż ok. 3 wiersze
                        .height(56.dp * 3)
                ) {
                    Column(Modifier.verticalScroll(vScroll)) {
                        filtered.forEach { ex ->
                            // stan zaznaczeń z ViewModelu
                            val selList = planViewModel.exercisesByDay[d] ?: emptyList()
                            val checked = ex.id in selList

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { /* opcjonalnie: więcej akcji */ }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = {
                                        // toggle w ViewModelu
                                        planViewModel.toggleExercise(d, ex.id)
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(ex.name, Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Options",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Divider()
                        }
                    }
                    // strzałka dół, jeśli jest scroll
                    if (vScroll.value < vScroll.maxValue) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(16.dp)
                                .padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }

        // ─── Przycisk Save ───
        item {
            Button(
                onClick = onSave,
                // aktywny, gdy co najmniej jedno ćwiczenie wybrane
                enabled = planViewModel.exercisesByDay.values.any { it.isNotEmpty() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Save Plan")
            }
        }
    }
}
