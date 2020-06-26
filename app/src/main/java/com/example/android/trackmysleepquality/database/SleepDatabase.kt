/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = arrayOf(SleepNight::class), version = 1, exportSchema = false)
abstract class SleepDatabase: RoomDatabase() {

    //We don't need to instantiate this class, just need to access it's methods, so declare
    //the Dao as a value, then get it with the companion object, which allows clients to access
    //methods for creating the database without instantiating the class. The only purpose of the class
    //is to provide a database.
    abstract val sleepDatabaseDao: SleepDatabaseDao

    companion object {

        //Volatile variables are never cached and are kept in main memory. This makes sure the
        //value of the Instance variable is always up to date, and is the same for all threads
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase {
            //Wrap code in synchronized to be sure only one thread of execution can enter the
            //block at a time, preventing the database from being initialized more than once
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database")
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
