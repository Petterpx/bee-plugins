package com.bee.router_simple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bee.router.annotations.BeeRouter

@BeeRouter("test", "测试的")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
