import java.io.File
import java.lang.Integer.max

val raw = File("../inputs/6.txt").readText()
var sop = 4
while (raw.subSequence(sop-4, sop).toSet().size < 4) sop++
println("The start-of-packet marker is found after reading $sop characters.")

var som = max(sop, 14)
while (raw.subSequence(som-14, som).toSet().size < 14) som++
println("The start-of-message marker is found after reading $som characters.")