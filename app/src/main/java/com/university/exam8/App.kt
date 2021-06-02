package com.university.exam8

import android.app.Application
import android.content.Context
import android.content.res.Resources

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        res = resources
    }

    private object Holder {
        val INSTANCE: App = App()
    }

    companion object {
        val instance: App by lazy { Holder.INSTANCE }
        internal var context: Context? = null
        internal var res: Resources? = null
    }

    fun getContext(): Context = context!!
}