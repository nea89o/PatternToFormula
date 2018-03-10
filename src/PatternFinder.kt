import java.lang.Math.floor
import java.lang.Math.max
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

data class Expression(val expression: String)

fun main(args:Array<String>) {
    while (true) {
        println("---------------------------")
        println("Separate numbers by comma.")
        print("Enter numbers: ")

        val inputNumbers = readLine()!!.split(",").map { it.trim().toDouble() }
        val formula = findFormula(inputNumbers)
        println("Formula: $formula")
        println("Pattern: " + continuePattern(formula))
    }
}

private fun findFormula(numbers: List<Double>): String {
    // Find difference between numbers
    val differences = listOf(
            numbers[1] - numbers[0],
            numbers[2] - numbers[1]
    )

    var incrementsByDifference = true
    var increment = 0.0
    if (differences[0] != differences[1]) // If the pattern doesn't increment by difference
        incrementsByDifference = false
    else
        increment = differences[0]

    // Start-value
    val nAsExponent = nAsExponent(differences)
    val startValueDifference = when {
        incrementsByDifference -> differences[0]             // xn
        nAsExponent            -> getBase(differences) // x^n
        else                   -> 1.0                        // n^x
    }

    val startValue = numbers[0] - startValueDifference

    // Exponents
    var base = "n"
    var exponent = ""
    if (nAsExponent(differences)) {
        base = getBase(differences).cleanRedundancy() + "^"
        exponent = "n"
    } else if (!incrementsByDifference) {
        base = "n^"
        exponent = getExponent(numbers[1], startValue).cleanRedundancy()
    }

    // Add the pieces together
    var formula = increment.cleanRedundancy()
    formula += base + exponent + startValue.cleanRedundancy().addSign()

    return formula
}

private fun continuePattern(formula: String): String {
    var output = ""
    for (i in 1..20) {
        val expression = Expression(formula.replace("n", i.toString()))
        output += expression.calculate().cleanRedundancy() + ", "
    }

    return output.cleanRedundancy()
}

fun Double.cleanRedundancy(): String {
    return when {
        this == 0.0         -> return ""
        floor(this) == this -> this.toInt().toString()
        else                -> return this.toString()
    }
}

fun String.cleanRedundancy(): String {
    return if (this.endsWith(", "))
        this.substring(0, this.length - 3)
    else
        this
}

fun String.addSign(): String {
    return if (!this.startsWith("-"))
        "+" + this
    else
        this
}

fun Expression.calculate(): Double {
    val addSubSign = max(expression.indexOf("+"), expression.indexOf("-"))
    val addSub = expression.substring(addSubSign + 1, expression.length).toDouble()
    val numbers = expression.substring(0, addSubSign)
            .split(Regex("[*^]")).map { it.toDouble() }

    if (expression.contains("*"))
        return numbers[0] * numbers[1] + addSub
    else if (expression.contains("^"))
        return Math.pow(numbers[0], numbers[1]) + addSub
    return 0.0
}

private fun getBase(differences: List<Double>): Double {
    return differences[1] / differences[0]
}

private fun getExponent(secondNumber: Double, startValue: Double): Double {
    return Math.log(secondNumber - startValue) / Math.log(2.0)
}

private fun nAsExponent(differences: List<Double>): Boolean {
    val base = getBase(differences)
    return  base == differences[0] / (differences[0] / base)
}