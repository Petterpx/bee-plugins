package com.bee.analysis

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 *
 * @author petterp
 */
class AnalysisClassVisitor(cv: ClassVisitor?, private val buildType: String) :
    ClassVisitor(Opcodes.ASM9, cv) {
    private var className: String = ""
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name ?: ""
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor {
        return AnalysisMethodVisitor(
            className,
            access,
            name,
            descriptor,
            signature,
            exceptions
        ).apply {
            buildType = this@AnalysisClassVisitor.buildType
        }
    }
}
