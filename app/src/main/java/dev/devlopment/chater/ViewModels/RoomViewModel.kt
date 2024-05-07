package dev.devlopment.chater.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.devlopment.chater.MainAndUtils.Injection
import dev.devlopment.chater.Repository.Result
import dev.devlopment.chater.Repository.Room
import dev.devlopment.chater.Repository.RoomRepository
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms
    private val roomRepository: RoomRepository = RoomRepository(Injection.instance())

    init {
        loadRooms()
    }

    fun createRoom(name: String) {
        viewModelScope.launch {
            roomRepository.createRoom(name)
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            when (val result = roomRepository.getRooms()) {
                is Result.Success -> _rooms.value = result.data
                is Result.Error -> {

                }
            }
        }
    }

}