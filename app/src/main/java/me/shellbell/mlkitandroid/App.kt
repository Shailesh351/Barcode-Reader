package me.shellbell.mlkitandroid

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Created by Shailesh351 on 22/4/19.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}