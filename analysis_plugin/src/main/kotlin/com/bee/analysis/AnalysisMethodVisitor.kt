package com.bee.analysis

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodNode

/**
 *
 * @author petterp
 */
class AnalysisMethodVisitor(
    private val className: String,
    access: Int,
    name: String?,
    descriptor: String?,
    signature: String?,
    exceptions: Array<out String>?,
) : MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions) {

    override fun visitMethodInsn(
        opcodeAndSource: Int,
        owner: String,
        name: String,
        descriptor: String?,
        isInterface: Boolean,
    ) {
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
        MethodAnalysisUtils.filterAndAddMethod(className, this.name, owner, name)
    }
}
