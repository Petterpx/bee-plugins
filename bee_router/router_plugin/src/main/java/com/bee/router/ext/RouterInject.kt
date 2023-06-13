package com.bee.router.ext

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 *
 * @author petterp
 */
object RouterInject {
    private const val NAVIGATION = "com/bee/router/core/RouterNavigation"
    private const val NAVIGATION_INJECT_METHOD = "init"
    private const val NAVIGATION_FIELD_NAME = "routerMap"

    fun inject(origin: ByteArray, targetList: List<String>): ByteArray {
        val cr = ClassReader(origin)
        val cw = ClassWriter(cr, 0)
        val cv = RouterClassVisitor(cw, targetList)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    class RouterClassVisitor(cw: ClassWriter, private val clas: List<String>) :
        ClassVisitor(Opcodes.ASM9, cw) {
        override fun visitMethod(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
            if (name == NAVIGATION_INJECT_METHOD) {
                return RouterMethodVisitor(mv, clas)
            }
            return mv
        }
    }

    class RouterMethodVisitor(methodVisitor: MethodVisitor, private val clas: List<String>) :
        MethodVisitor(Opcodes.ASM9, methodVisitor) {

        override fun visitInsn(opcode: Int) {
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                clas.map {
                    it.replace(".class", "")
                }.forEach { className ->
                    mv.visitTypeInsn(Opcodes.NEW, className)
                    mv.visitInsn(Opcodes.DUP)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        className,
                        "<init>",
                        "()V",
                        false
                    )
                    mv.visitFieldInsn(
                        Opcodes.GETSTATIC,
                        NAVIGATION,
                        NAVIGATION_FIELD_NAME,
                        "Ljava/util/Map;"
                    )
                    mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        className,
                        "init",
                        "(Ljava/util/Map;)V",
                        false
                    )
                }
            }
            super.visitInsn(opcode)
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + clas.size, maxLocals)
        }
    }
}