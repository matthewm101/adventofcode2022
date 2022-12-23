import java.io.File

val raw = File("../inputs/22.txt").readLines()
val blankLineIndex = raw.indexOf("")

sealed class Inst {
    object Left: Inst()
    object Right: Inst()
    data class Move(val n: Int): Inst()
}
val instructions = raw[blankLineIndex+1].replace("L", " L ").replace("R", " R ").split(" ").map {
    when (it) {
        "L" -> Inst.Left
        "R" -> Inst.Right
        else -> Inst.Move(it.toInt())
    }
}

data class Pos(val x: Int, val y: Int) {
    operator fun plus(a: Pos) = Pos(x + a.x, y + a.y)
}

val map: MutableMap<Pos,Boolean> = mutableMapOf()

for (y in 0 until blankLineIndex) {
    for (x in 0 until raw[y].length) {
        if (raw[y][x] == '#') map[Pos(x,y)] = true
        else if (raw[y][x] == '.') map[Pos(x,y)] = false
    }
}

val width = map.keys.maxOf { it.x } + 1
val height = map.keys.maxOf { it.y } + 1

// Index using a y value to get the minimum/maximum x value in that row
val xMins = (0 until height).map { y -> (0 until width).first { x -> Pos(x,y) in map } }
val xMaxes = (0 until height).map { y -> (0 until width).last { x -> Pos(x,y) in map } }
// Index using an x value to get the minimum/maximum y value in that row
val yMins = (0 until width).map { x -> (0 until height).first { y -> Pos(x,y) in map } }
val yMaxes = (0 until width).map { x -> (0 until height).last { y -> Pos(x,y) in map } }

val RIGHT = Pos(1,0)
val LEFT = Pos(-1,0)
val DOWN = Pos(0,1)
val UP = Pos(0,-1)

val start = Pos(xMins[0],0)
val startingDir = RIGHT
fun rotateRight(p: Pos) = when(p) {
    RIGHT -> DOWN
    DOWN -> LEFT
    LEFT -> UP
    UP -> RIGHT
    else -> Pos(0,0)
}
fun rotateLeft(p: Pos) = when(p) {
    RIGHT -> UP
    DOWN -> RIGHT
    LEFT -> DOWN
    UP -> LEFT
    else -> Pos(0,0)
}
fun canonicalDir(p: Pos) = when(p) {
    RIGHT -> 0
    DOWN -> 1
    LEFT -> 2
    UP -> 3
    else -> 0
}

run {
    var pos = start
    var dir = startingDir
    for (i in instructions) {
        when (i) {
            is Inst.Left -> dir = rotateLeft(dir)
            is Inst.Right -> dir = rotateRight(dir)
            is Inst.Move -> {
                for (counter in 0 until i.n) {
                    var nextPos = pos + dir
                    if (dir.x != 0) {
                        if (nextPos.x < xMins[pos.y]) nextPos = Pos(xMaxes[nextPos.y], nextPos.y)
                        else if (nextPos.x > xMaxes[pos.y]) nextPos = Pos(xMins[nextPos.y], nextPos.y)
                    }
                    if (dir.y != 0) {
                        if (nextPos.y < yMins[pos.x]) nextPos = Pos(nextPos.x, yMaxes[nextPos.x])
                        else if (nextPos.y > yMaxes[pos.x]) nextPos = Pos(nextPos.x, yMins[nextPos.x])
                    }
                    assert (nextPos in map.keys)
                    if (map[nextPos]!!) break
                    pos = nextPos
                }
            }
        }
    }
    val endingColumn = pos.x + 1
    val endingRow = pos.y + 1
    val endingDir = canonicalDir(dir)
    val result = endingRow * 1000 + endingColumn * 4 + endingDir
    println("Final column: $endingColumn, Final row: $endingRow, Final dir: $endingDir, Password: $result")
}

run {
    var pos = start
    var dir = startingDir
    for (i in instructions) {
        when (i) {
            is Inst.Left -> dir = rotateLeft(dir)
            is Inst.Right -> dir = rotateRight(dir)
            is Inst.Move -> {
                for (counter in 0 until i.n) {
                    var nextPos = pos + dir
                    var nextDir = dir

                    // we do a little bit of hardcoding
                    // my cube looks like this (1 through E are edge labels, # are faces, | and - are connected edges):

                    //    6 7
                    //   5#-#8
                    //    | 9
                    //   4#A
                    //  3 |
                    // 2#-#B
                    //  | C
                    // 1#D
                    //  E

                    if (nextPos !in map) {
                        if (nextPos.x == -1 && nextPos.y in 150..199 && dir == LEFT) {nextPos = Pos(nextPos.y-100,0); nextDir = DOWN}
                        else if (nextPos.x == -1 && nextPos.y in 100..149 && dir == LEFT) {nextPos = Pos(50,149-nextPos.y); nextDir = RIGHT}
                        else if (nextPos.y == 99 && nextPos.x in 0..49 && dir == UP) {nextPos = Pos(50,50+nextPos.x); nextDir = RIGHT}
                        else if (nextPos.x == 49 && nextPos.y in 50..99 && dir == LEFT) {nextPos = Pos(nextPos.y-50,100); nextDir = DOWN}
                        else if (nextPos.x == 49 && nextPos.y in 0..49 && dir == LEFT) {nextPos = Pos(0,149-nextPos.y); nextDir = RIGHT}
                        else if (nextPos.y == -1 && nextPos.x in 50..99 && dir == UP) {nextPos = Pos(0,nextPos.x+100); nextDir = RIGHT}
                        else if (nextPos.y == -1 && nextPos.x in 100..149 && dir == UP) {nextPos = Pos(nextPos.x-100,199); nextDir = UP}
                        else if (nextPos.x == 150 && nextPos.y in 0..49 && dir == RIGHT) {nextPos = Pos(99,149-nextPos.y); nextDir = LEFT}
                        else if (nextPos.y == 50 && nextPos.x in 100..149 && dir == DOWN) {nextPos = Pos(99,nextPos.x-50); nextDir = LEFT}
                        else if (nextPos.x == 100 && nextPos.y in 50..99 && dir == RIGHT) {nextPos = Pos(nextPos.y+50,49); nextDir = UP}
                        else if (nextPos.x == 100 && nextPos.y in 100..149 && dir == RIGHT) {nextPos = Pos(149,149-nextPos.y); nextDir = LEFT}
                        else if (nextPos.y == 150 && nextPos.x in 50..99 && dir == DOWN) {nextPos = Pos(49,nextPos.x+100); nextDir = LEFT}
                        else if (nextPos.x == 50 && nextPos.y in 150..199 && dir == RIGHT) {nextPos = Pos(nextPos.y-100,149); nextDir = UP}
                        else if (nextPos.y == 200 && nextPos.x in 0..49 && dir == DOWN) {nextPos = Pos(nextPos.x+100,0); nextDir = DOWN}
                        else throw Exception("Unhandled movement out of bounds: $nextPos, $dir")
                    }

                    if (map[nextPos]!!) break
                    pos = nextPos
                    dir = nextDir
                }
            }
        }
    }
    val endingColumn = pos.x + 1
    val endingRow = pos.y + 1
    val endingDir = canonicalDir(dir)
    val result = endingRow * 1000 + endingColumn * 4 + endingDir
    println("Final column: $endingColumn, Final row: $endingRow, Final dir: $endingDir, Password: $result")
}