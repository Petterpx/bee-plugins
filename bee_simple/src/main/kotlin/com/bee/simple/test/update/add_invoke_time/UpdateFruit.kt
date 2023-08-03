package com.bee.simple.test.update.add_invoke_time

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * ASM练习: 对Fruit增加耗时检测
 * */
fun main() {
    val c =
        FileInputStream(File("bee_simple/src/main/kotlin/com/bee/simple/test/update/add_invoke_time/Fruit.class"))
    val outClassPath = "bee_simple/src/main/kotlin/com/bee/simple/test/update/add_invoke_time/FruitNew.class"
    val cr = ClassReader(c)
    val cw = ClassWriter(cr, COMPUTE_MAXS)
    val cv = ProfileClassAdapter(cw)
    cr.accept(cv, ClassReader.EXPAND_FRAMES)
    FileOutputStream(outClassPath).use {
        it.write(cw.toByteArray())
    }
}

class ProfileClassAdapter(cw: ClassWriter) : ClassVisitor(Opcodes.ASM9, cw) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor? {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (name == "test") {
            ProfileMethodVisitor(mv, access, name, descriptor)
        } else {
            mv
        }
    }
}

class ProfileMethodVisitor(
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?,
) : AdviceAdapter(ASM9, methodVisitor, access, name, descriptor) {

    /**
     * 方法开始之前
     * */
    override fun visitCode() {
        super.visitCode()
        mv.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false,
        )
        // 将获取的时间存储在本地变量,这里的 2 并不是最佳推荐，很可能会被覆盖,暂时不知道怎么解决
        mv.visitVarInsn(LSTORE, 2)
    }

    override fun visitInsn(opcode: Int) {
        if (opcode == IRETURN) {
            mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false,
            )
            // 加载之前存储的变量3
            mv.visitVarInsn(LLOAD, 2)
            // 计算耗时
            mv.visitInsn(LSUB)
            mv.visitVarInsn(LSTORE, 2)

            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;",
            )

            mv.visitVarInsn(LLOAD, 2)

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(J)V",
                false,
            )
        }
        super.visitInsn(opcode)
    }
}
