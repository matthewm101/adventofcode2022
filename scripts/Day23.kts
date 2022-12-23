import java.io.File

data class Pos(val x: Int, val y: Int) {
    operator fun plus(a: Pos) = Pos(x + a.x, y + a.y)
}
val RIGHT = Pos(1,0)
val LEFT = Pos(-1,0)
val DOWN = Pos(0,1)
val UP = Pos(0,-1)
val UPLEFT = UP + LEFT
val UPRIGHT = UP + RIGHT
val DOWNLEFT = DOWN + LEFT
val DOWNRIGHT = DOWN + RIGHT

val startingPositions = File("../inputs/23.txt").readLines().flatMapIndexed { y, l -> l.mapIndexed { x, c -> if (c == '#') Pos(x,y) else null } }.filterNotNull().toSet()

var positions = startingPositions
var cycle = 0
fun update(): Boolean {
    val posList = positions.toList()
    val nextPositions = posList.map { e ->
        val freeR = (e + RIGHT) !in positions
        val freeL = (e + LEFT) !in positions
        val freeD = (e + DOWN) !in positions
        val freeU = (e + UP) !in positions
        val freeUL = (e + UPLEFT) !in positions
        val freeUR = (e + UPRIGHT) !in positions
        val freeDL = (e + DOWNLEFT) !in positions
        val freeDR = (e + DOWNRIGHT) !in positions
        if (freeR && freeL && freeD && freeU && freeUL && freeUR && freeDL && freeDR) e
        else when (cycle) {
            0 -> if (freeU && freeUL && freeUR) e+UP else if (freeD && freeDL && freeDR) e+DOWN else if (freeL && freeUL && freeDL) e+LEFT else if (freeR && freeUR && freeDR) e+RIGHT else e
            1 -> if (freeD && freeDL && freeDR) e+DOWN else if (freeL && freeUL && freeDL) e+LEFT else if (freeR && freeUR && freeDR) e+RIGHT else if (freeU && freeUL && freeUR) e+UP else e
            2 -> if (freeL && freeUL && freeDL) e+LEFT else if (freeR && freeUR && freeDR) e+RIGHT else if (freeU && freeUL && freeUR) e+UP else if (freeD && freeDL && freeDR) e+DOWN else e
            3 -> if (freeR && freeUR && freeDR) e+RIGHT else if (freeU && freeUL && freeUR) e+UP else if (freeD && freeDL && freeDR) e+DOWN else if (freeL && freeUL && freeDL) e+LEFT else e
            else -> e
        }
    }
    val seenPositions = mutableSetOf<Pos>()
    val dupePositions = positions.toMutableSet()
    for (p in nextPositions) {
        if (p in seenPositions) dupePositions += p
        seenPositions += p
    }
    var changed = false
    positions = nextPositions.mapIndexed { i, np -> if (np in dupePositions) posList[i] else {changed = true; np} }.toSet()
    cycle = (cycle+1).mod(4)
    return changed
}

//fun printMap() {
//    val minX = positions.minOf { it.x }
//    val maxX = positions.maxOf { it.x }
//    val minY = positions.minOf { it.y }
//    val maxY = positions.maxOf { it.y }
//    for (y in minY..maxY) {
//        for (x in minX..maxX) {
//            if (Pos(x, y) in positions) print("#") else print(".")
//        }
//        println()
//    }
//    println()
//}
//printMap()

var counter = 0
while (counter < 10) {
    update()
    counter++
}

val minX = positions.minOf { it.x }
val maxX = positions.maxOf { it.x }
val minY = positions.minOf { it.y }
val maxY = positions.maxOf { it.y }
val rectangleArea = (maxX + 1 - minX) * (maxY + 1 - minY)
val emptySpace = rectangleArea - positions.size
println("After 10 rounds, the rectangle containing all the elves has $emptySpace empty spaces.")

do counter++ while (update())
println("All the elves stop moving on round $counter.")