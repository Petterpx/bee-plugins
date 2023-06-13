package com.bee.simple.test.update.router

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *
 * @author petterp
 */
private const val NAVIGATION = "com/bee/router/core/RouterNavigation"
private const val NAVIGATION_INJECT_METHOD = "initRouter"
private const val NAVIGATION_FIELD_NAME = "routerMap"

fun main() {
    val c =
        FileInputStream(File("bee_simple/src/main/kotlin/com/bee/simple/test/update/router/RouterNavigation.class"))
    val outClassPath =
        "bee_simple/src/main/kotlin/com/bee/simple/test/update/router/RouterNavigationNew.class"
    val cr = ClassReader(c)
    val cw = ClassWriter(cr, 0)
    val targetList = mutableListOf(
        "com.bee.router.mapping._RouterMapping_1686628071982.class",
        "com.bee.router.mapping._RouterMapping_16866280719822.class"
    )
    val cv = RouterClassVisitor(cw, targetList)
    cr.accept(cv, ClassReader.EXPAND_FRAMES)
    FileOutputStream(outClassPath).use {
        it.write(cw.toByteArray())
    }
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

}
