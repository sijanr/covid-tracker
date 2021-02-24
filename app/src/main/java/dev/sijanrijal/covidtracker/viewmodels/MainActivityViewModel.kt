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

    var nationData = mutableListOf<COVIDData>()


    var stateData = mutableMapOf<String, List<COVIDData>>()

    private val _areDataReady = MutableLiveData<Boolean>()
    val areDataReady: LiveData<Boolean>
        get() = _areDataReady

    var stateList = mutableListOf<String>()

    //launch the coroutine to fetch the latest data from the internet
    init {
        viewModelScope.launch {
            fetch()
        }
    }


    //fetch the data from the internet and notify the user once it is ready
    private suspend fun fetch() {
        coroutineScope {
            val nationalListDeferred = async(Dispatchers.IO) {
                COVIDApiService.retrofitService.getAllUSData().reversed()
            }
            val stateListDeferred = async(Dispatchers.IO) {
                COVIDApiService.retrofitService.getAllStatesData()
            }
            val list = stateListDeferred.await()

            val stateValueDeferred = async(Dispatchers.Default) {
                val map = list.reversed().groupBy { it.state!! }.toSortedMap()
                for (item in map) {
                    stateList.add(item.key)
                }
                return@async map
            }

            stateData = stateValueDeferred.await()
            nationData = nationalListDeferred.await() as MutableList<COVIDData>
            _areDataReady.value = true

        }
    }

}