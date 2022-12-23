import java.io.File
import kotlin.math.max

data class Pos(val x: Int, val y: Int) {
    operator fun plus(a: Pos) = Pos(x + a.x, y + a.y)
    fun inbounds() = x in 0..6 && y >= 0
}

val RIGHT = Pos(1, 0)
val LEFT = Pos(-1, 0)
val DOWN = Pos(0, -1)

data class Block(val blocks: List<Pos>) {
    val width = blocks.maxOf { it.x } + 1
    val height = blocks.maxOf { it.y } + 1
    fun at(pos: Pos) = blocks.map { it + pos }
}

val block1 = Block(listOf(Pos(0,0), Pos(1,0), Pos(2,0), Pos(3,0)))
val block2 = Block(listOf(Pos(1,0), Pos(0,1), Pos(1,1), Pos(2, 1), Pos(1, 2)))
val block3 = Block(listOf(Pos(0,0), Pos(1,0), Pos(2,0), Pos(2,1), Pos(2,2)))
val block4 = Block(listOf(Pos(0,0), Pos(0,1), Pos(0,2), Pos(0,3)))
val block5 = Block(listOf(Pos(0,0), Pos(1,0), Pos(0,1), Pos(1,1)))
val blockCycle = listOf(block1, block2, block3, block4, block5)

val wind = File("../inputs/17.txt").readText().map { if (it == '>') RIGHT else LEFT }

data class State(val block: Int, val wind: Int, val h1: Int, val h2: Int, val h3: Int, val h4: Int, val h5: Int, val h6: Int, val h7: Int)

class Tower {
    var blockIndex = 0
    var windIndex = 0
    val tower = mutableSetOf<Pos>()
    var height = 0

    fun drop() {
        val block = blockCycle[blockIndex++]
        if (blockIndex == 5) blockIndex = 0

        var blockPos = Pos(2, height + 3)
        while (true) {
            if (block.at(blockPos + wind[windIndex]).all { it.inbounds() && it !in tower }) blockPos += wind[windIndex]
            windIndex++
            if (windIndex == wind.size) windIndex = 0
            if (block.at(blockPos + DOWN).all {it.inbounds() && it !in tower}) blockPos += DOWN
            else {
                val newBlocks = block.at(blockPos)
                height = max(height, newBlocks.maxOf { it.y } + 1)
                tower.addAll(newBlocks)
                break
            }
        }
    }

    fun printTower() {
        for (i in height downTo 0) {
            print("|")
            (0..6).forEach { if (Pos(it,i) in tower) print("#") else print(".") }
            println("|")
        }
        println("+-------+")
        println()
    }

    fun probe(i: Int): Int {
        var j = 0
        while (height-j-1 >= 0 && Pos(i,height-j-1) !in tower) j++
        return j
    }

    fun dumpState() = State(blockIndex, windIndex, probe(0), probe(1), probe(2), probe(3), probe(4), probe(5), probe(6))
}

run {
    val tower = Tower()
    for (i in 1..2022) {
        tower.drop()
    }
    println("After 2022 blocks are dropped, the tower's height is ${tower.height}.")
}



run {
    val tower = Tower()
    val states = mutableMapOf(tower.dumpState() to Pair(0,0L))
    val heights = mutableListOf(0L)
    var cycleStart = 0L
    var cycleLength = 0L
    var cycleHeightGain = 0L
    for (i in 1..1000000) {
        tower.drop()
        heights += tower.height.toLong()
        val last = states.put(tower.dumpState(), Pair(i,tower.height.toLong()))
        if (last != null) {
            cycleStart = last.first.toLong()
            cycleLength = i - cycleStart
            cycleHeightGain = tower.height.toLong() - last.second
            break
        }
    }
    val elapsedCycles = (1000000000000 - cycleStart) / cycleLength
    val lastCycles = (1000000000000 - cycleStart) % cycleLength
    val totalCycleHeight = elapsedCycles * cycleHeightGain
    val startingEndingHeight = heights[(cycleStart + lastCycles).toInt()]
    val finalHeight = totalCycleHeight + startingEndingHeight
    println("After a trillion blocks are dropped, the final height is $finalHeight.")
}