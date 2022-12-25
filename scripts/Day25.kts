import java.io.File

fun fromSnafu(s: String): Long {
    var ctr = 0L
    s.forEach {
        when (it) {
            '-' -> ctr = ctr * 5 - 1
            '=' -> ctr = ctr * 5 - 2
            '0' -> ctr *= 5
            '1' -> ctr = ctr * 5 + 1
            '2' -> ctr = ctr * 5 + 2
            else -> throw Exception("bad input")
        }
    }
    return ctr
}

fun toSnafu(i: Long): String {
    var ctr = i
    var s = ""
    while (ctr > 0) {
        s = when(ctr.mod(5)) {
            0 -> '0'
            1 -> '1'
            2 -> '2'
            3 -> {ctr += 5; '='}
            4 -> {ctr += 5; '-'}
            else -> throw Exception("unreachable")
        } + s
        ctr /= 5
    }
    return s
}

val sum = File("../inputs/25.txt").readLines().sumOf { fromSnafu(it) }

println("The sum of all the numbers in decimal is ${sum}, which is ${toSnafu(sum)} in SNAFU.")