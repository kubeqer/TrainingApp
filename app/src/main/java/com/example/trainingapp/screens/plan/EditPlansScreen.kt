package com.example.trainingapp.screens.plan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.example.trainingapp.data.model.WorkoutPlanModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlansScreen(
    planList: List<WorkoutPlanModel>,
    activePlanId: Long?,
    onSelect: (Long)->Unit,
    onDelete: (Long)->Unit,
    onEditClick: (Long)->Unit,
    onBack: ()->Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Workout Plans") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBackIos, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(planList, key = { it.id }) { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(plan.id) }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (plan.id == activePlanId),
                            onClick  = { onSelect(plan.id) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            plan.name,
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(onClick = { onEditClick(plan.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDelete(plan.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}
