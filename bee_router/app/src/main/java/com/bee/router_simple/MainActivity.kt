package com.bee.router_simple

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bee.router.annotations.Router
import com.bee.router.core.RouterNavigation

@Router("test", "测试的")
class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RouterNavigation.init()
        findViewById<View>(R.id.main).setOnClickListener {
            RouterNavigation.navigation(this, "login")
        }
    }
}
