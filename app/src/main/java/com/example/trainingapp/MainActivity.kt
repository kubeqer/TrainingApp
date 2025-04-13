package com.example.trainingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingapp.data.database.WorkoutDatabase
import com.example.trainingapp.data.entity.BodyPart
import com.example.trainingapp.ui.theme.TrainingAppTheme

// Definiujemy kolory sportowe
private val SportRed = Color(0xFFE53935)
private val SportRedLight = Color(0xFFFF6F60)
private val SportRedDark = Color(0xFFAB000D)
private val BackgroundColor = Color(0xFFF5F5F5)
private val CardColor = Color.White
private val TextDark = Color(0xFF212121)
private val TextMedium = Color(0xFF666666)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        val database = WorkoutDatabase.getDatabase(applicationContext, lifecycleScope)

        setContent {
            TrainingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundColor
                ) {
                    TrainingDashboard(mainViewModel = ViewModelProvider(this)[MainViewModel::class.java])
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingDashboard(mainViewModel: MainViewModel = viewModel()) {
    // Fetch body parts from database using ViewModel
    val bodyParts by mainViewModel.bodyParts.collectAsState(initial = emptyList())

    // Load body parts when composable is first launched
    LaunchedEffect(key1 = Unit) {
        mainViewModel.loadBodyParts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FitStrong",
                        fontWeight = FontWeight.Bold,
                        color = SportRed,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(BackgroundColor),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Body parts categories (horizontal scroll)
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            ) {
                Text(
                    text = "MUSCLE GROUPS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMedium,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp, bottom = 8.dp)
                ) {
                    items(bodyParts) { bodyPart ->
                        BodyPartButton(bodyPart)
                    }
                }
            }

            // Workout actions section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMedium,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionButton(
                        title = "Create Plan",
                        icon = Icons.Rounded.Create,
                        modifier = Modifier.weight(1f),
                        onClick = { /* TODO: Open workout plan creation screen */ }
                    )

                    ActionButton(
                        title = "Add Exercise",
                        icon = Icons.Rounded.Add,
                        modifier = Modifier.weight(1f),
                        onClick = { /* TODO: Open exercise addition screen */ }
                    )
                }
            }

            // Schedule management
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "TRAINING SCHEDULE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMedium,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                ScheduleCard(
                    onClick = { /* TODO: Open schedule management screen */ }
                )
            }

            // Today's workout
            TodaysWorkoutCard(
                onClick = { /* TODO: Navigate to today's workout */ },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyPartButton(bodyPart: BodyPart) {
    Card(
        onClick = { /* TODO: Navigate to exercises for this body part */ },
        modifier = Modifier.size(100.dp, 100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = bodyPart.type,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = TextDark
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SportRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = SportRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SportRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = "Schedule",
                    tint = SportRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Weekly Plan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "View and manage your schedule",
                    fontSize = 14.sp,
                    color = TextMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodaysWorkoutCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SportRed
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Today's Workout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Start your training session",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Transparent
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier
                        .size(36.dp)
                        .padding(6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingDashboardPreview() {
    TrainingAppTheme {
        TrainingDashboard()
    }
}