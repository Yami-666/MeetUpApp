package com.example.meetupapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow

typealias FlowStateList<T> = Flow<DataState<List<T>>>
typealias MutableLiveDataStateList<PostUi> = MutableLiveData<DataState<List<PostUi>>>
typealias LiveDataStateList<PostUi> = LiveData<DataState<List<PostUi>>>