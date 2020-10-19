package com.enping.transformers.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enping.transformers.data.TransformerRepo
import com.enping.transformers.data.model.Transformer
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(private val repo: TransformerRepo) : ViewModel() {

    private val _transformers = MutableLiveData<List<Transformer>>()
    val transformers : LiveData<List<Transformer>> = _transformers

    fun load(){
        viewModelScope.launch {
            //Todo, handle network disconnect
            repo.getOrCreateAllSpark()
            _transformers.value = repo.getTransformers()
        }
    }

    fun delete(transformerId: String){
        viewModelScope.launch {
            repo.deleteTransformer(transformerId)
            _transformers.value = repo.getTransformers()
        }
    }
}
