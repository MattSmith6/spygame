package com.example.spygame.util

import android.os.Handler
import android.os.Looper
import java.util.function.Consumer
import javax.security.auth.callback.Callback

class ThreadCreator {

    companion object {
        fun createThreadWithCallback(runnable: Runnable, callback: Runnable) {
            val thread = Thread {
                runnable.run()
                runTaskOnMainThread(callback)
            }
            thread.start()
        }

        private fun runTaskOnMainThread(runnable: Runnable) {
            // Modified from: https://stackoverflow.com/questions/11123621/running-code-in-main-thread-from-another-thread
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(runnable)
        }
    }

}