package com.example.trainingapp.data.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.trainingapp.data.dao.*
import com.example.trainingapp.data.entity.*
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [
        BodyPart::class,
        Exercise::class,
        WorkoutPlan::class,
        WorkoutDay::class,
        DayExercise::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun bodyPartDao(): BodyPartDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutDayDao(): WorkoutDayDao
    abstract fun dayExerciseDao(): DayExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(WorkoutDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback do inicjalizacji bazy danych po jej utworzeniu
     */
    private class WorkoutDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                DatabaseInitializer.populateDatabase(database, scope)
            }
        }
    }
}