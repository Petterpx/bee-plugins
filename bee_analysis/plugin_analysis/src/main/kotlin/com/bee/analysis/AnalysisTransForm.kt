package com.bee.analysis

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor

/**
 * ASM分析器
 * 其中 InstrumentationParameters 用于传参
 * None 代表不用传参
 * @author petterp
 */
abstract class AnalysisTransForm : AsmClassVisitorFactory<AnalysisParameters> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor,
    ): ClassVisitor {
        val buildType = parameters.get().buildType.get()
        return AnalysisClassVisitor(nextClassVisitor, buildType)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return !classData.className.contains("androidx.") && !classData.className.contains(".R")
    }
}
