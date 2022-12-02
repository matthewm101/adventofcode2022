import java.io.File

data class Elf(val foods: List<Long>) {
    fun total() = foods.sum()
}

val elves: List<Elf> = File("../inputs/1.txt").readLines().fold(mutableListOf(Elf(listOf())), {
    l: List<Elf>, s: String -> if (s.isEmpty()) {l + Elf(listOf())}
    else l.take(l.size-1) + Elf(l.last().foods + s.toLong())
} )

val hungriestElf = elves.maxBy { elf -> elf.total() }

println("The elf with the most calories has ${hungriestElf.total()} calories.")

val topThreeElves = elves.sortedBy { elf -> elf.total() }.reversed().take(3)

val topThreeElvesSum = topThreeElves.map { elf -> elf.total() }.sum()

println("The top three elves have $topThreeElvesSum calories total.")