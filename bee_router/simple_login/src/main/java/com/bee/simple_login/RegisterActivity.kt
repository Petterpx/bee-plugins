package com.bee.simple_login // ktlint-disable package-name

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bee.router.annotations.Router

/**
 *
 * @author petterp
 */
@Router("registerR", "注123册13")
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val img = ImageView(this)
        setContentView(TextView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
            text = "登录"
            textSize = 30F
        })
    }
}
