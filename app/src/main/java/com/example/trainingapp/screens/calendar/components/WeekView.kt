package com.example.trainingapp.screens.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trainingapp.viewmodels.DaySchedule
import java.util.Calendar
import java.util.Date

@Composable
fun WeekView(
    weekSchedule: List<DaySchedule>,
    today: Date = Calendar.getInstance().time
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(weekSchedule) { daySchedule ->
            DayItem(
                daySchedule = daySchedule,
                isToday     = isSameDay(daySchedule.date, today)
            )
        }
    }
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val c1 = Calendar.getInstance().apply { time = date1 }
    val c2 = Calendar.getInstance().apply { time = date2 }
    return c1.get(Calendar.YEAR)        == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}