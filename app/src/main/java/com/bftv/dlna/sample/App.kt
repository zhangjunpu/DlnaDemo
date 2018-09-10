package com.bftv.dlna.sample

import android.app.Application
import com.abooc.widget.Toast

/**
 *
 * @author Junpu
 * @time 2018/5/17 19:08
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Toast.init(this)
    }
}