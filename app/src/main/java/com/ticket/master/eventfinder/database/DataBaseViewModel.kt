package com.ticket.master.eventfinder.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataBaseViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var allEventList: List<EventEntity>
    private val eventRepository: EventEntityRepository

    init {
        val db = EventEntityDatabase.getInstance(application)
        val dao = db.eventEntityDao()
        eventRepository = EventEntityRepository(dao)
        CoroutineScope(Dispatchers.Main).launch {
            allEventList = eventRepository.getEventList()
        }
    }

    fun insert(event: EventEntity) =
        viewModelScope.launch(Dispatchers.IO) { eventRepository.insert(event) }

    fun isFavorite(id: String): Boolean {
        allEventList.map {
            if (it.id.equals(id)) {
                return true
            }
        }
        return false
    }

    fun removeEvent(event: EventEntity) =
        viewModelScope.launch(Dispatchers.IO) { eventRepository.removeEvent(event) }
}