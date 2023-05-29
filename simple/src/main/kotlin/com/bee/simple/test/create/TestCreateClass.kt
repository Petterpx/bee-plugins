package com.bee.simple.test.create

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.File
import java.io.FileOutputStream

/**
 *
 * @author petterp
 */
fun main() {
    val c =
        File("/Users/petterp/Documents/Android/kotlin/gradle/BeePlugins/simple/src/main/kotlin/com/bee/simple/test/create/A.class")
    FileOutputStream(c).use {
        it.write(dump())
    }
}

fun dump(): ByteArray {
    val classWriter = ClassWriter(1)
    var methodVisitor: MethodVisitor
    classWriter.visit(
        Opcodes.V1_8,
        Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
        "com/bee/analysis/test/Sum",
        null,
        "java/lang/Object",
        null,
    )

    methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
    methodVisitor.visitCode()
    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
    methodVisitor.visitMethodInsn(
        Opcodes.INVOKESPECIAL,
        "java/lang/Object",
        "<init>",
        "()V",
        false,
    )
    methodVisitor.visitInsn(Opcodes.RETURN)
    methodVisitor.visitMaxs(1, 1)
    methodVisitor.visitEnd()

    methodVisitor =
        classWriter.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL, "add", "()V", null, null)
    methodVisitor.visitCode()
    methodVisitor.visitIntInsn(Opcodes.BIPUSH, 123)
    methodVisitor.visitVarInsn(Opcodes.ISTORE, 1)
    methodVisitor.visitFieldInsn(
        Opcodes.GETSTATIC,
        "java/lang/System",
        "out",
        "Ljava/io/PrintStream;",
    )
    methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
    methodVisitor.visitMethodInsn(
        Opcodes.INVOKEVIRTUAL,
        "java/io/PrintStream",
        "println",
        "(I)V",
        false,
    )
    methodVisitor.visitTypeInsn(Opcodes.NEW, "com/bee/simple/test/create/a/TestA")
    methodVisitor.visitInsn(Opcodes.DUP)
    methodVisitor.visitMethodInsn(
        Opcodes.INVOKESPECIAL,
        "com/bee/simple/test/create/a/TestA",
        "<init>",
        "()V",
        false,
    )
    methodVisitor.visitInsn(Opcodes.POP)
    methodVisitor.visitInsn(Opcodes.RETURN)
    methodVisitor.visitMaxs(2, 2)
    methodVisitor.visitEnd()

    classWriter.visitEnd()
    return classWriter.toByteArray()
}

class CustomClassNode() : ClassNode(Opcodes.ASM9) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor {
        val method = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (name != "add") {
            method
        } else {
            return LogMethodVisitor(method, access, name, descriptor)
        }
    }
}

class LogMethodVisitor(
    mv: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?,
) : AdviceAdapter(ASM9, mv, access, name, descriptor) {
    override fun onMethodEnter() {
        mv.visitLdcInsn("123")
        mv.visitVarInsn(Opcodes.ASTORE, 1)
        mv.visitInsn(Opcodes.ICONST_0)
        mv.visitVarInsn(Opcodes.ISTORE, 2)
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/Object;)V",
            false,
        )
        super.onMethodEnter()
    }
}

class CustomMethodVisitor(private val mvv: MethodVisitor) : MethodNode(Opcodes.ASM9) {
    override fun visitCode() {
        mvv.apply {
            visitVarInsn(Opcodes.ALOAD, 0)
            visitLdcInsn("123")
            visitVarInsn(Opcodes.ASTORE, 1)
            visitInsn(Opcodes.ICONST_0)
            visitVarInsn(Opcodes.ISTORE, 2)
            visitFieldInsn(
                Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;",
            )
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/Object;)V",
                false,
            )
            visitMaxs(2, 3)
        }
    }
}
