package com.bee.plugins

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val aa = print("")

    /**
     * 这什么玩意
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("woshishi")
        setContentView(R.layout.activity_main)
    }
}
