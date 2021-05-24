package com.example.maptimelineapp.datasource.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.maptimelineapp.datasource.room.dao.UserLocationDao
import com.example.maptimelineapp.datasource.room.models.DateConverter
import com.example.maptimelineapp.datasource.room.models.UserLocation

@Database(entities = [UserLocation::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val userLocationDao: UserLocationDao

    /*private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) :RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.userLocationDao())
                }
            }
        }

        suspend fun populateDatabase(userLocationDao: UserLocationDao) {
            val loc = UserLocation(
                Random.nextInt(),
                Random.nextDouble(),
                Random.nextDouble(),
                "Name ${Random.nextInt()}",
                "Description ${Random.nextInt()}",
                DateConverter().fromTimeStamp(Date().time),
            )
            userLocationDao.insertUserLocation(loc)
        }
    }*/


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "location-database"
                    )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    /*companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(passCode: CharArray, context: Context): AppDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = buildDatabase(passCode, context)
                    INSTANCE = instance
                }
                return INSTANCE
            }
        }

        //fun getInstance(passcode: CharArray, context: Context):
          //      AppDatabase = buildDatabase(passcode, context)

        private fun buildDatabase(
            passcode: CharArray,
            context: Context
        ): AppDatabase {
            // DatabaseKeyMgr is a singleton that all of the above code is wrapped into.
            // Ideally this should be injected through DI but to simplify the sample code
            // we'll retrieve it as follows
            val dbKey = EncryptAndStoreKey().getCharKey(passcode, context)
            val supportFactory = SupportFactory(SQLiteDatabase.getBytes(dbKey))
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "encry-location-database"
            ).allowMainThreadQueries()
                .openHelperFactory(supportFactory)
                .build()
        }
    }*/
}