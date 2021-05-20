package com.example.meetupapp.util.providers

import android.content.Context
import com.example.meetupapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StringProvider @Inject constructor(
    @ApplicationContext val context: Context
) : IStringProvider {
    override val hello: String = context.getString(R.string.hello)
}

interface IStringProvider {
    val hello: String
}