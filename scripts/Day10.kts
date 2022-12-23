import java.io.File
import kotlin.math.abs

val program = File("../inputs/10.txt").readLines().map { it.split(" ").getOrNull(1)?.toInt() }

val coolCycles = setOf(20, 60, 100, 140, 180, 220)
val coolCycleValues: MutableList<Int> = mutableListOf()
val crt: MutableList<MutableList<Char>> = MutableList(6) { MutableList(40) { '.' } }

var x = 1
var state: Int? = null
var pc = 0
for (cycle in 0..239) {
    if (cycle + 1 in coolCycles) coolCycleValues.add(x * (cycle + 1))
    if (abs(x - (cycle % 40)) <= 1) crt[cycle / 40][cycle % 40] = '#'
    if (state != null) {
        x += state!!
        state = null
    } else {
        state = program[pc]
        pc++
    }
}

println("The sum of the six interesting signal strengths is ${coolCycleValues.sum()}.")
println("Image:")
println()
crt.forEach { println(it.joinToString(separator = "")) }