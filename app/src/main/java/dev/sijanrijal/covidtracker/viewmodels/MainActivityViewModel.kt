package dev.sijanrijal.covidtracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sijanrijal.covidtracker.model.COVIDData
import dev.sijanrijal.covidtracker.network.COVIDApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    private var _nationData = MutableLiveData<List<COVIDData>>()
    val nationData : LiveData<List<COVIDData>>
        get() = _nationData

    private var _stateData = MutableLiveData<Map<String, List<COVIDData>>>()
    val stateData : LiveData<Map<String, List<COVIDData>>>
        get() = _stateData

    var stateList = mutableListOf<String>()

    init {
        viewModelScope.launch {
            fetch()
        }
    }

    private suspend fun fetch() {
        coroutineScope {
           val nationalListDeferred =  async (Dispatchers.IO) {
                COVIDApiService.retrofitService.getAllUSData().reversed()
            }
            val stateListDeferred = async (Dispatchers.IO) {
                COVIDApiService.retrofitService.getAllStatesData()
            }
            _nationData.value = nationalListDeferred.await()
            val list = stateListDeferred.await()

            val stateValueDeferred = async(Dispatchers.Default) {
                val map = list.reversed().groupBy { it.state!! }.toSortedMap()
                for (item in map) {
                    stateList.add(item.key)
                }
                return@async map
            }

            _stateData.value = stateValueDeferred.await()

        }
    }

}