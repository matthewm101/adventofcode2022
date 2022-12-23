import java.io.File

val initial = File("../inputs/20.txt").readLines().mapIndexed { index, s -> Pair(s.toLong(), index) }
val modulus = initial.size - 1
val key = 811589153

run {
    val l = initial.toMutableList()
    initial.forEach { p ->
        val start = l.indexOf(p)
        if (p.first != 0L) {
            l.removeAt(start)
            var end = (start + p.first).mod(modulus)
            if (end == 0) end = modulus
            l.add(end, p)
        }
    }
    val zeroIndex = l.indexOfFirst { it.first == 0L }
    val n1000 = l[(zeroIndex+1000).mod(l.size)].first
    val n2000 = l[(zeroIndex+2000).mod(l.size)].first
    val n3000 = l[(zeroIndex+3000).mod(l.size)].first
    println("The 1000th, 2000th, and 3000th numbers after 0 are $n1000, $n2000, and $n3000. These sum to ${n1000+n2000+n3000}.")
}

run {
    val decrypted = initial.map { Pair(it.first * key, it.second) }
    val l = decrypted.toMutableList()
    for (i in 1..10) {
        decrypted.forEach { p ->
            val start = l.indexOf(p)
            if (p.first != 0L) {
                l.removeAt(start)
                var end = (start + p.first).mod(modulus)
                if (end == 0) end = modulus
                l.add(end, p)
            }
        }
    }
    val zeroIndex = l.indexOfFirst { it.first == 0L }
    val n1000 = l[(zeroIndex+1000).mod(l.size)].first
    val n2000 = l[(zeroIndex+2000).mod(l.size)].first
    val n3000 = l[(zeroIndex+3000).mod(l.size)].first
    println("The grove coordinates are $n1000, $n2000, and $n3000. These sum to ${n1000+n2000+n3000}.")
}