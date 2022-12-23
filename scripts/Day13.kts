import java.io.File
import kotlin.math.max

sealed class Data {
    data class Packet(val data: List<Data>) : Data(), Comparable<Packet> {
        override fun toString() = data.joinToString(separator = ",", prefix = "[", postfix = "]") { it.toString() }
        override fun compareTo(other: Packet): Int {
            for (i in 0 until max(data.size, other.data.size)) {
                if (i >= data.size) return -1
                if (i >= other.data.size) return 1
                val e1 = data[i]
                val e2 = other.data[i]
                when (e1) {
                    is Num -> when (e2) {
                        is Num -> if (e1.num != e2.num) return e1.num - e2.num
                        is Packet -> {
                            val result = Packet(listOf(e1)).compareTo(e2)
                            if (result != 0) return result
                        }
                    }
                    is Packet -> when (e2) {
                        is Num -> {
                            val result = e1.compareTo(Packet(listOf(e2)))
                            if (result != 0) return result
                        }
                        is Packet -> {
                            val result = e1.compareTo(e2)
                            if (result != 0) return result
                        }
                    }
                }
            }
            return 0
        }
    }
    data class Num(val num: Int) : Data(), Comparable<Num> {
        override fun toString() = num.toString()
        override fun compareTo(other: Num) = num - other.num
    }
}

sealed class Token {
    object In: Token()
    object Out: Token()
    data class Num(val num: Int): Token()
}

fun parsePacket(line: String): Data.Packet? {
    val tokens: List<Token> = line.fold<List<Token?>>(listOf()) {
        prev, c -> when(c) {
            '[' -> prev.plus(Token.In)
            ']' -> prev.plus(Token.Out)
            ',' -> prev.plus(null)
            else -> if (prev.last() != null && prev.last() is Token.Num)
                prev.dropLast(1).plus(Token.Num((prev.last() as Token.Num).num * 10 + c.digitToInt()))
            else prev.plus(Token.Num(c.digitToInt()))
        }
    }.filterNotNull()
    val packetStack: MutableList<MutableList<Data>> = mutableListOf()
    var finalPacket: Data.Packet? = null
    tokens.forEach {
        when(it) {
            is Token.In -> packetStack.add(mutableListOf())
            is Token.Out -> {
                val p = packetStack.removeLast()
                if (packetStack.isEmpty()) finalPacket = Data.Packet(p)
                else packetStack.last().add(Data.Packet(p))
            }
            is Token.Num -> packetStack.last().add(Data.Num(it.num))
        }
    }
    return finalPacket
}

data class PacketPair(val left: Data.Packet, val right: Data.Packet) {
    fun ordered() = left < right
}
val packets = File("../inputs/13.txt").readLines().mapNotNull { parsePacket(it) }
val packetPairs = packets.chunked(2).map { PacketPair(it[0], it[1]) }
val indexSum = packetPairs.mapIndexed{ index, packetPair -> if (packetPair.ordered()) index + 1 else 0 }.sum()
println("There sum of the indices of the ordered pairs is $indexSum.")

val divider1 = Data.Packet(listOf(Data.Packet(listOf(Data.Num(2)))))
val divider2 = Data.Packet(listOf(Data.Packet(listOf(Data.Num(6)))))
val sortedPackets = packets.plus(divider1).plus(divider2).sorted()
val index1 = sortedPackets.indexOf(divider1)
val index2 = sortedPackets.indexOf(divider2)
println("The product of the indices of the divider packets is ${(index1 + 1) * (index2 + 1)}.")