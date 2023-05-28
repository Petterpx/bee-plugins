package com.bee.simple.test.update

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream

/**
 *
 * @author petterp
 */
fun main() {
    val c =
        File("/Users/petterp/Documents/Android/kotlin/gradle/BeePlugins/analysis_plugin/src/main/kotlin/com/bee/analysis/test/A.class.class")
    FileOutputStream(c).use {
    }
}

fun update(): ByteArray {
    val classWriter = ClassWriter(1)
    var methodVisitor: MethodVisitor
    var annotationVisitor0: AnnotationVisitor
    classWriter.visit(
        Opcodes.V1_8,
        Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
        "com/bee/analysis/test/Sum",
        null,
        "java/lang/Object",
        null,
    )

//    annotationVisitor0 = classWriter.visitAnnotation("Lkotlin/Metadata;", true)
//    annotationVisitor0.visit("mv", intArrayOf(1, 4, 3))
//    annotationVisitor0.visit("bv", intArrayOf(1, 0, 3))
//    annotationVisitor0.visit("k", 1)
//    annotationVisitor0.visitEnd()
//    annotationVisitor0.visitArray("d1").apply {
//        visit(
//            null,
//            "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0008\u0002\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"
//        )
//        visitEnd()
//    }
//    annotationVisitor0.visitArray("d2").apply {
//        visit(null, "Lcom/bee/analysis/test/Sum;")
//        visit(null, "")
//        visit(null, "()V")
//        visit(null, "add")
//        visit(null, "")
//        visit(null, "analysis_plugin")
//        visitEnd()
//    }
    methodVisitor =
        classWriter.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
            "add",
            "()V",
            null,
            null,
        )
    methodVisitor.visitCode()
    methodVisitor.visitIntInsn(Opcodes.BIPUSH, 123)
    methodVisitor.visitVarInsn(Opcodes.ISTORE, 1)
    methodVisitor.visitInsn(Opcodes.ICONST_0)
    methodVisitor.visitVarInsn(Opcodes.ISTORE, 2)
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
    methodVisitor.visitInsn(Opcodes.RETURN)
    methodVisitor.visitMaxs(2, 2)
    methodVisitor.visitEnd()
    classWriter.visitEnd()
    return classWriter.toByteArray()
}
