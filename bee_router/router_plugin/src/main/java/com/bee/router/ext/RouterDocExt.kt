package com.bee.router.ext

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 用于处理Router文档的Ext
 * @author petterp
 */
object RouterDocExt {
    const val BEE_ROUTER_DOC_DIR = "beeRouter"
    private const val BEE_ROUTER_DOC_NAME = "doc.json"
    private const val BEE_ROUTER_MAPPING_PARAMS = "params"

    fun scanInput(
        isEnableDoc: Boolean,
        jar: JarFile,
        entry: JarEntry,
        docList: MutableList<String>
    ) {
        if (!isEnableDoc) return
        scanInput(jar.getInputStream(entry), docList)
    }

    fun scanInput(isEnableDoc: Boolean, file: File, docList: MutableList<String>) {
        if (!isEnableDoc) return
        scanInput(file.inputStream(), docList)
    }

    fun createDoc(isEnableDoc: Boolean, path: String, docList: List<String>) {
        if (!isEnableDoc) return
        val builder = StringBuilder("[")
        docList.forEach {
            builder.append(it).append(",")
        }
        if (builder.length > 1) builder.deleteCharAt(builder.lastIndex)
        builder.append("]")
        val file = File(path, BEE_ROUTER_DOC_NAME)
        if (!file.exists()) {
            if (file.parentFile.mkdirs()) {
                file.createNewFile()
            }
        }
        file.bufferedWriter().use {
            it.write(builder.toString())
        }
    }

    private fun scanInput(input: InputStream, docList: MutableList<String>) {
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
            if (name == BEE_ROUTER_MAPPING_PARAMS && value.toString().isNotEmpty()) {
                docList.add(value.toString().replace("[", "").replace("]", ""))
            }
            return super.visitField(access, name, descriptor, signature, value)
        }

        override fun visitEnd() {
            accept(cw)
        }
    }
}