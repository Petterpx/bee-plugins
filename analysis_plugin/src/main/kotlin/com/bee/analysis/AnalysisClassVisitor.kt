package com.bee.analysis

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 *
 * @author petterp
 */
class AnalysisClassVisitor(private val classVisitor: ClassVisitor?) : ClassNode(Opcodes.ASM9) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor {
        return AnalysisMethodVisitor(this.name, access, name, descriptor, signature, exceptions)
    }

    override fun visitEnd() {
        accept(classVisitor)
    }
}
