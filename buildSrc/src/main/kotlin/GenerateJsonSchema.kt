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
import java.lang.IllegalStateException

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

  private fun sortFiles(files: Set<File>?): List<AvroFile> {
    if (files == null) {
      return listOf()
    }

    val schemas = files.map(::AvroFile).toSet()
    val schemasByName = schemas.associateBy { it.name }
    val edges = schemasByName.values.flatMap { file ->
      file.dependencies
          .map { depName -> schemasByName[depName] }
          .map { if (it == null) null else Edge(from=it, to=file) }
    }.filterNotNull().toSet()
    return topologicalSort(schemas, edges)
  }

  private fun printInfo(files: List<AvroFile>) {
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

  /**
   * Helper class to parse avro files and collect their dependencies, i.e. references to external record types
   */
  data class AvroFile(val file: File) {
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

    override fun toString(): String {
      return name
    }
  }

  /**
   * Helper class to represent a directed graph edge from one value to another
   */
  data class Edge<T>(val from: T, val to: T) {
    override fun toString(): String {
      return "$from --> $to"
    }
  }

  /**
   * Kahn's algorithm for topological sorting of DAGs!
   * see: https://en.wikipedia.org/wiki/Topological_sorting#Kahn's_algorithm
   *
   * L ← Empty list that will contain the sorted elements
   * S ← Set of all nodes with no incoming edge
   * while S is non-empty do
   *   remove a node n from S
   *   add n to tail of L
   *   for each node m with an edge e from n to m do
   *     remove edge e from the graph
   *     if m has no other incoming edges then
   *       insert m into S
   * if graph has edges then
   *   return error   (graph has at least one cycle)
   * else
   *   return L   (a topologically sorted order)
   */
  private fun <T> topologicalSort(nodes: Set<T>, edges: Set<Edge<T>>): List<T> {
    val result = mutableListOf<T>()
    val nodesWithIncoming = edges.map { it.to }
    val remainingEdges = edges.toMutableSet()
    val nodesWithoutIncoming = nodes.filter { !nodesWithIncoming.contains(it) }.toMutableSet()

    while (nodesWithoutIncoming.isNotEmpty()) {
      val node = nodesWithoutIncoming.first()
      nodesWithoutIncoming.remove(node)
      result.add(node)
      edges.filter { it.from == node }.forEach { e ->
        remainingEdges.remove(e)
        if (remainingEdges.none { it.to == e.to }) {
          nodesWithoutIncoming.add(e.to)
        }
      }
    }
    if (remainingEdges.isNotEmpty()) {
      throw IllegalStateException("The graph has a cycle in it; unable to sort topologically. The following edges are part of the cycle: $remainingEdges")
    }
    else {
      return result
    }
  }
}
