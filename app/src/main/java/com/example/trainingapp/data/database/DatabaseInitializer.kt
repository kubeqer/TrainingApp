package com.example.trainingapp.data.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.trainingapp.data.entity.BodyPart
import com.example.trainingapp.data.entity.Exercise

object DatabaseInitializer {
    fun populateDatabase(database: WorkoutDatabase, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            populateBodyParts(database)
        }
    }
    private suspend fun populateBodyParts(database: WorkoutDatabase) {
        val bodyPartDao = database.bodyPartDao()
        val exerciseDao = database.exerciseDao()

        val bodyPartTypes = listOf(
            "Chest",
            "Back",
            "Legs",
            "Shoulders",
            "Arms",
            "Core",
            "Cardio",
            "Full Body"
        )

        val bodyPartIdsMap = mutableMapOf<String, Long>()

        bodyPartTypes.forEach { type ->
            val id = bodyPartDao.insert(BodyPart(type = type))
            bodyPartIdsMap[type] = id
        }
        val exercises = mutableListOf<Exercise>()
        bodyPartIdsMap["Chest"]?.let { bodyPartId ->
            exercises.addAll(
                listOf(
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Bench Press",
                        description = "Lying on bench, lower barbell to chest and press up until arms are extended.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Push-Up",
                        description = "Start in plank position with hands shoulder-width apart, lower chest to ground, then push back up.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Dumbbell Fly",
                        description = "Lying on bench with dumbbells extended above chest, lower weights out to sides in arc motion.",
                        youtube_url = ""
                    )
                )
            )
        }
        bodyPartIdsMap["Back"]?.let { bodyPartId ->
            exercises.addAll(
                listOf(
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Pull-Up",
                        description = "Hang from bar with palms facing away, pull body up until chin clears bar.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Deadlift",
                        description = "Lift barbell from ground to hip level with back straight and shoulders back.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Bent Over Row",
                        description = "Bend at hips with barbell or dumbbells, pull weight to lower chest while keeping back straight.",
                        youtube_url = ""
                    )
                )
            )
        }
        bodyPartIdsMap["Legs"]?.let { bodyPartId ->
            exercises.addAll(
                listOf(
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Squat",
                        description = "Lower body by bending knees and hips, then return to standing position.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Leg Press",
                        description = "Push weight platform away with feet while seated in machine.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Lunge",
                        description = "Step forward, lowering hips until both knees are bent at 90 degrees.",
                        youtube_url = ""
                    )
                )
            )
        }
        bodyPartIdsMap["Shoulders"]?.let { bodyPartId ->
            exercises.addAll(
                listOf(
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Overhead Press",
                        description = "Press weight overhead from shoulder level until arms are fully extended.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Lateral Raise",
                        description = "Raise dumbbells out to sides until arms are parallel to floor.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Front Raise",
                        description = "Raise weights in front of body to shoulder height with straight arms.",
                        youtube_url = ""
                    )
                )
            )
        }
        bodyPartIdsMap["Arms"]?.let { bodyPartId ->
            exercises.addAll(
                listOf(
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Bicep Curl",
                        description = "Curl weight from extended arm position to shoulder while keeping elbows fixed.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Tricep Extension",
                        description = "Extend arms against resistance to work triceps.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Dips",
                        description = "Lower and raise body between parallel bars, primarily working triceps.",
                        youtube_url = ""
                    )
                )
            )
        }
        bodyPartIdsMap["Core"]?.let { bodyPartId ->
            exercises.addAll(
                listOf(
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Crunch",
                        description = "Lying on back, raise shoulders towards knees to work abdominal muscles.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Plank",
                        description = "Hold body in straight line from head to heels, supported by forearms and toes.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Russian Twist",
                        description = "Seated with torso at 45-degree angle, rotate torso from side to side.",
                        youtube_url = ""
                    )
                )
            )
        }
        bodyPartIdsMap["Cardio"]?.let { bodyPartId ->
            exercises.addAll(
                listOf(
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Running",
                        description = "Continuous running at steady pace to improve cardiovascular fitness.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Jumping Rope",
                        description = "Skip over rope swung under feet and over head.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Burpee",
                        description = "Drop to push-up position, perform push-up, return to standing, then jump up.",
                        youtube_url = ""
                    )
                )
            )
        }
        bodyPartIdsMap["Full Body"]?.let { bodyPartId ->
            exercises.addAll(
                listOf(
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Kettlebell Swing",
                        description = "Swing kettlebell from between legs to shoulder height using hip drive.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Thruster",
                        description = "Combination of front squat and overhead press in one fluid movement.",
                        youtube_url = ""
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Burpee",
                        description = "Drop to push-up position, perform push-up, return to standing, then jump up.",
                        youtube_url = ""
                    )
                )
            )
        }
        exercises.forEach { exercise ->
            exerciseDao.insert(exercise)
        }
    }
}