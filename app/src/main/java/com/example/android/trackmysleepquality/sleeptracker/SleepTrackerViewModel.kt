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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment. Takes application context as a parameter and makes it available
 * as a property. android:onClick handler is added to fragment xml with databinding to call
 * lambda function onStartTracking()
 *
 * Use a transformation map everytime nights receives new data from the database
 */
class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application)
    : AndroidViewModel(application) {

    //Define a Job.
    //This allows you to cancel all coroutines started by this ViewModel when the ViewModel is
    //destroyed
    private var viewModelJob = Job()

    //Define a scope for the coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Define a variable to hold the current night. Make it MutableLiveData, because we need to
    //observe the data and change it
    private var tonight = MutableLiveData<SleepNight?>()

    //The Dao returns a LiveData object, so this is automatically run on a background thread by Room
    //Therefore we do not need to set this up in a coroutine
    private val nights = database.getAllNights()

    val nightsString = Transformations.map(nights) {nights ->
        formatNights(nights, application.resources)
    }

    //Start a coroutine when the ViewModel is initialized
    init {
        initializeTonight()
        Log.i("Fragment", "init called: ")
    }

    //Launch a coroutine to query the database
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    //Implement the click handlers
    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    //Cancel the Job when the ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    private suspend fun update (night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }
}

