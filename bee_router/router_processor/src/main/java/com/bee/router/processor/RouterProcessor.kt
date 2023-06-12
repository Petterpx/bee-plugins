package com.bee.router.processor

import com.bee.router.annotations.Router
import com.bee.router.core.RouterData
import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * 路由注解处理器
 * @author petterp
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("com.bee.router.annotations.Router")
class RouterProcessor : AbstractProcessor() {

    // 增量编译的例子：https://blog.csdn.net/qfanmingyiq/article/details/116300913
    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment,
    ): Boolean {
        if (roundEnv.processingOver()) return false
        // 获得相应路由mapping管理类与json内容
        val mapping = mappingManagerToJsonContent(roundEnv) ?: return false
        writeMappingClass(mapping)
        println("$TAG-------finish")
        return false
    }

    private fun mappingManagerToJsonContent(roundEnvironment: RoundEnvironment): Map<String, RouterData>? {
        // 获取所有标记了@Router注解类的信息
        val elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Router::class.java)
        // 当未收集到@Router注解时，跳过
        if (elementsAnnotatedWith.size < 1) return null
        println("$TAG-------start")
        println("$TAG------收集到 ${elementsAnnotatedWith.size} 个使用 [BeeRouter] 的类信息")
        val mapping = mutableMapOf<String, RouterData>()
        elementsAnnotatedWith.forEach {
            (it as? TypeElement)?.let { element ->
                // 获取注解信息
                element.getAnnotation(Router::class.java)?.apply {
                    // 获取注解类的全类名
                    val realPath = element.qualifiedName.toString()
                    // 为下面的mapping表做准备
                    mapping[url] = RouterData(url, desc, realPath)
                    println("$TAG--->url:$url---->description:$desc---->className:$realPath")
                }
            }
        }
        return mapping
    }

    private fun writeMappingClass(mapping: Map<String, RouterData>) {
        val time = System.currentTimeMillis()
        val className = "RouterMapping_$time"

        val mappingFullClassName = "com.bee.router.mapping.$className"
        val source = processingEnv.filer.createSourceFile(mappingFullClassName)
        val writeMappingClassContent = getMappingClassContent(className, mapping)
        source.openWriter().use {
            it.write(writeMappingClassContent)
        }
        println("$TAG-----> mappingClassName = $className")
//        val docName = "BeeRouterMapping_doc_$time.json"
//        val docFile = File(File(source.toUri().path).parent, docName)
//        docFile.bufferedWriter().use {
//            it.write(jsonArray.toString())
//        }
//        println("$TAG-----> mappingClassDoc = $docName")
    }

    private fun getMappingClassContent(
        className: String,
        map: Map<String, RouterData>
    ): String {
        // 将要自动生成的类的类名
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
            .append(" implements com.bee.router.core._IRouter ").append(" {\n")
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

    companion object {
        private const val TAG = "BeeRouterProcessor"
    }
}
