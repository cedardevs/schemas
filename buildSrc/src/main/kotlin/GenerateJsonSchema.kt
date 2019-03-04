import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.avro.Avro2JsonSchemaProcessor
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ConsoleProcessingReport
import com.github.fge.jsonschema.core.tree.JsonTree
import com.github.fge.jsonschema.core.tree.SimpleJsonTree
import com.github.fge.jsonschema.core.util.ValueHolder
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * A task to generate json schema from a set of avro schema files
 */
open class GenerateJsonSchema : DefaultTask() {

  @Input
  var avroFiles: FileTree = project.fileTree("${project.projectDir}/src/main/resources") {
    it.include("**/*.avsc")
  }

  fun from(path: String) {
    avroFiles = project.fileTree(path)
  }

  fun from(fileTree: FileTree) {
    avroFiles = fileTree
  }

  @OutputDirectory
  var outDir: File = project.file("${project.buildDir}/generated/resources")

  fun into(path: String) {
    outDir = project.file(path)
  }

  fun into(file: File) {
    outDir = file
  }


  @TaskAction
  fun generate() {
    val sortedAvro = sortFiles(avroFiles.files)
    printInfo(sortedAvro)
    val tmpPath = "${project.buildDir}/tmp/${this.name}/combined.avsc"
    val combinedAvro = concatJsonFiles(sortedAvro.map { it.file }, tmpPath)
    generateJsonSchema(combinedAvro, outDir)
  }

  private fun sortFiles(files: Set<File>?): List<ComparableAvroFile> {
    return when {
      files != null -> files.map(::ComparableAvroFile).sorted()
      else -> listOf()
    }
  }

  private fun printInfo(files: List<ComparableAvroFile>) {
    files.forEach { file ->
      logger.info("${file.name} depends on:")
      file.dependencies.forEach { logger.info("  - $it") }
    }
    logger.info("sorted files:")
    files.forEach { logger.info("  - ${it.file}") }
  }

  private fun concatJsonFiles(inFiles: Collection<File>, outPath: String): File {
    val outFile = project.file(outPath)
    outFile.parentFile.mkdirs()
    outFile.delete()

    outFile.appendText("[\n")
    inFiles.forEachIndexed { i, file ->
      outFile.appendBytes(file.readBytes())
      if (i < inFiles.size - 1) {
        outFile.appendText(",\n")
      }
    }
    outFile.appendText("]\n")

    return outFile
  }

  private fun generateJsonSchema(avroSchema: File, outDir: File): File {
    val processor = Avro2JsonSchemaProcessor()
    val report = ConsoleProcessingReport()
    val input = ValueHolder.hold<JsonTree>(SimpleJsonTree(JsonLoader.fromFile(avroSchema)))
    val output = processor.process(report, input).value
    val mapper = ObjectMapper()
    val json = mapper.readValue(output.baseNode.toString(), Object().getClass())
    outDir.mkdirs()
    val outFile = File(outDir, "schema-definitions.json")
    mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, json)
    return outFile
  }

  data class ComparableAvroFile(val file: File) : Comparable<ComparableAvroFile> {

    private val knownAvroTypes: List<String> = listOf("null", "boolean", "int", "long", "float", "double", "bytes", "string")
    private val jsonMap: Map<String, Any> = ObjectMapper().readValue(file, mutableMapOf<String, Any>().javaClass)
    private val namespace: String = jsonMap["namespace"].toString()
    val name: String = buildName(jsonMap["name"].toString())
    val dependencies: List<String> = collectDependencies(jsonMap)

    private fun buildName(jsonName: String): String {
      return if (jsonName.contains(".")) jsonName else "$namespace.$jsonName"
    }

    private fun collectDependencies(json: Map<*,*>): List<String> {
      val fields = json["fields"]
      return if (fields is List<*>) {
        fields.flatMap(this::extractTypesFromField).filterNot(knownAvroTypes::contains).map(this::buildName).toSet().toList()
      } else {
        listOf()
      }
    }

    private fun extractTypesFromField(field: Any?): List<String> {
      return if (field is Map<*,*>) collectTypes(field["type"]) else listOf()
    }

    private fun collectTypes(type: Any?): List<String> {
      return if (type is String) {
        listOf(type)
      } else if (type is List<*>) {
        type.flatMap(this::collectTypes)
      } else if (type is Map<*,*> && type["type"] == "array") {
        collectTypes(type["items"])
      } else if (type is Map<*,*> && type["type"] == "record") {
        collectDependencies(type)
      } else {
        listOf()
      }
    }

    override fun compareTo(other: ComparableAvroFile): Int {
      // TODO - I'm definitely not sure that this will work every time... but it works for our current set of schemas!
      return if (dependencies.contains(other.name) || dependencies.size > other.dependencies.size) 1 else -1
    }
  }
}
