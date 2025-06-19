package com.example.tp_flashcard.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CategoryEntity::class, FlashCardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun flashCardDao(): FlashCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flashcard_db"
                )
                    // Option 1 : préremplissage avec un asset .db
                    //.createFromAsset("database/prepopulate.db")
                    // Option 2 : insertion au premier lancement
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                scope.launch(Dispatchers.IO) {
                                    // Préremplir ici si besoin
                                    val categories = listOf(
                                        CategoryEntity(1, "Mathématiques"),
                                        CategoryEntity(2, "Histoire"),
                                        CategoryEntity(3, "Sciences")
                                    )
                                    database.categoryDao().insertCategories(categories)

                                    val cards = listOf(
                                        FlashCardEntity(1, 1, "7×8 = ?", "56"),
                                        FlashCardEntity(2, 1, "√49 = ?", "7"),
                                        FlashCardEntity(3, 2, "Révolution française", "1789")
                                    )
                                    database.flashCardDao().insertCards(cards)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}