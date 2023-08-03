package com.bee.analysis

import com.bee.analysis.model.HitMethodData

object MethodAnalysisUtils {
    private val results = HashMap<String, MutableList<HitMethodData>>()
    private val packages = HashSet<String>()
    private var enableLog = false

    fun initConfig(extension: RuleExtension) {
        reset()
        enableLog = extension.enableLog
        extension.packages.forEach {
            val packageClassName = it.replace('.', '/')
            packages.add(packageClassName)
        }
    }

    fun filterAndAddMethod(
        buildType: String,
        currentClass: String,
        currentMethod: String,
        invokeClassName: String,
        invokeMethod: String,
    ) {
        val isHit = packages.any { invokeClassName.contains(it) }
        if (!isHit) return
        if (results[buildType] == null) results[buildType] = mutableListOf()
        val d = HitMethodData(currentClass, currentMethod, invokeClassName, invokeMethod)
        results[buildType]?.add(d)
    }

    fun end(type: String) {
        if (!results.containsKey(type)) return
        println("--------bee_analysis-$type-start----->")
        println("--> rules [packages]:")
        packages.forEach {
            println("package: [$it]")
        }
        println("------------------------------------->")
        println("--> result: ")
        results[type]?.forEach {
            println(
                "currentClass:[${it.currentClass}],method:[${it.currentMethod}],"
                        + "  invokeClass:[${it.invokeClassName}],method:[${it.invokeMethod}]"
            )
        }
        println("--------bee_analysis-$type-end------->")
    }

    private fun reset() {
        results.clear()
        packages.clear()
        enableLog = false
    }
}
