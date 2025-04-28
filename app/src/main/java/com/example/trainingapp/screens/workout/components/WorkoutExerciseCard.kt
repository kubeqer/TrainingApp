package com.example.trainingapp.screens.workout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainingapp.ui.theme.CardColor
import com.example.trainingapp.ui.theme.TextDark
import com.example.trainingapp.viewmodels.WorkoutSet

@Composable
fun WorkoutExerciseCard(
    exerciseName: String,
    sets: List<WorkoutSet>,
    onSetComplete: (Int, Int, Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = exerciseName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextDark
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            sets.forEachIndexed { index, set ->
                WorkoutSetRow(
                    setNumber = index + 1,
                    set = set,
                    onComplete = { reps, weight ->
                        onSetComplete(index, reps, weight)
                    }
                )
            }
        }
    }
}
