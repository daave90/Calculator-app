package pl.daveprojects.calculatorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var lastNumeric = false
    var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onButtonClick(view: View) {
        val btnText = (view as Button).text.toString()
        if (isNumber(btnText)) {
            onNumber(btnText)
        } else {
            onDigit(btnText)
        }
    }

    private fun isNumber(string: String): Boolean {
        return string.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    private fun onNumber(number: String) {
        val tvInput = findViewById<TextView>(R.id.tvInput)
        tvInput.append(number)
        lastNumeric = true
    }

    private fun onDigit(btnText: String) {
        when (btnText) {
            "/", "*", "-", "+" -> onOperator(btnText)
            "CLR" -> clearTextView()
            "." -> displayDecimalPoint()
            "=" -> onEqual()
        }
    }

    private fun onOperator(btnText: String) {
        val tvInput = findViewById<TextView>(R.id.tvInput)
        if (tvInput.text.isEmpty() && btnText.equals("-")) {
            tvInput.append(btnText)
        }
        if (lastNumeric && !isOperatorAdded(tvInput.text.toString())) {
            tvInput.append(btnText)
            lastNumeric = false
            lastDot = false
        }
    }

    private fun isOperatorAdded(value: String): Boolean {
        return if (value.startsWith("-")) {
            false
        } else {
            value.contains("/") || value.contains("*") ||
                    value.contains("-") || value.contains("+")
        }
    }

    private fun clearTextView() {
        val tvInput = findViewById<TextView>(R.id.tvInput)
        tvInput.text = ""
        lastNumeric = false
        lastDot = false
    }

    private fun displayDecimalPoint() {
        if (lastNumeric && !lastDot) {
            val tvInput = findViewById<TextView>(R.id.tvInput)
            tvInput.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    private fun onEqual() {
        val tvInput = findViewById<TextView>(R.id.tvInput)
        if (lastNumeric) {
            var tvValue = tvInput.text.toString()
            val prefix = setupPrefixIfStartsWithMinus(tvValue)
            tvValue = removeMinusFromTvValueIfPrefixNotEmpty(prefix, tvValue)
            try {
                calculateTvValueByOperator(tvValue, prefix)
            } catch (e: ArithmeticException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupPrefixIfStartsWithMinus(tvValue: String): String {
        if (tvValue.startsWith("-")) {
            return "-"
        }
        return ""
    }

    private fun removeMinusFromTvValueIfPrefixNotEmpty(prefix: String, tvValue: String): String {
        if (prefix.isEmpty()) {
            return tvValue
        }
        return tvValue.substring(1)
    }

    private fun calculateTvValueByOperator(tvValue: String, prefix: String) {
        if (tvValue.contains("+")) {
            calculateTvValue(tvValue, "+", prefix)
        } else if (tvValue.contains("-")) {
            calculateTvValue(tvValue, "-", prefix)
        } else if (tvValue.contains("*")) {
            calculateTvValue(tvValue, "*", prefix)
        } else if (tvValue.contains("/")) {
            calculateTvValue(tvValue, "/", prefix)
        } else {
            throw ArithmeticException("Operator not supported")
        }
    }

    private fun calculateTvValue(tvValue: String, operator: String, prefix: String) {
        val splitValue = tvValue.split(operator)
        val one = (prefix + splitValue[0]).toDouble()
        val two = splitValue[1].toDouble()
        val result = removeLastZeroFromResult(calculate(one, two, operator).toString())

        val tvInput = findViewById<TextView>(R.id.tvInput)
        tvInput.text = result
    }

    private fun calculate(one: Double, two: Double, operator: String): Double {
        return when (operator) {
            "+" -> one + two
            "-" -> one - two
            "*" -> one * two
            "/" -> one / two
            else -> throw ArithmeticException("Operator not supported")
        }
    }

    private fun removeLastZeroFromResult(result: String): String {
        if (result.endsWith(".0")) {
            return result.substring(0, result.length - 2)
        }
        return result
    }
}