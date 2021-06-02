package com.university.exam8.ui.interfaces

interface FutureCallBack<T> {
    fun done(result: String) {}
    fun error(title: String, errorMessage: String) {}
}
