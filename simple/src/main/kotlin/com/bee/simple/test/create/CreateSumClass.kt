package com.bee.simple.test.create

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.FileOutputStream

/**
 *
 * @author petterp
 */
fun main() {
    FileOutputStream("simple/src/main/kotlin/com/bee/simple/test/create/cla/Sum.class").use {
        it.write(dump())
    }
}

fun dump(): ByteArray {
    val classWriter = ClassWriter(1)
    classWriter.visit(
        Opcodes.V1_8,
        Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
        "com/bee/analysis/test/Sum",
        null,
        "java/lang/Object",
        null,
    )

    var methodVisitor: MethodVisitor =
        classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
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
