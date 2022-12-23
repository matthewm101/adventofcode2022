import java.io.File

data class CrateStack(val stack: MutableList<Char>) {
    fun pop() = stack.removeLast()
    fun push(c: Char) = stack.add(c)
    fun peek() = stack.last()
    fun clone() = CrateStack(stack.toMutableList())
}

data class Instruction(val count: Int, val start: Int, val end: Int)

val stacks9000: MutableList<CrateStack> = mutableListOf()
fun applyInstruction9000(i: Instruction) {
    for (x in 1..i.count) {
        stacks9000[i.end].push(stacks9000[i.start].pop())
    }
}

val lines = File("../inputs/5.txt").readLines()
val blankLineIndex = lines.indexOf("")
for (i in 0.until((lines[blankLineIndex - 1].length + 2) / 4)) {
    stacks9000.add(CrateStack(mutableListOf()))
}
for (i in (blankLineIndex - 2) downTo 0) {
    for (j in 0.until(stacks9000.size)) {
        val index = j * 4 + 1
        if (index < lines[i].length && lines[i][index] != ' ')
            stacks9000[j].push(lines[i][index])
    }
}

val stacks9001: List<CrateStack> = stacks9000.map { it.clone() }
fun applyInstruction9001(i: Instruction) {
    val tempstack = mutableListOf<Char>()
    for (x in 1..i.count) {
        tempstack.add(stacks9001[i.start].pop())
    }
    for (x in 1..i.count) {
        stacks9001[i.end].push(tempstack.removeLast())
    }
}

val insts = lines.subList(blankLineIndex+1, lines.size).map { line ->
    val splits = line.split("move ", " from ", " to ").filter { it.isNotEmpty() }.map { it.toInt() }
    Instruction(splits[0], splits[1] - 1, splits[2] - 1)
}

insts.forEach { applyInstruction9000(it) }
val message9000 = stacks9000.map { it.peek() }.joinToString(separator = "")

println("After following the CrateMover 9000 instructions, the top crates spell out $message9000.")

insts.forEach { applyInstruction9001(it) }
val message9001 = stacks9001.map { it.peek() }.joinToString(separator = "")

println("After following the CrateMover 9001 instructions, the top crates spell out $message9001.")