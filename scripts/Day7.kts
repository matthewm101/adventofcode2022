import java.io.File

data class File(val size: Int, val name: String, val parent: Dir)
data class Dir(val name: String, val files: MutableMap<String, File>, val dirs: MutableMap<String, Dir>, val parent: Dir?) {
    fun size(): Int = files.values.sumOf { it.size } + dirs.values.sumOf { it.size() }
}

val root = Dir("/", mutableMapOf(), mutableMapOf(), null)
val directories: MutableList<Dir> = mutableListOf(root)
val files: MutableList<File> = mutableListOf()

var currentDir = root

val start = "^"
val dollar = "\\${'$'}"
val end = "${'$'}"
val dot = "\\."
val cdIn = Regex("$start$dollar cd ([a-z]+)$end")
val cdOut = Regex("$start$dollar cd $dot$dot$end")
val printedDir = Regex("${start}dir ([a-z]+)$end")
val printedFile = Regex("${start}([0-9]+) ([a-z.]+)$end")

File("../inputs/7.txt").forEachLine {
    val cdInMatch = cdIn.matchEntire(it)?.groups?.get(1)?.value
    val dirMatch = printedDir.matchEntire(it)?.groups?.get(1)?.value
    val sizeMatch = printedFile.matchEntire(it)?.groups?.get(1)?.value?.toIntOrNull()
    val filenameMatch = printedFile.matchEntire(it)?.groups?.get(2)?.value
    if (cdOut.matches(it)) {
        currentDir = currentDir.parent!!
//        println("Going up to dir ${currentDir.name}.")
    }
    else if (cdInMatch != null) {
        currentDir = currentDir.dirs[cdInMatch]!!
//        println("Going down to dir ${currentDir.name}.")
    }
    else if (dirMatch != null) {
        val newDir = Dir(dirMatch, mutableMapOf(), mutableMapOf(), currentDir)
        currentDir.dirs[dirMatch] = newDir
        directories.add(newDir)
//        println("Made ${newDir.name} a child directory of ${currentDir.name}.")
    }
    else if (sizeMatch != null && filenameMatch != null) {
        val newFile = File(sizeMatch, filenameMatch, currentDir)
        currentDir.files[filenameMatch] = newFile
        files.add(newFile)
//        println("Made ${newFile.name} a child file of ${currentDir.name}.")
    }
}

val totalSmallDirSize = directories.sumOf { if (it.size() <= 100000) { it.size() } else 0 }
println("The sum of the sizes of all small (<=100000) directories is $totalSmallDirSize.")

val smallest30mDir = directories.filter { it.size() >= root.size() - 40000000 }.minOf { it.size() }
println("The smallest directory that, if removed, leaves 30M unused space has a size of $smallest30mDir.")