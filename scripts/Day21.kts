import java.io.File

run {
    var funcs = mapOf<String, Lazy<Long>>()
    funcs = File("../inputs/21.txt").readLines().associate {
        val splits = it.split(": ", " ")
        val name = splits[0]
        val f = if (splits.size == 2) {
            val n = splits[1].toLong()
            lazy { n }
        } else {
            val a1 = splits[1]
            val a2 = splits[3]
            when (splits[2][0]) {
                '+' -> lazy { funcs[a1]!!.value + funcs[a2]!!.value }
                '-' -> lazy { funcs[a1]!!.value - funcs[a2]!!.value }
                '*' -> lazy { funcs[a1]!!.value * funcs[a2]!!.value }
                '/' -> lazy { funcs[a1]!!.value / funcs[a2]!!.value }
                else -> lazy { 0L }
            }
        }
        Pair(name, f)
    }

    println("The value of root is ${funcs["root"]!!.value}")
}



sealed class Result {
    data class Val(val n: Long?) : Result()
    data class Wait(val f: (Long?) -> Long?) : Result()
    fun nAdd(a: Long?, b: Long?) = if (b == null) null else a?.plus(b)
    fun nSubtract(a: Long?, b: Long?) = if (b == null) null else a?.minus(b)
    fun nMultiply(a: Long?, b: Long?) = if (b == null) null else a?.times(b)
    fun nDivide(a: Long?, b: Long?) = if (b == null || b == 0L) null else a?.div(b)
    operator fun plus(other: Result) = when (this) {
        is Val -> when (other) {
            is Val -> Val(nAdd(this.n, other.n))
            is Wait -> Wait { target -> other.f(nSubtract(target, this.n)) }
        }
        is Wait -> when (other) {
            is Val -> Wait { target -> this.f(nSubtract(target, other.n)) }
            is Wait -> Val(null)
        }
    }
    operator fun minus(other: Result) = when (this) {
        is Val -> when (other) {
            is Val -> Val(nSubtract(this.n, other.n))
            is Wait -> Wait { target -> other.f(nSubtract(this.n, target)) }
        }
        is Wait -> when (other) {
            is Val -> Wait { target -> this.f(nAdd(other.n, target)) }
            is Wait -> Val(null)
        }
    }
    operator fun times(other: Result) = when (this) {
        is Val -> when (other) {
            is Val -> Val(nMultiply(this.n, other.n))
            is Wait -> Wait { target -> other.f(nDivide(target, this.n)) }
        }
        is Wait -> when (other) {
            is Val -> Wait { target -> this.f(nDivide(target, other.n)) }
            is Wait -> Val(null)
        }
    }
    operator fun div(other: Result) = when (this) {
        is Val -> when (other) {
            is Val -> Val(nDivide(this.n, other.n))
            is Wait -> Wait { target -> other.f(nDivide(this.n, target)) }
        }
        is Wait -> when (other) {
            is Val -> Wait { target -> this.f(nMultiply(other.n, target)) }
            is Wait -> Val(null)
        }
    }
    fun solveEqual(other: Result) = when (this) {
        is Val -> when (other) {
            is Val -> Val(null)
            is Wait -> Val(other.f(this.n))
        }
        is Wait -> when (other) {
            is Val -> Val(this.f(other.n))
            is Wait -> Val(null)
        }
    }
}

run {
    var funcs = mapOf<String, Lazy<Result>>()
    funcs = File("../inputs/21.txt").readLines().associate {
        val splits = it.split(": ", " ")
        val name = splits[0]
        val f = if (splits.size == 2) {
            if (name == "humn") {
                lazy { Result.Wait { a -> a } }
            } else {
                val n = splits[1].toLong()
                lazy { Result.Val(n) }
            }
        } else {
            val a1 = splits[1]
            val a2 = splits[3]
            if (name == "root") {
                lazy { funcs[a1]!!.value.solveEqual(funcs[a2]!!.value) }
            } else when (splits[2][0]) {
                '+' -> lazy { funcs[a1]!!.value + funcs[a2]!!.value }
                '-' -> lazy { funcs[a1]!!.value - funcs[a2]!!.value }
                '*' -> lazy { funcs[a1]!!.value * funcs[a2]!!.value }
                '/' -> lazy { funcs[a1]!!.value / funcs[a2]!!.value }
                else -> lazy { Result.Val(0L) }
            }
        }
        Pair(name, f)
    }

    val result = (funcs["root"]!!.value as Result.Val).n
    println("The equality test passes when the human provides the number $result.")
}