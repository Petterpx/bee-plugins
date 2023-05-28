package com.bee.analysis

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodNode

/**
 *
 * @author petterp
 */
class CustomMethodVistor(
    access: Int,
    name: String?,
    descriptor: String?,
    signature: String?,
    exceptions: Array<out String>?,
) : MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions) {
    override fun visitEnd() {
        super.visitEnd()
    }

    override fun visitMethodInsn(
        opcodeAndSource: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean,
    ) {
        println("你会被调用吗---$owner")
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
    }
}
