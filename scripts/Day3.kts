import java.io.File

data class Sack(val first: Set<Int>, val second: Set<Int>) {
    fun match() = first.intersect(second)
    fun all() = first.union(second)
}

data class Group(val first: Sack, val second: Sack, val third: Sack) {
    fun badge() = first.all().intersect(second.all()).intersect(third.all())
}

fun adjustValue(c: Char) = if (c >= 'A' && c <= 'Z') c.code - 65 + 27 else c.code - 97 + 1

val sacks = File("../inputs/3.txt").readLines().map { line ->
    val chars = line.map { adjustValue(it) }
    Sack(chars.subList(0, chars.size / 2).toSet(), chars.subList(chars.size / 2, chars.size).toSet())
}

val matchSum = sacks.map { sack ->
    val set = sack.match()
    assert(set.size == 1)
    set.first()
}.sum()

println("The sum of the priorities of the items that appear in both compartments in each sack is $matchSum.")

val groups = (0).until(sacks.size / 3).map { Group(sacks[it*3], sacks[it*3+1], sacks[it*3+2]) }

val badgeSum = groups.map { group ->
    val set = group.badge()
    assert(set.size == 1)
    set.first()
}.sum()

println("The sum of the priorities of the items that appear in all sacks in each group is $badgeSum.")