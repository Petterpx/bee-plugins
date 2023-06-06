package com.bee.router.processor

import com.bee.router.annotations.BeeRouter
import com.google.auto.service.AutoService
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * 路由注解处理器
 * @author petterp
 */
@AutoService(Processor::class)
class BeeRouterProcessor : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment,
    ): Boolean {
        if (roundEnv.processingOver()) return false
        // 获得相应路由mapping管理类与json内容
        val (mapping, jsonArray) = mappingManagerToJsonContent(roundEnv) ?: return false
        // 生成相应的mapping类和doc
        writeMappingClassManagerAndDoc(mapping, jsonArray)
        println("$TAG-------finish")
        return false
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(BeeRouter::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }

    private fun mappingManagerToJsonContent(roundEnvironment: RoundEnvironment): Pair<Map<String, String>, JsonArray>? {
        // 获取所有标记了@Router注解类的信息
        val elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(BeeRouter::class.java)
        // 当未收集到@Router注解时，跳过
        if (elementsAnnotatedWith.size < 1) return null
        println("$TAG-------start")
        println("$TAG------收集到 ${elementsAnnotatedWith.size} 个使用 [BeeRouter] 的类信息")
        val jsonArray = JsonArray()
        val mapping = mutableMapOf<String, String>()
        elementsAnnotatedWith.forEach {
            (it as? TypeElement)?.let { element ->
                // 获取注解信息
                element.getAnnotation(BeeRouter::class.java)?.apply {
                    // 获取注解类的全类名
                    val realPath = element.qualifiedName.toString()
                    // 为下面的mapping表做准备
                    mapping[url] = realPath
                    // 保存路由信息json
                    jsonArray.add(getRouterJsonObject(url, desc, realPath))
                    println("$TAG--->url:$url---->description:$desc---->className:$realPath")
                }
            }
        }
        return mapping to jsonArray
    }

    private fun writeMappingClassManagerAndDoc(mapping: Map<String, String>, jsonArray: JsonArray) {
        val time = System.currentTimeMillis()
        val className = "BeeRouterMapping_$time"

        val writeMappingClassContent = getMappingClassContent(className, mapping)
        val mappingFullClassName = "com.bee.router.mapping.$className"
        val source = processingEnv.filer.createSourceFile(mappingFullClassName)
        source.openWriter().use {
            it.write(writeMappingClassContent)
        }

        val docName = "BeeRouterMapping_doc_$time.json"
        val docFile = File(File(source.toUri()).parent, docName)
        docFile.bufferedWriter().use {
            it.write(jsonArray.toString())
        }

        println("$TAG-----> mappingClassName = $className")
        println("$TAG-----> mappingClassDoc = $docName")
    }

    private fun getRouterJsonObject(url: String, description: String, realPath: String) =
        JsonObject().apply {
            addProperty("url", url)
            addProperty("description", description)
            addProperty("realPath", realPath)
        }

    private fun getMappingClassContent(className: String, map: Map<String, String>): String {
        // 将要自动生成的类的类名
        val builder = StringBuilder()
        builder.append("package com.petterp.router.mapping;\n")
        builder.append("import java.util.HashMap;\n")
        builder.append("import java.util.Map;\n")
        builder.append(
            "\n/**\n" +
                " * 自动生成的路由表\n" +
                " * @author petterp\n" +
                " */\n",
        )
            .append("public class ").append(className).append(" {\n")
            .append("    private static final HashMap<String, String> mapping = new HashMap<>();\n")
            .append(
                "    public static Map<String, String> getMapping() {\n" +
                    "        return mapping;\n" +
                    "    }\n",
            ).append(
                "\n    protected static void addKeyValue(String key, String value) {\n" +
                    "        mapping.put(key, value);\n" +
                    "    }",
            )
        if (map.isNotEmpty()) {
            builder.append("\n    static {\n")
            map.forEach { (url, realPath) ->
                builder.append("      addKeyValue(\"$url\",\"$realPath\");\n")
            }
            builder.append("    }\n")
        }
        builder.append("}\n")
        return builder.toString()
    }

    companion object {
        private const val TAG = "BeeRouterProcessor"
    }
}
