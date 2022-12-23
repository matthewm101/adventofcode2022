import java.io.File

data class Pair(val start1: Int, val end1: Int, val start2: Int, val end2: Int) {
    fun nested() = (start1 <= start2 && end2 <= end1) || (start2 <= start1 && end1 <= end2)
    fun overlaps() = nested() || (start2 in start1..end1) || (end2 in start1..end1)
}

val pairs = File("../inputs/4.txt").readLines().map {
    val splits = it.split("-", ",").map { s -> s.toInt() }
    Pair(splits[0], splits[1], splits[2], splits[3])
}

val nestedPairCount = pairs.count { it.nested() }
println("There are $nestedPairCount pairs with nested assignments.")

val overlapPairCount = pairs.count { it.overlaps() }
println("There are $overlapPairCount pairs with overlapping assignments.")