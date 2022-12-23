import java.io.File
import kotlin.math.sign

data class Pos(val x: Int, val y: Int) {
    fun down() = Pos(x, y+1)
    fun downleft() = Pos(x-1, y+1)
    fun downright() = Pos(x+1, y+1)
    fun inchToward(p: Pos) = Pos(x + (p.x - x).sign, y + (p.y - y).sign)
}

data class World(val paths: List<List<Pos>>) {
    val xMin = paths.flatMap { path -> path.map { it.x } }.min() - 1
    val xMax = paths.flatMap { path -> path.map { it.x } }.max() + 1
    val yMin = 0
    val yMax = paths.flatMap { path -> path.map { it.y } }.max() + 1

    fun build(): MutableSet<Pos> {
        val world: MutableSet<Pos> = mutableSetOf()
        for (path in paths) {
            var i = 0
            var pos = path[0]
            world.add(path[0])
            while (i < path.size - 1) {
                i++
                while (pos != path[i]) {
                    pos = pos.inchToward(path[i])
                    world.add(pos)
                }
            }
        }
        return world
    }

    fun sim1(): Int {
        val world = build()
        var count = 0
        while (true) {
            var sand = Pos(500,0)
            while (true) {
                if (sand.x == xMin || sand.x == xMax || sand.y == yMax) break
                sand = if (sand.down() !in world) sand.down()
                else if (sand.downleft() !in world) sand.downleft()
                else if (sand.downright() !in world) sand.downright()
                else break
            }
            if (sand.x == xMin || sand.x == xMax || sand.y == yMax) break
            world.add(sand)
            count += 1
        }
        return count
    }

    fun sim2(): Int {
        val world = build()
        var count = 0
        while (true) {
            var sand = Pos(500,0)
            while (true) {
                if (sand.y == yMax) break
                sand = if (sand.down() !in world) sand.down()
                else if (sand.downleft() !in world) sand.downleft()
                else if (sand.downright() !in world) sand.downright()
                else break
            }
            world.add(sand)
            count += 1
            if (sand == Pos(500, 0)) break
        }
        return count
    }
}

val world = World(File("../inputs/14.txt").readLines().map { line -> line.split(" -> ").map { Pos(it.split(",")[0].toInt(), it.split(",")[1].toInt()) } })
val simResult1 = world.sim1()
println("During the first simulation, $simResult1 grains of sand came to rest.")
val simResult2 = world.sim2()
println("During the second simulation, $simResult2 grains of sand came to rest.")