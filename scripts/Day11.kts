import java.io.File

data class Monkey(val items: MutableList<Long>, val op: (Long) -> Long, val divisor: Long, val trueTarget: Int, val falseTarget: Int, var inspects: Long)

val lines = File("../inputs/11.txt").readLines()

run {
    val monkeys = (lines.indices step 7).map { i ->
        val items = lines[i + 1].drop(18).split(", ").map { it.toLong() }.toMutableList()
        val op = if (lines[i + 2][23] == '*') {
            if (lines[i + 2].drop(25) == "old") { x: Long -> x * x }
            else { x: Long -> x * lines[i + 2].drop(25).toLong() }
        } else if (lines[i + 2][23] == '+') {
            if (lines[i + 2].drop(25) == "old") { x: Long -> x + x }
            else { x: Long -> x + lines[i + 2].drop(25).toLong() }
        } else {
            throw Exception("Unhandled operation: ${lines[i + 2].drop(13)}")
        }
        val divisor = lines[i + 3].drop(21).toLong()
        val trueTarget = lines[i + 4].drop(29).toInt()
        val falseTarget = lines[i + 5].drop(30).toInt()
        Monkey(items, op, divisor, trueTarget, falseTarget, 0)
    }

    for (round in 1..20) {
        monkeys.forEach { monkey ->
            monkey.items.forEach { item ->
                val newItem = monkey.op(item) / 3
                if (newItem % monkey.divisor == 0L) monkeys[monkey.trueTarget].items += newItem
                else monkeys[monkey.falseTarget].items += newItem
                monkey.inspects++
            }
            monkey.items.clear()
        }
    }

    val monkeyBusiness =
        monkeys.sortedBy { it.inspects }.drop(monkeys.size - 2).fold(1L) { acc, monkey -> acc * monkey.inspects }
    println("The monkey business after 20 rounds (with worry) is $monkeyBusiness.")
}

run {
    val monkeys = (lines.indices step 7).map { i ->
        val items = lines[i + 1].drop(18).split(", ").map { it.toLong() }.toMutableList()
        val op = if (lines[i + 2][23] == '*') {
            if (lines[i + 2].drop(25) == "old") { x: Long -> x * x }
            else { x: Long -> x * lines[i + 2].drop(25).toLong() }
        } else if (lines[i + 2][23] == '+') {
            if (lines[i + 2].drop(25) == "old") { x: Long -> x + x }
            else { x: Long -> x + lines[i + 2].drop(25).toLong() }
        } else {
            throw Exception("Unhandled operation: ${lines[i + 2].drop(13)}")
        }
        val divisor = lines[i + 3].drop(21).toLong()
        val trueTarget = lines[i + 4].drop(29).toInt()
        val falseTarget = lines[i + 5].drop(30).toInt()
        Monkey(items, op, divisor, trueTarget, falseTarget, 0)
    }

    val lcm = monkeys.map { it.divisor }.reduce { acc, i -> acc * i }

    for (round in 1..10000) {
        monkeys.forEach { monkey ->
            monkey.items.forEach { item ->
                val newItem = monkey.op(item % lcm)
                if (newItem % monkey.divisor == 0L) monkeys[monkey.trueTarget].items += newItem
                else monkeys[monkey.falseTarget].items += newItem
                monkey.inspects++
            }
            monkey.items.clear()
        }
    }

    val monkeyBusiness =
        monkeys.sortedBy { it.inspects }.drop(monkeys.size - 2).fold(1L) { acc, monkey -> acc * monkey.inspects }
    println("The monkey business after 10000 rounds (without worry) is $monkeyBusiness.")
}