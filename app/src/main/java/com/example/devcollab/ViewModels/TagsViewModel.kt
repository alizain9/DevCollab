package com.example.devcollab.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map


class TagsViewModel : ViewModel() {
    private val _tags = MutableLiveData<List<String>>(emptyList())
    val tags: LiveData<List<String>> get() = _tags

    fun addTag(tag: String) {
        val updated = _tags.value.orEmpty() + tag
        _tags.value = updated

    }

    fun removeTag(tag: String) {
        val updated = _tags.value.orEmpty().filterNot { it == tag }
        _tags.value = updated
    }
}