package com.enping.transformers.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enping.transformers.data.GameResult
import com.enping.transformers.data.TransformerRepo
import com.enping.transformers.data.model.Transformer
import com.enping.transformers.ui.util.Event
import kotlinx.coroutines.launch

class TransformersViewModel(private val repo: TransformerRepo) : ViewModel() {

    private val _transformers = MutableLiveData<List<Transformer>>()
    val transformers: LiveData<List<Transformer>> = _transformers

    private val _isLoaded = MutableLiveData<Boolean>(false)
    val isLoaded: LiveData<Boolean> = _isLoaded

    private val _warEvent = MutableLiveData<Event<GameResult>>()
    val warEvent: LiveData<Event<GameResult>> = _warEvent

    private val _errorEvent = MutableLiveData<Event<Throwable>>()
    val errorEvent: LiveData<Event<Throwable>> = _errorEvent

    fun load() {
        viewModelScope.launch {
            try {
                repo.getOrCreateAllSpark()
            } catch (e: Throwable) {
                _errorEvent.value = Event(MissingAllSparkException())
                _isLoaded.value = false
                return@launch
            }
            try {
                _transformers.value = repo.getTransformers()
                _isLoaded.value = true
            } catch (e: Throwable) {
                _errorEvent.value = Event(e)
                _isLoaded.value = false
            }
        }
    }

    fun delete(transformerId: String) {
        viewModelScope.launch {
            try {
                repo.deleteTransformer(transformerId)
            } catch (e: Throwable) {
                _errorEvent.value = Event(e)
            } finally {
                _transformers.value = repo.getTransformers()
            }
        }
    }

    fun startWar() {
        viewModelScope.launch {
            try {
                _warEvent.value = Event(repo.battleTransformers())
            } catch (e: Exception) {
                _errorEvent.value = Event(e)
            }
        }
    }
}

class MissingAllSparkException: Exception()
