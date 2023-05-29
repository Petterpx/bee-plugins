package com.bee.analysis.model

data class HitMethodData(
    val currentClass: String,
    val currentMethod: String,
    val invokeClassName: String,
    val invokeMethod: String,
)
