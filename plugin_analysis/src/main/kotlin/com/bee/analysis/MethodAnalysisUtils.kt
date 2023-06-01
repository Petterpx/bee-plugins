package com.bee.analysis

import com.bee.analysis.model.HitMethodData

object MethodAnalysisUtils {
    private val results = mutableListOf<HitMethodData>()
    private val packages = mutableListOf<String>()
    private var enableLog = false

    fun initConfig(extension: RuleExtension) {
        enableLog = extension.enableLog
        extension.packages.forEach {
            val packageClassName = it.replace('.', '/')
            packages.add(packageClassName)
        }
    }

    fun filterAndAddMethod(
        currentClass: String,
        currentMethod: String,
        invokeClassName: String,
        invokeMethod: String,
    ) {
        val isHit = packages.any { invokeClassName.contains(it) }
        if (!isHit) return
        results.add(HitMethodData(currentClass, currentMethod, invokeClassName, invokeMethod))
    }

    fun end() {
        println("--------bee_analysis-start----->")
        println("--> rules [packages]:")
        packages.forEach {
            println("package: [$it]")
        }
        println("------------------------------->")
        println("--> result:")
        results.forEach {
            println(
                "currentClass:[${it.currentClass}],method:[${it.currentMethod}]," +
                    "  invokeClass:[${it.invokeClassName}],method:[${it.invokeMethod}]",
            )
        }
        println("--------bee_analysis-end------->")
    }
}
