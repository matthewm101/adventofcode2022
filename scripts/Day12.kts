import java.io.File
import java.util.*

data class Pos(val x: Int, val y: Int) {
    fun up() = Pos(x, y-1)
    fun down() = Pos(x, y+1)
    fun left() = Pos(x-1, y)
    fun right() = Pos(x+1, y)
    fun neighbors() = listOf(up(), down(), left(), right())
}

data class Grid(val raw: List<String>) {
    val width = raw[0].length
    val height = raw.size
    val start = (0 until width).flatMap { x -> (0 until height).map { y -> if (at(Pos(x,y)) == 'S') Pos(x,y) else null } }.filterNotNull().first()
    val end = (0 until width).flatMap { x -> (0 until height).map { y -> if (at(Pos(x,y)) == 'E') Pos(x,y) else null } }.filterNotNull().first()
    fun at(pos: Pos) = raw[pos.y][pos.x]
    fun elev(pos: Pos) = if (at(pos) == 'E') 26 else if (at(pos) == 'S') 1 else at(pos) - 'a' + 1
    fun inbounds(pos: Pos) = (pos.x in 0 until width) && (pos.y in 0 until height)
    fun validUpwardsMove(pos1: Pos, pos2: Pos) = inbounds(pos1) && inbounds(pos2) && (elev(pos2) - elev(pos1) <= 1)
    fun validDownwardsMove(pos1: Pos, pos2: Pos) = inbounds(pos1) && inbounds(pos2) && (elev(pos1) - elev(pos2) <= 1)
}

val grid = Grid(File("../inputs/12.txt").readLines())

data class State(val steps: Int, val pos: Pos) : Comparable<State> {
    override fun compareTo(other: State): Int {
        return steps - other.steps
    }
    fun nextValidUpStates() = pos.neighbors().filter { grid.validUpwardsMove(pos, it) }.map { State(steps+1, it) }
    fun nextValidDownStates() = pos.neighbors().filter { grid.validDownwardsMove(pos, it) }.map { State(steps+1, it) }
}

run {
    val queue: PriorityQueue<State> = PriorityQueue()
    val start = State(0,grid.start)
    queue.add(start)
    val visited: MutableSet<Pos> = mutableSetOf()
    while (queue.isNotEmpty()) {
        val state = queue.remove()
        if (state.pos == grid.end) {
            println("The shortest path from the start to the goal takes ${state.steps} steps.")
            break
        }
        if (state.pos !in visited) {
            visited += state.pos
            queue.addAll(state.nextValidUpStates().filter { it.pos !in visited })
        }
    }
}

run {
    val queue: PriorityQueue<State> = PriorityQueue()
    val start = State(0,grid.end)
    queue.add(start)
    val visited: MutableSet<Pos> = mutableSetOf()
    while (queue.isNotEmpty()) {
        val state = queue.remove()
        if (grid.elev(state.pos) == 1) {
            println("The shortest path from any low point to a goal takes ${state.steps} steps.")
            break
        }
        if (state.pos !in visited) {
            visited += state.pos
            queue.addAll(state.nextValidDownStates().filter { it.pos !in visited })
        }
    }
}