package com.bee.router.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent


/**
 * 路由导航
 * @author petterp
 */
object RouterNavigation {
    private val routerMap = mutableMapOf<String, RouterData>()

    fun init() {
        //在这里我们会对路由表进行注册
    }

    fun navigation(context: Context, url: String) {
        val routerData = routerMap[url] ?: return
        val intent = Intent().apply {
            component = ComponentName(context, routerData.cla)
        }
        context.startActivity(intent)
    }
}