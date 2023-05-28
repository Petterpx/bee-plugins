package com.bee.analysis

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 *
 * @author petterp
 */
class AnalysisClassVisitor(private val classVisitor: ClassVisitor?) : ClassNode(Opcodes.ASM9) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor {
        println("---visitMethod----$name")
        return CustomMethodVistor(access, name, descriptor, signature, exceptions)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?,
    ): FieldVisitor {
        println("---visitField---$name,--$value,---$descriptor,---$signature")
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitSource(file: String?, debug: String?) {
        super.visitSource(file, debug)
        println("---visitSource,---$file")
    }

    override fun visitNestHost(nestHost: String?) {
        super.visitNestHost(nestHost)
        println("---visitNestHost,---$nestHost")
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?,
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("----> 开始访问: $name")
    }

    override fun visitEnd() {
        super.visitEnd()
        accept(classVisitor)
        println("----> 结束访问 $name")
        println("------------------")
    }
}
