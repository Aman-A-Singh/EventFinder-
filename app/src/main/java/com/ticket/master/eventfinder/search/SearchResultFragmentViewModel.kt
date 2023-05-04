package com.ticket.master.eventfinder.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ticket.master.eventfinder.models.event.EventItem
import com.ticket.master.eventfinder.models.location.LocationX
import com.ticket.master.eventfinder.services.EventService
import com.ticket.master.eventfinder.util.UIState
import kotlinx.coroutines.launch

class SearchResultFragmentViewModel : ViewModel() {
    val eventList: MutableLiveData<List<EventItem>> = MutableLiveData()
    private val _location = MutableLiveData<LocationX>()
    val location: LiveData<LocationX> = _location
    val uiState: MutableLiveData<UIState> = MutableLiveData(UIState.INPROGREES)

    fun getEvents(radius: Int, category: String, keyword: String, geoHash: String) {
        viewModelScope.launch {
            try {
                eventList.value = EventService.EventsServiceApi.retrofitService.getEventList(
                    radius,
                    category,
                    keyword,
                    geoHash
                )
                uiState.postValue(UIState.COMPLETED)
            } catch (e: Exception) {
                uiState.postValue(UIState.ERROR)
            }
        }
    }
}