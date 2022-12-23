import java.io.File
import kotlin.math.abs
import kotlin.math.sign

val program = File("../inputs/9.txt").readLines().map {
    val splits = it.split(" ")
    Pair(splits[0][0], splits[1].toInt())
}

val visited: MutableSet<Pair<Int,Int>> = mutableSetOf(Pair(0,0))
var xh = 0
var yh = 0
var xt = 0
var yt = 0

program.forEach { (dir, len) ->
    var oldxt = xt
    var oldyt = yt

    if (dir == 'U') yh -= len
    if (dir == 'D') yh += len
    if (dir == 'L') xh -= len
    if (dir == 'R') xh += len
    val dx = xt - xh
    val dy = yt - yh
    if (dx > 1 && abs(dx) >= abs(dy)) { xt = xh + 1; if (dy != 0) yt = yh}
    if (dx < -1 && abs(dx) >= abs(dy)) { xt = xh - 1; if (dy != 0) yt = yh}
    if (dy > 1 && abs(dy) >= abs(dx)) {yt = yh + 1; if (dx != 0) xt = xh}
    if (dy < -1 && abs(dy) >= abs(dx)) {yt = yh - 1; if (dx != 0) xt = xh}

    while (oldxt != xt || oldyt != yt) {
        oldxt += (xt - oldxt).sign
        oldyt += (yt - oldyt).sign
        visited.add(Pair(oldxt, oldyt))
    }
}

println("After all the moves were simulated with a 2-knot rope, the tail visited ${visited.size} locations.")

val visited2: MutableSet<Pair<Int,Int>> = mutableSetOf(Pair(0,0))
val xs = MutableList(10) { 0 }
val ys = MutableList(10) { 0 }
fun dx(i: Int) = xs[i+1] - xs[i]
fun dy(i: Int) = ys[i+1] - ys[i]
fun adx(i: Int) = abs(dx(i))
fun ady(i: Int) = abs(dy(i))

program.forEach { (dir, len) ->
    for (n in 0 until len) {
        if (dir == 'U') ys[0] += 1
        if (dir == 'D') ys[0] -= 1
        if (dir == 'L') xs[0] -= 1
        if (dir == 'R') xs[0] += 1
        for (i in 0 until 9) {
            if (adx(i) > 1 || ady(i) > 1) {
                xs[i+1] -= dx(i).sign
                ys[i+1] -= dy(i).sign
            }
        }
        visited2.add(Pair(xs[9], ys[9]))
    }
}

println(visited2)
println("After all the moves were simulated with a 10-knot rope, the tail visited ${visited2.size} locations.")