package com.bee.router.apt.processor

import com.bee.router.annotations.Router
import com.bee.router.apt.data.RouterData
import com.google.auto.service.AutoService
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.TypeElement

/**
 * BeeRouterProcessor
 * @author petterp
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.bee.router.annotations.Router")
class RouterProcessor : BaseProcessor() {

    // 增量编译的例子：https://blog.csdn.net/qfanmingyiq/article/details/116300913
    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment,
    ): Boolean {
        if (roundEnv.processingOver() || annotations.isEmpty()) return false
        logger.info("-------> start -----")
        generateRouterMappings(roundEnv)
        logger.info("------->  end  -----")
        return true
    }

    private fun generateRouterMappings(roundEnvironment: RoundEnvironment) {
        val elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Router::class.java)
        if (elementsAnnotatedWith.size < 1) return
        logger.info("-------> 收集到 ${elementsAnnotatedWith.size} 个使用 [BeeRouter] 的类-----")
        val mapping = mutableMapOf<String, RouterData>()
        val docJsons = JsonArray()
        elementsAnnotatedWith.forEach {
            (it as? TypeElement)?.let { element ->
                // 获取注解信息
                element.getAnnotation(Router::class.java)?.apply {
                    val realPath = element.qualifiedName.toString()
                    mapping[url] = RouterData(url, realPath, desc)
                    val json = JsonObject().apply {
                        addProperty("url", url)
                        addProperty("cla", realPath)
                        addProperty("desc", desc)
                    }
                    docJsons.add(json)
                    logger.info("-------> url:$url, className:$realPath -----")
                }
            }
        }
        writeMappingClass(mapping, docJsons)
    }

    private fun writeMappingClass(mapping: Map<String, RouterData>, docs: JsonArray) {
        val time = System.currentTimeMillis()
        val className = "_RouterMapping_$time"
        val mappingFullClassName = "com.bee.router.mapping.$className"
        val source = processingEnv.filer.createSourceFile(mappingFullClassName)
        val writeMappingClassContent = getMappingClassContent(className, mapping, docs)
        logger.info("-------> mappingClassName:$className -----")
        source.openWriter().use {
            it.write(writeMappingClassContent)
        }
    }

    private fun getMappingClassContent(
        className: String,
        map: Map<String, RouterData>,
        docs: JsonArray
    ): String {
        val builder = StringBuilder()
        builder.append("package com.bee.router.mapping;\n")
        builder.append("import androidx.annotation.Keep;\n\n")
        builder.append("import java.util.Map;\n")
        builder.append(
            "\n/**\n" +
                    " * 自动生成的路由表\n" +
                    " * @author petterp\n" +
                    " */\n",
        )
            .append("@Keep\n")
            .append("public class ").append(className)
            .append(" implements com.bee.router.core._IRouter ").append(" {\n\n")
            .append(String.format("    private final String params = \"%s\";\n\n",docs.toString().replace("\"", "\\\"")))
            .append("    @Override\n")
            .append("    public void init(Map<String, com.bee.router.core.RouterData> map) {\n")
        if (map.isNotEmpty()) {
            map.forEach { (url, data) ->
                builder.append("        map.put(\"$url\", new com.bee.router.core.RouterData(\"${data.url}\", \"${data.cla}\", \"${data.desc}\"));\n")
            }
        }
        builder.append("    }\n")
        builder.append("}")
        return builder.toString()
    }

//    private fun getRouterJsonObject(url: String, description: String, realPath: String) =
//        JsonObject().apply {
//            addProperty("url", url)
//            addProperty("description", description)
//            addProperty("realPath", realPath)
//        }
}
