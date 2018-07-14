package kg.auca.example.converter;

import java.math.BigDecimal;
import java.util.Stack;

public class Calculator {
    public enum Operation {
        NONE,
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        MODULO
    }

    private Stack<BigDecimal> operands;
    private Operation operation;
    private boolean topOperandHasDecimalPoint;

    public BigDecimal getCurrentValue() {
        return operands.peek();
    }

    public Calculator() {
        init();
    }

    public final void init() {
        if (operands == null) {
            operands = new Stack<>();
        }

        operands.clear();
        operands.push(BigDecimal.ZERO);

        operation = Operation.NONE;
        topOperandHasDecimalPoint = false;
    }

    public void negate() {
        operands.push(operands.pop().negate());
    }

    public void addDecimalPoint() {
        topOperandHasDecimalPoint = true;
    }

    public void addDigit(int digit) {
        if (operation != Operation.NONE && operands.size() == 1) {
            operands.push(BigDecimal.ZERO);
            topOperandHasDecimalPoint = false;
        }

        BigDecimal operand = operands.pop();
        if (operand.scale() == 0 && !topOperandHasDecimalPoint) {
            operand = operand.multiply(BigDecimal.TEN);
            operand = operand.compareTo(BigDecimal.ZERO) < 0 ?
                    operand.subtract(new BigDecimal(digit)) :
                    operand.add(new BigDecimal(digit));
        } else {
            int scale = operand.scale() + 1;
            BigDecimal fraction = new BigDecimal(digit).movePointLeft(scale);
            operand = operand.setScale(scale, BigDecimal.ROUND_UNNECESSARY);
            operand = operand.compareTo(BigDecimal.ZERO) < 0 ?
                    operand.subtract(fraction) :
                    operand.add(fraction);
        }
        operands.push(operand);
    }

    public void performBinaryOperation(Operation binaryOperation) throws ArithmeticException {
        if (operands.size() > 1) {
            BigDecimal secondOperand = operands.pop();
            BigDecimal firstOperand  = operands.pop();
            switch (binaryOperation) {
                case ADD:
                    operands.push(firstOperand.add(secondOperand));
                    break;
                case SUBTRACT:
                    operands.push(firstOperand.subtract(secondOperand));
                    break;
                case MULTIPLY:
                    operands.push(firstOperand.multiply(secondOperand));
                    break;
                case DIVIDE:
                case MODULO:
                    if (secondOperand.equals(BigDecimal.ZERO)) {
                        operands.push(BigDecimal.ZERO);
                        throw new ArithmeticException();
                    } else {
                        final int ROUNDING_PRECISION = 20;
                        BigDecimal result =
                            binaryOperation == Operation.DIVIDE ?
                                firstOperand.divide(
                                    secondOperand,
                                    ROUNDING_PRECISION,
                                    BigDecimal.ROUND_HALF_EVEN
                                ) :
                                firstOperand.divideAndRemainder(
                                    secondOperand
                                )[1];

                        operands.push(result);
                    }
                    break;
            }

            BigDecimal operand = operands.pop();
            operand = operand.stripTrailingZeros();
            operands.push(operand);

            topOperandHasDecimalPoint = operands.peek().scale() > 0;
        }

        operation = binaryOperation;
    }

    public void calculate() throws ArithmeticException {
        if (operation != Operation.NONE) {
            performBinaryOperation(operation);
        }
    }
}
