import java.io.File

data class Cube(val x: Int, val y: Int, val z: Int) {
    operator fun plus(c: Cube) = Cube(x+c.x,y+c.y,z+c.z)
    fun neighbors() = listOf(Cube(1,0,0),Cube(-1,0,0),Cube(0,1,0),Cube(0,-1,0),Cube(0,0,1),Cube(0,0,-1)).map { this + it }
}

val droplet = File("../inputs/18.txt").readLines().map { val splits = it.split(","); Cube(splits[0].toInt(),splits[1].toInt(),splits[2].toInt()) }.toSet()
val surfaceArea = droplet.sumOf { cube -> cube.neighbors().count { it !in droplet } }
println("The surface area of the droplet (including pockets) is $surfaceArea.")

val xMin = droplet.minOf { it.x }
val xMax = droplet.maxOf { it.x }
val yMin = droplet.minOf { it.y }
val yMax = droplet.maxOf { it.y }
val zMin = droplet.minOf { it.z }
val zMax = droplet.maxOf { it.z }

val air = (xMin..xMax).flatMap { x -> (yMin..yMax).flatMap { y -> (zMin..zMax).map { z -> Cube(x,y,z) } } }.filter { it !in droplet }.toMutableSet()
var frontier = setOf(Cube(xMin,yMin,zMin))
while (frontier.isNotEmpty()) {
    frontier = frontier.flatMap { it.neighbors() }.filter { it in air }.toSet()
    air.removeAll(frontier)
}

val filledDroplet = droplet + air
val fixedSurfaceArea = filledDroplet.sumOf { cube -> cube.neighbors().count { it !in filledDroplet } }
println("The surface area of the droplet (without pockets) is $fixedSurfaceArea.")