import java.io.File
import kotlin.math.absoluteValue

data class Interval(val a: Int, val b: Int) {
    val size = b - a + 1

    fun tryMerge(other: Interval): Interval? {
        if (a <= other.a && other.b <= b) return this
        if (other.a <= a && b <= other.b) return other
        if (other.a in a..b) return Interval(a, other.b)
        if (other.b in a..b) return Interval(other.a, b)
        return null
    }

    fun inside(c: Int) = c in a..b
}

data class Reading(val x: Int, val y: Int, val bx: Int, val by: Int) {
    val dist = (x - bx).absoluteValue + (y - by).absoluteValue
    fun getRowDeadZone(row: Int): Interval? {
        val rowDist = (y - row).absoluteValue
        if (rowDist > dist) return null
        val radius = dist - rowDist
        return Interval(x - radius, x + radius)
    }
}

fun getRowDeadZones(row: Int): Set<Interval> {
    val deadZones = readings.mapNotNull { it.getRowDeadZone(row) }.toMutableSet()
    while (true) {
        val dzList = deadZones.toList()
        var foundA: Interval = dzList[0]
        var foundB: Interval = dzList[0]
        var foundAB: Interval? = null
        outer@ for (i in dzList.indices) {
            foundA = dzList[i]
            for (j in i+1 until dzList.size) {
                foundB = dzList[j]
                foundAB = foundA.tryMerge(foundB)
                if (foundAB != null) break@outer
            }
        }
        if (foundAB != null) {
            deadZones.remove(foundA)
            deadZones.remove(foundB)
            deadZones.add(foundAB)
        } else break
    }
    return deadZones
}

fun checkForGap(dzs: Set<Interval>, end: Int): Int? {
    var potentialGap = 0
    while (true) {
        var changed = false
        for (dz in dzs) {
            if (dz.inside(potentialGap)) {
                potentialGap = dz.b + 1
                changed = true
            }
        }
        if (potentialGap > end) return null
        if (!changed) return potentialGap
    }
}

val readings = File("../inputs/15.txt").readLines().map { line ->
    val splits = line.split("Sensor at x=", ", y=", ": closest beacon is at x=").filter { it.isNotEmpty() }
    Reading(splits[0].toInt(), splits[1].toInt(), splits[2].toInt(), splits[3].toInt())
}

val targetRow = 2000000
val targetRowDeadZones = getRowDeadZones(targetRow)

val totalDeadZoneSize = targetRowDeadZones.sumOf { it.size }
val beaconsInDeadZone = readings.map { Pair(it.bx,it.by) }.toSet().count { (x,y) -> y == targetRow && targetRowDeadZones.any { it.inside(x) } }
val totalDefiniteNonBeacons = totalDeadZoneSize - beaconsInDeadZone
println("There are $totalDefiniteNonBeacons positions in row $targetRow that cannot contain a beacon.")

val end = 4000000
var gapX = 0
var gapY = 0
for (row in 0..end) {
    val maybeGap = checkForGap(getRowDeadZones(row), end)
    if (maybeGap != null) {
        gapX = maybeGap
        gapY = row
        break
    }
}
println("The distress signal is at ($gapX,$gapY) with tuning frequency ${gapX.toLong() * 4000000L + gapY.toLong()}.")