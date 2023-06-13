package com.bee.router.utils

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.io.InputStream

/**
 * 文档生成utils
 * @author petterp
 */
object DocUtils {
    private const val ROUTER_MAPPING_PARAMS = "params"

    fun scanInput(input: InputStream, docList: MutableList<String>) {
        input.use {
            val cr = ClassReader(it)
            val cw = ClassWriter(cr, 0)
            val cv = RouterDocClassNode(cw, docList)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
        }
    }

    class RouterDocClassNode(
        private val cw: ClassWriter,
        private val docList: MutableList<String>
    ) :
        ClassNode(Opcodes.ASM9) {

        override fun visitField(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            value: Any?
        ): FieldVisitor {
            if (name == ROUTER_MAPPING_PARAMS && value.toString().isNotEmpty()) {
                docList.add(value.toString().replace("[", "").replace("]", ""))
            }
            return super.visitField(access, name, descriptor, signature, value)
        }

        override fun visitEnd() {
            accept(cw)
        }
    }
}