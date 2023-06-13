package com.bee.router.apt.utils

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * from ARouter Logger(use kotlin to convert)
 * @author petterp
 */
class Logger(private val msg: Messager) {

    /**
     * Print info log.
     */
    fun info(info: CharSequence) {
        if (info.isEmpty()) return
        msg.printMessage(Diagnostic.Kind.NOTE, PREFIX_OF_LOGGER + info)
    }

    fun error(error: CharSequence) {
        if (error.isEmpty()) return
        msg.printMessage(
            Diagnostic.Kind.ERROR,
            "$PREFIX_OF_LOGGER An exception is encountered, [$error]"
        )
    }

    fun error(error: Throwable?) {
        if (error == null) return
        msg.printMessage(
            Diagnostic.Kind.ERROR,
            "$PREFIX_OF_LOGGER An exception is encountered, [${error.message}]\n" +
                    " ${formatStackTrace(error.stackTrace)}"
        )
    }

    fun warning(warning: CharSequence) {
        if (warning.isEmpty()) return
        msg.printMessage(Diagnostic.Kind.WARNING, PREFIX_OF_LOGGER + warning)
    }

    private fun formatStackTrace(stackTrace: Array<StackTraceElement>): String {
        val sb = StringBuilder()
        for (element in stackTrace) {
            sb.append("    at ").append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }

    companion object {
        private const val PREFIX_OF_LOGGER = "BeeRouter::apt "
    }
}