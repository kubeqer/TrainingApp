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
        if (bodyPartDao.getAllBodyPartsImmediate().isNotEmpty()) {
            return
        }
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
                        youtube_url = "https://www.youtube.com/shorts/hWbUlkb5Ms4"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Push-Up",
                        description = "Start in plank position with hands shoulder-width apart, lower chest to ground, then push back up.",
                        youtube_url = "https://www.youtube.com/shorts/ba8tr1NzwXU"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Dumbbell Fly",
                        description = "Lying on bench with dumbbells extended above chest, lower weights out to sides in arc motion.",
                        youtube_url = "https://www.youtube.com/watch?v=QENKPHhQVi4"
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
                        youtube_url = "https://www.youtube.com/watch?v=eGo4IYlbE5g"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Deadlift",
                        description = "Lift barbell from ground to hip level with back straight and shoulders back.",
                        youtube_url = "https://www.youtube.com/shorts/ZaTM37cfiDs"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Bent Over Row",
                        description = "Bend at hips with barbell or dumbbells, pull weight to lower chest while keeping back straight.",
                        youtube_url = "https://www.youtube.com/shorts/Nqh7q3zDCoQ"
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
                        youtube_url = "https://www.youtube.com/shorts/PPmvh7gBTi0"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Leg Press",
                        description = "Push weight platform away with feet while seated in machine.",
                        youtube_url = "https://www.youtube.com/shorts/nDh_BlnLCGc"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Lunge",
                        description = "Step forward, lowering hips until both knees are bent at 90 degrees.",
                        youtube_url = "https://www.youtube.com/watch?v=wrwwXE_x-pQ"
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
                        youtube_url = "https://www.youtube.com/watch?v=_RlRDWO2jfg"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Lateral Raise",
                        description = "Raise dumbbells out to sides until arms are parallel to floor.",
                        youtube_url = "https://www.youtube.com/watch?v=geenhiHju-o&t=19s"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Front Raise",
                        description = "Raise weights in front of body to shoulder height with straight arms.",
                        youtube_url = "https://www.youtube.com/watch?v=X2BPkPvWd34"
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
                        youtube_url = "https://www.youtube.com/watch?v=yTWO2th-RIY"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Tricep Extension",
                        description = "Extend arms against resistance to work triceps.",
                        youtube_url = "https://www.youtube.com/shorts/8FNGBJUHfsA"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Dips",
                        description = "Lower and raise body between parallel bars, primarily working triceps.",
                        youtube_url = "https://www.youtube.com/watch?v=l41SoWZiowI"
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
                        youtube_url = "https://www.youtube.com/watch?v=MKmrqcoCZ-M"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Plank",
                        description = "Hold body in straight line from head to heels, supported by forearms and toes.",
                        youtube_url = "https://www.youtube.com/shorts/v25dawSzRTM"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Russian Twist",
                        description = "Seated with torso at 45-degree angle, rotate torso from side to side.",
                        youtube_url = "https://www.youtube.com/watch?v=DJQGX2J4IVw"
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
                        youtube_url = "https://www.youtube.com/watch?v=kVnyY17VS9Y"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Jumping Rope",
                        description = "Skip over rope swung under feet and over head.",
                        youtube_url = "https://www.youtube.com/watch?v=vEJ7XbbAMAg"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Burpee",
                        description = "Drop to push-up position, perform push-up, return to standing, then jump up.",
                        youtube_url = "https://www.youtube.com/watch?v=qLBImHhCXSw&t=39s"
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
                        youtube_url = "https://www.youtube.com/watch?v=DqkYuWR4zRI"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Thruster",
                        description = "Combination of front squat and overhead press in one fluid movement.",
                        youtube_url = "https://www.youtube.com/watch?v=rqPNxGQqYLA"
                    ),
                    Exercise(
                        bodyPartId = bodyPartId,
                        name = "Burpee",
                        description = "Drop to push-up position, perform push-up, return to standing, then jump up.",
                        youtube_url = "https://www.youtube.com/watch?v=qLBImHhCXSw&t=39s"
                    )
                )
            )
        }
        exercises.forEach { exercise ->
            exerciseDao.insert(exercise)
        }
    }
}