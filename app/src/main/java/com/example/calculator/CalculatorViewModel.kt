package com.example.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.udojava.evalex.Expression

class CalculatorViewModel: ViewModel() {
    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> state = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
            is CalculatorAction.Bracket -> enterBracket(action.bracket)
        }
    }

    private fun performCalculation() {
        if (state.expression.isBlank()) return

        try {
            val result = Expression(state.expression.replace("x", "*"))
                .setPrecision(10)
                .eval()
            
            state = state.copy(
                expression = result.toPlainString().take(15)
            )
        } catch (e: Exception) {
            // If the expression is incomplete or invalid, we just don't calculate
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (state.expression.isNotBlank() && !isLastCharOperator()) {
            state = state.copy(
                expression = state.expression + operation.symbol
            )
        } else if (state.expression.isNotBlank() && isLastCharOperator()) {
            // Replace last operator
            state = state.copy(
                expression = state.expression.dropLast(1) + operation.symbol
            )
        }
    }

    private fun isLastCharOperator(): Boolean {
        if (state.expression.isEmpty()) return false
        val lastChar = state.expression.last().toString()
        return lastChar == "+" || lastChar == "-" || lastChar == "x" || lastChar == "/"
    }

    private fun enterDecimal() {
        // Simple decimal logic: only allow if the last part (current number) doesn't have a decimal
        val parts = state.expression.split("+", "-", "x", "/", "(", ")")
        val currentPart = parts.last()
        
        if (!currentPart.contains(".") && currentPart.isNotBlank()) {
            state = state.copy(
                expression = state.expression + "."
            )
        } else if (currentPart.isEmpty() && !isLastCharOperator()) {
             // Handle start with decimal if needed, but usually we want "0."
             state = state.copy(
                expression = state.expression + "0."
            )
        }
    }

    private fun enterNumber(number: Int) {
        state = state.copy(
            expression = state.expression + number
        )
    }

    private fun enterBracket(bracket: String) {
        state = state.copy(
            expression = state.expression + bracket
        )
    }

    private fun performDeletion() {
        if (state.expression.isNotBlank()) {
            state = state.copy(
                expression = state.expression.dropLast(1)
            )
        }
    }
}
