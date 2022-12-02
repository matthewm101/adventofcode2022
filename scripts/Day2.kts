import java.io.File

enum class Hand(val handScore: Long) {Rock(1), Paper(2), Scissors(3)}
fun charToHand(c: Char) = when(c) {
    'A', 'X' -> Hand.Rock
    'B', 'Y' -> Hand.Paper
    'C', 'Z' -> Hand.Scissors
    else -> Hand.Rock
}

enum class Goal(val goalScore: Long) {Loss(0), Tie(3), Win(6)}
fun charToGoal(c: Char) = when(c) {
    'X' -> Goal.Loss
    'Y' -> Goal.Tie
    'Z' -> Goal.Win
    else -> Goal.Tie
}

fun judge(yours: Hand, theirs: Hand) = when(yours) {
    Hand.Rock -> when(theirs) {
        Hand.Rock -> Goal.Tie
        Hand.Paper -> Goal.Loss
        Hand.Scissors -> Goal.Win
    }
    Hand.Paper -> when(theirs) {
        Hand.Rock -> Goal.Win
        Hand.Paper -> Goal.Tie
        Hand.Scissors -> Goal.Loss
    }
    Hand.Scissors -> when(theirs) {
        Hand.Rock -> Goal.Loss
        Hand.Paper -> Goal.Win
        Hand.Scissors -> Goal.Tie
    }
}

fun reverseJudge(theirs: Hand, goal: Goal) = when(goal) {
    Goal.Win -> when(theirs) {
        Hand.Rock -> Hand.Paper
        Hand.Paper -> Hand.Scissors
        Hand.Scissors -> Hand.Rock
    }
    Goal.Tie -> theirs
    Goal.Loss -> when(theirs) {
        Hand.Rock -> Hand.Scissors
        Hand.Paper -> Hand.Rock
        Hand.Scissors -> Hand.Paper
    }
}

data class RoundV1(val opponent: Hand, val player: Hand) {
    fun playerScore() = judge(player, opponent).goalScore + player.handScore
}

val roundsV1: List<RoundV1> = File("../inputs/2.txt").readLines().map {
    s -> RoundV1(charToHand(s[0]), charToHand(s[2]))
}.toList()

val totalPlayerScoreV1 = roundsV1.map { r -> r.playerScore() }.sum()

println("Using the initial instructions, the player's total score from all rounds is $totalPlayerScoreV1.")

data class RoundV2(val opponent: Hand, val goal: Goal) {
    fun playerScore() = goal.goalScore + reverseJudge(opponent, goal).handScore
}

val roundsV2: List<RoundV2> = File("../inputs/2.txt").readLines().map {
        s -> RoundV2(charToHand(s[0]), charToGoal(s[2]))
}.toList()

val totalPlayerScoreV2 = roundsV2.map { r -> r.playerScore() }.sum()

println("Using the fixed instructions, the player's total score from all rounds is $totalPlayerScoreV2.")