package com.example.flutter_demo

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.moviefavorites/movies"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "getIntentExtra" -> {
                        val key = call.argument<String>("key")
                        if (key != null) {
                            val value = intent.getStringExtra(key)
                            result.success(value)
                        } else {
                            result.error("INVALID_ARGUMENT", "Key is null", null)
                        }
                    }
                    else -> result.notImplemented()
                }
            }
    }
}
