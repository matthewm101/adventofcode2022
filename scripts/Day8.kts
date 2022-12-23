import java.io.File

data class Grid(val raw: List<String>) {
    fun at(x: Int, y: Int) = raw[y][x]
    fun width() = raw[0].length
    fun height() = raw.size
    fun exposed(): Set<Pair<Int,Int>> {
        val exposed: MutableSet<Pair<Int,Int>> = mutableSetOf()
        for (i in 0 until width()) {
            exposed += Pair(i, 0)
            exposed += Pair(i, height()-1)
        }
        for (j in 0 until height()) {
            exposed += Pair(0, j)
            exposed += Pair(width()-1, j)
        }

        for (x in 1 until width()-1) {
            for (y in 1 until height()-1) {
                var leftClear = true
                for (i in x-1 downTo 0) if (at(i,y) >= at(x,y)) {leftClear = false; break}
                if (leftClear) {exposed += Pair(x,y); continue}
                var rightClear = true
                for (i in x+1 until width()) if (at(i,y) >= at(x,y)) {rightClear = false; break}
                if (rightClear) {exposed += Pair(x,y); continue}
                var upClear = true
                for (j in y-1 downTo 0) if (at(x,j) >= at(x,y)) {upClear = false; break}
                if (upClear) {exposed += Pair(x,y); continue}
                var downClear = true
                for (j in y+1 until height()) if (at(x,j) >= at(x,y)) {downClear = false; break}
                if (downClear) {exposed += Pair(x,y); continue}
            }
        }

        return exposed
    }

    fun score(x: Int, y: Int): Int {
        var leftCount = 0
        for (i in x-1 downTo 0) if (at(i,y) >= at(x,y)) {leftCount++; break} else {leftCount++}
        var rightCount = 0
        for (i in x+1 until width()) if (at(i,y) >= at(x,y)) {rightCount++; break} else {rightCount++}
        var upCount = 0
        for (j in y-1 downTo 0) if (at(x,j) >= at(x,y)) {upCount++; break} else {upCount++}
        var downCount = 0
        for (j in y+1 until height()) if (at(x,j) >= at(x,y)) {downCount++; break} else {downCount++}
        return leftCount * rightCount * upCount * downCount
    }
}

val grid = Grid(File("../inputs/8.txt").readLines())
val exposed = grid.exposed()
println("The number of exposed trees is ${exposed.size}.")

val bestScore = (0 until grid.width()).flatMap { x -> (0 until grid.height()).map { y -> grid.score(x,y) } }.max()
println("The best scenic score is ${bestScore}.")