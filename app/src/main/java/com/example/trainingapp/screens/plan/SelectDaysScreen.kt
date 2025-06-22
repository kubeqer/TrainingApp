package com.example.trainingapp.screens.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.trainingapp.viewmodels.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDaysScreen(
    planViewModel: PlanViewModel,
    onNext: ()->Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Plan Name", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = planViewModel.planName,
            onValueChange = { planViewModel.updatePlanName(it) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        Text("Days per week", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
        days.forEachIndexed { idx, label ->
            val dayNum = idx+1
            val selected = planViewModel.selectedDays.contains(dayNum)
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(if (selected) Color(0xFFE0F7FA) else Color.White)
                    .clickable { planViewModel.toggleDay(dayNum) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label)
                Switch(
                    checked = selected,
                    onCheckedChange = { planViewModel.toggleDay(dayNum) }
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onNext,
            enabled = planViewModel.planName.isNotBlank() && planViewModel.selectedDays.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}
