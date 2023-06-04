package com.bee.router.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class BeeRouter(
    val url: String,
    val desc: String = ""
)
