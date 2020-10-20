package com.enping.transformers.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enping.transformers.data.TransformerRepo
import com.enping.transformers.data.model.Transformer
import com.enping.transformers.ui.Event
import kotlinx.coroutines.launch

class TransformerEditViewModel(private val repo: TransformerRepo) : ViewModel() {

    private val _transformer = MutableLiveData<Transformer>()
    val transformer: LiveData<Transformer> = _transformer

    private val _isEdit = MutableLiveData<Boolean>()
    val isEdit: LiveData<Boolean> = _isEdit

    private val _isSubmit = MutableLiveData<Boolean>()
    val isSubmit: LiveData<Boolean> = _isSubmit

    private val _errorEvent = MutableLiveData<Event<Throwable>>()
    val errorEvent: LiveData<Event<Throwable>> = _errorEvent

    fun load(isEdit: Boolean, id: String) {
        viewModelScope.launch {
            _isEdit.value = isEdit
            if (isEdit) {
                _transformer.value = repo.getTransformer(id)
            } else {
                _transformer.value = Transformer.create()
            }
        }
    }

    fun edit(transformer: Transformer) {
        val originalTransformer = _transformer.value ?: return
        val updatedTransformer = originalTransformer.copy(
            name = transformer.name,
            strength = transformer.strength,
            intelligence = transformer.intelligence,
            speed = transformer.speed,
            endurance = transformer.endurance,
            rank = transformer.rank,
            courage = transformer.courage,
            firepower = transformer.firepower,
            skill = transformer.skill,
            team = transformer.team
        )
        _transformer.value = updatedTransformer
    }

    fun save() {
        viewModelScope.launch {
            try {
                val transformer = _transformer.value ?: return@launch
                if (isEdit.value == true) {
                    repo.updateTransformer(transformer)
                } else {
                    repo.createTransformer(transformer)
                }
                _isSubmit.value = true
            } catch (e: Throwable) {
                _errorEvent.value = Event(e)
                _isSubmit.value = false
            }
        }
    }
}
