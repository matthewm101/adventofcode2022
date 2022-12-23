import java.io.File
import kotlin.math.max

data class Blueprint(val id: Int, val orePerOreRobot: Int, val orePerClayRobot: Int, val orePerObsidianRobot: Int, val clayPerObsidianRobot: Int, val orePerGeodeRobot: Int, val obsidianPerGeodeRobot: Int)

val blueprints = File("../inputs/19.txt").readLines().map { line ->
    val s = line.split("Blueprint ",": Each ore robot costs "," ore. Each clay robot costs "," ore. Each obsidian robot costs "," ore and "," clay. Each geode robot costs "," ore and "," obsidian.")
        .filter { it.isNotEmpty() }.map { it.toInt() }
    Blueprint(s[0],s[1],s[2],s[3],s[4],s[5],s[6])
}

data class State(val time: Int, val ore: Int, val oreBots: Int, val clay: Int, val clayBots: Int, val obsidian: Int, val obsidianBots: Int, val score: Int) {
    fun nexts(bp: Blueprint, scoreToBeat: Int): List<State> {
        val scoreDiff = scoreToBeat - score
        val geodeOnlyScore = time * (time - 1) / 2
        val relaxedScore = (time - 1) * (time - 2) / 2
        if (scoreDiff >= geodeOnlyScore) return listOf()
        val noGeodeOnly = scoreDiff < relaxedScore

        val ns = mutableListOf<State>()
        val t1 = (1..time).firstOrNull { ore + oreBots * (it-1) >= bp.orePerOreRobot }
        val t2 = (1..time).firstOrNull { ore + oreBots * (it-1) >= bp.orePerClayRobot }
        val t3 = (1..time).firstOrNull { ore + oreBots * (it-1) >= bp.orePerObsidianRobot && clay + clayBots * (it-1) >= bp.clayPerObsidianRobot }
        val t4 = (1..time).firstOrNull { ore + oreBots * (it-1) >= bp.orePerGeodeRobot && obsidian + obsidianBots * (it-1) >= bp.obsidianPerGeodeRobot }
        if (noGeodeOnly && t1 != null) ns += State(time-t1,ore+oreBots*t1-bp.orePerOreRobot,oreBots+1,clay+clayBots*t1,clayBots,obsidian+obsidianBots*t1,obsidianBots,score)
        if (noGeodeOnly && t2 != null) ns += State(time-t2,ore+oreBots*t2-bp.orePerClayRobot,oreBots,clay+clayBots*t2,clayBots+1,obsidian+obsidianBots*t2,obsidianBots,score)
        if (noGeodeOnly && t3 != null) ns += State(time-t3,ore+oreBots*t3-bp.orePerObsidianRobot,oreBots,clay+clayBots*t3-bp.clayPerObsidianRobot,clayBots,obsidian+obsidianBots*t3,obsidianBots+1,score)
        if (t4 != null) ns += State(time-t4,ore+oreBots*t4-bp.orePerGeodeRobot,oreBots,clay+clayBots*t4,clayBots,obsidian+obsidianBots*t4-bp.obsidianPerGeodeRobot,obsidianBots,score+time-t4)
        return ns
    }

    override fun toString() = "t=$time r=${oreBots}t+$ore c=${clayBots}t+${clay} o=${obsidianBots}t+$obsidian s=$score"
}

run {
    val qualities = mutableMapOf<Int,Int>()
    val start = State(24,0,1,0,0,0,0,0)
    for (bp in blueprints) {
        var frontier = setOf(start)
        var bestScore = 0
        while (frontier.isNotEmpty()) {
            frontier = frontier.flatMap { it.nexts(bp, bestScore) }.toSet()
            bestScore = max(bestScore, frontier.maxOfOrNull { it.score }?:0)
        }
        qualities[bp.id] = bestScore * bp.id
    }
    println("The sum of the qualities of all blueprints is ${qualities.values.sum()}.")
}

run {
    val start = State(32,0,1,0,0,0,0,0)
    val product = blueprints.subList(0,3).map { bp ->
        var frontier = setOf(start)
        var bestScore = 0
        while (frontier.isNotEmpty()) {
            frontier = frontier.flatMap { it.nexts(bp, bestScore) }.toSet()
            bestScore = max(bestScore, frontier.maxOfOrNull { it.score }?:0)
        }
        bestScore
    }.reduce { acc, i -> acc * i }
    println("The product of the best 32-minute scores of the first three blueprints is ${product}.")
}