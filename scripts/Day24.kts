import java.io.File
import kotlin.math.max

data class Pos(val x: Int, val y: Int) {
    operator fun plus(a: Pos) = Pos(x + a.x, y + a.y)
}
val RIGHT = Pos(1,0)
val LEFT = Pos(-1,0)
val DOWN = Pos(0,1)
val UP = Pos(0,-1)

val raw = File("../inputs/24.txt").readLines()
val minWindX = 1
val minWindY = 1
val maxWindX = raw[0].length - 2
val maxWindY = raw.size - 2
val startingWalls = raw.flatMapIndexed { y, line -> line.mapIndexed { x, c -> if (c == '#') Pos(x,y) else null } }.filterNotNull().plus(Pos(1,-1)).plus(Pos(maxWindX,maxWindY+2)).toSet()
val startingLefts = raw.flatMapIndexed { y, line -> line.mapIndexed { x, c -> if (c == '<') Pos(x,y) else null } }.filterNotNull().toSet()
val startingRights = raw.flatMapIndexed { y, line -> line.mapIndexed { x, c -> if (c == '>') Pos(x,y) else null } }.filterNotNull().toSet()
val startingUps = raw.flatMapIndexed { y, line -> line.mapIndexed { x, c -> if (c == '^') Pos(x,y) else null } }.filterNotNull().toSet()
val startingDowns = raw.flatMapIndexed { y, line -> line.mapIndexed { x, c -> if (c == 'v') Pos(x,y) else null } }.filterNotNull().toSet()
val cycleTimeLR = maxWindX - minWindX + 1
val cycleTimeUD = maxWindY - minWindY + 1
val cycleTime = (max(cycleTimeLR,cycleTimeUD)..cycleTimeLR*cycleTimeUD).first { it % cycleTimeUD == 0 && it % cycleTimeLR == 0} // brute-force LCM calculation
val startPos = Pos(1,0)
val endPos = Pos(maxWindX,maxWindY+1)

fun wrap(p: Pos) =
    if (p.x > maxWindX) Pos(minWindX, p.y)
    else if (p.x < minWindX) Pos(maxWindX, p.y)
    else if (p.y > maxWindY) Pos(p.x, minWindY)
    else if (p.y < minWindY) Pos(p.x, maxWindY)
    else p

val wallList: MutableList<Set<Pos>> = mutableListOf(startingWalls + startingLefts + startingRights + startingUps + startingDowns)
var lefts = startingLefts
var rights = startingRights
var ups = startingUps
var downs = startingDowns
for (i in 1 until cycleTime) {
    lefts = lefts.map { wrap(it + LEFT) }.toSet()
    rights = rights.map { wrap(it + RIGHT) }.toSet()
    ups = ups.map { wrap(it + UP) }.toSet()
    downs = downs.map { wrap(it + DOWN) }.toSet()
    wallList += startingWalls + lefts + rights + ups + downs
}

data class State(val time: Int, val pos: Pos) {
    fun nexts(): List<State> {
        val t = time + 1
        val options = mutableListOf<State>()
        if (pos !in wallList[t.mod(cycleTime)]) options += State(t,pos)
        if ((pos + LEFT) !in wallList[t.mod(cycleTime)]) options += State(t,pos + LEFT)
        if ((pos + RIGHT) !in wallList[t.mod(cycleTime)]) options += State(t,pos + RIGHT)
        if ((pos + UP) !in wallList[t.mod(cycleTime)]) options += State(t,pos + UP)
        if ((pos + DOWN) !in wallList[t.mod(cycleTime)]) options += State(t,pos + DOWN)
        return options
    }
}

run {
    val start = State(0,startPos)
    var frontier = setOf(start)
    var solution: State?
    while (true) {
        frontier = frontier.flatMap { it.nexts() }.toSet()
        solution = frontier.find { it.pos == endPos }
        if (solution != null) break
    }
    println("The goal can be reached in ${solution!!.time} minutes.")
}

data class State2(val time: Int, val pos: Pos, val progress: Int) {
    fun nexts(): List<State2> {
        val t = time + 1
        val options = mutableListOf<State2>()
        if (pos !in wallList[t.mod(cycleTime)]) options += State2(t,pos, progress)
        if ((pos + LEFT) !in wallList[t.mod(cycleTime)]) options += State2(t,pos + LEFT, progress)
        if ((pos + RIGHT) !in wallList[t.mod(cycleTime)]) options += State2(t,pos + RIGHT, progress)
        if ((pos + UP) !in wallList[t.mod(cycleTime)]) options += State2(t,pos + UP, progress)
        if ((pos + DOWN) !in wallList[t.mod(cycleTime)]) options += State2(t,pos + DOWN, progress)
        for (i in options.indices) {
            if (options[i].progress == 0 && options[i].pos == endPos)
                options[i] = State2(options[i].time, options[i].pos, 1)
            if (options[i].progress == 1 && options[i].pos == startPos)
                options[i] = State2(options[i].time, options[i].pos, 2)
        }
        return options
    }
}

run {
    val start = State2(0,startPos,0)
    var frontier = setOf(start)
    var solution: State2?
    while (true) {
        frontier = frontier.flatMap { it.nexts() }.toSet()
        solution = frontier.find { it.pos == endPos && it.progress == 2 }
        if (solution != null) break
    }
    println("Going back, forth, and back again takes ${solution!!.time} minutes.")
}