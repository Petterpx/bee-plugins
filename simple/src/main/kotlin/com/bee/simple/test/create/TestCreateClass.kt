package com.bee.simple.test.create

import org.objectweb.asm.AnnotationVisitor
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
