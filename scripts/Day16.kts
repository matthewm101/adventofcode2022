import java.io.File
import kotlin.math.max

data class Valve(val name: String, val flow: Int, val neighbors: Set<String>)

val allValves = File("../inputs/16.txt").readLines().map {
    val splits = it.split("Valve ", " has flow rate=", "; tunnels lead to valves ", "; tunnel leads to valve ").filter { s -> s.isNotEmpty() }
    Valve(splits[0], splits[1].toInt(), splits[2].split(", ").toSet())
}.associateBy { it.name }

val bestValves = allValves.values.filter { it.flow > 0 }.associateBy { it.name }
val bestValvesIndex = listOf(allValves["AA"]!!).plus(bestValves.values.toList())
val BVRI = bestValvesIndex.mapIndexed { index, valve -> Pair(valve.name, index) }.toMap()
val nValves = bestValvesIndex.size
val flowIndex = bestValvesIndex.map { it.flow }

fun calcDist(a: Valve, b: Valve): Int {
    var frontier: Set<String> = mutableSetOf(a.name)
    var dist = 0
    while (b.name !in frontier) {
        frontier = frontier.flatMap { allValves[it]!!.neighbors }.toSet()
        dist++
    }
    return dist
}

// To index into dist: start_index * nValves + end_index
val dist = bestValvesIndex.flatMap { a -> bestValvesIndex.map { b -> Pair(BVRI[a.name]!! * nValves + BVRI[b.name]!!, calcDist(a,b))} }.sortedBy { it.first }.map { it.second }

data class State(val pos: Int, val score: Int, val time: Int, val rem: Int) {
    fun expand() = (1 until nValves).mapNotNull {
        val remainingTime = time - dist[pos * nValves + it] - 1
        val mask = (1).shl(it)
        if (rem.and(mask) != 0 && remainingTime >= 0) State(it, score + remainingTime * flowIndex[it], remainingTime, rem.and(mask.inv())) else null
    }
}

run {
    val start = State(0, 0, 30, (1).shl(nValves) - 2)
    val visited = mutableSetOf(start)
    var frontier = visited.toSet()
    while (true) {
        frontier = frontier.flatMap { it.expand() }.toSet()
        if (frontier.isEmpty()) break
        visited.addAll(frontier)
    }
    val pressureHighScore = visited.maxOf { it.score }
    println("The max possible pressure released by one person in 30 minutes is ${pressureHighScore}.")
}

run {
    val start = State(0, 0, 26, (1).shl(nValves) - 2)
    val visited = mutableSetOf(start)
    var frontier = visited.toSet()
    while (true) {
        frontier = frontier.flatMap { it.expand() }.toSet()
        if (frontier.isEmpty()) break
        visited.addAll(frontier)
    }
    val visitedList = visited.toList()
    val scores = visitedList.map { it.score }
    val twists = visitedList.map { it.rem.xor((1).shl(nValves) - 2) }
    var maxScore = 0
    for (i in twists.indices) {
        for (j in i+1 until twists.size) {
            if (twists[i].and(twists[j]) == 0) maxScore = max(maxScore, scores[i] + scores[j])
        }
    }
    println("The max possible pressure released by one person and one elephant in 26 minutes is ${maxScore}.")
}