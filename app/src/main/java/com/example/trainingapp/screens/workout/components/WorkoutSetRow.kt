package com.example.trainingapp.screens.workout.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trainingapp.ui.theme.TextDark
import com.example.trainingapp.viewmodels.WorkoutSet

@Composable
fun WorkoutSetRow(
    setNumber: Int,
    set: WorkoutSet,
    onComplete: (Int, Float) -> Unit
) {
    var reps by remember { mutableStateOf(set.completedReps ?: set.targetReps) }
    var weight by remember { mutableStateOf(set.weight?.toString() ?: "0") }
    var isCompleted by remember { mutableStateOf(set.isCompleted) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Set $setNumber",
            modifier = Modifier.width(60.dp),
            fontWeight = FontWeight.Medium,
            color = TextDark
        )

        OutlinedTextField(
            value = reps.toString(),
            onValueChange = { reps = it.toIntOrNull() ?: 0 },
            label = { Text("Reps") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = isCompleted,
            onCheckedChange = {
                isCompleted = it
                if (it) {
                    onComplete(reps, weight.toFloatOrNull() ?: 0f)
                }
            }
        )
    }
}