package com.caper.jungsoos.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.caper.jungsoos.entities.Product

/**
 * This is the SqlLite Data Base Helper Class
 * This will create the Data Base
 *  Update Version number if we are changing the Data Base Structure
 */
@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    private val mIsDatabaseCreated: MutableLiveData<Boolean> = MutableLiveData()
    abstract fun productModel(): ProductDao?

    private fun updateDatabaseCreated(context: Context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated()
        }
    }

    private fun setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true)
    }

    val databaseCreated: LiveData<Boolean>
        get() = mIsDatabaseCreated

    /**
     * SqlLite Data Base Initializer
     */
    companion object {
        const val DATABASE_NAME = "jungsoos-db"
        private var sInstance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase? { //, final AppExecutors executors
            if (sInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (sInstance == null) {
                        sInstance = buildDatabase(context.applicationContext) //, executors
                        sInstance!!.updateDatabaseCreated(context.applicationContext)
                    }
                }
            }
            return sInstance
        }

        private fun buildDatabase(appContext: Context): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}