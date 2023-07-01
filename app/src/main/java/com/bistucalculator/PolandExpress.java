package com.bistucalculator;

import java.util.*;
import java.util.regex.*;

// 逆波兰表达式完整版(可以匹配小数点)
public class PolandExpress {

    // 根据逆波兰表达式直接给计算器进行相应的计算
    public static Double calculate(String expression) {
        // 正规化部分表达式，将-6 或 6+(-3)这种形式转化为0-6 或 6+(0-3)
        expression = expression.replaceAll("(?<![\\d\\)])-(\\d+\\.?\\d*)", "0-$1");

        List<String> list = postfixExpressionChange(expressionSplit(expression));
        Stack<String> stack = new Stack<>();

        for (String item : list) {

            if (item.matches("((\\d+)(\\.\\d+)?)")) {
                stack.push(item);
            } else {

                String num2Str = stack.pop();
                String num1Str = stack.pop();

                double num2 = Double.parseDouble(num2Str),
                        num1 = Double.parseDouble(num1Str),
                        ans = 0;

                if (item.equals("+")) {
                    ans = num1 + num2;
                } else if (item.equals("-")) {
                    ans = num1 - num2;
                } else if (item.equals("*")) {
                    ans = num1 * num2;
                } else if (item.equals("/")) {
                    ans = num1 / num2;
                }

                stack.push(String.valueOf(ans));
            }
        }

        return Double.parseDouble(stack.pop());
    }

    // 将字符串的表达式拆分开来，保存到List中，方便后续的计算
    public static List<String> expressionSplit(String str) {
        List<String> expressionList = new ArrayList<String>();
        String temp = "";

        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);

            if (!numberJudge(item)) {
                expressionList.add(String.valueOf(item));
            } else if (item >= 48 && item <= 57 || item == 46) {
                temp += item;
                if (i + 1 == str.length() || !numberJudge(str.charAt(i + 1))) {
                    expressionList.add(String.valueOf(temp));
                    temp = "";
                }
            } else {
                throw new RuntimeException("表达式格式不正确");
            }
        }

        return expressionList;
    }

    // 根据规则，将中缀表达式转为后缀表达式。一个stack，一个List存储结果
    public static List<String> postfixExpressionChange(List<String> expressionSplit) {
        Stack<String> stack = new Stack<String>(); //存储符号
        List<String> postfixExpression = new ArrayList<String>(); //存储后缀表达式

        for (String item : expressionSplit) {
            if (numberJudge(item.toCharArray()[0])) {
                postfixExpression.add(item);
            } else {
                // 初始状态，符号栈里面什么都没有
                if (stack.size() == 0) {
                    stack.push(item);
                } else {
                    Operation oprCls = new Operation();
                    String lastOpr = stack.peek();
                    if (item.equals("(") || oprCls.oprPriority(item) > oprCls.oprPriority(lastOpr)) {
                        stack.push(item);
                    } else if (item.equals(")")) {
                        while (stack.size() > 0 && !stack.peek().equals("(")) {
                            postfixExpression.add(stack.pop());
                        }
                        stack.pop();
                    } else {
                        postfixExpression.add(stack.pop());
                        stack.push(item);
                    }
                }
            }
        }

        while (stack.size() != 0) {
            postfixExpression.add(stack.pop());
        }

        return postfixExpression;
    }

    /*
     * 4.定义一个方法，判断该字符是数字还是符号
     */
    public static Boolean numberJudge(char ch) {
        String oprRegEx = "[\\+\\-\\*\\/()]";
        return !Pattern.matches(oprRegEx, String.valueOf(ch));
    }
}

class Operation {
    private final int BRACKET = 0;
    private final int ADD = 1;
    private final int DELETE = 1;
    private final int MULTIPLY = 2;
    private final int DIVIDE = 2;

    public Integer oprPriority(String opr) {
        int result = 0;

        switch (opr) {
            case "+":
                result = ADD;
                break;
            case "-":
                result = DELETE;
                break;
            case "*":
                result = MULTIPLY;
                break;
            case "/":
                result = DIVIDE;
                break;
            default:
                result = BRACKET;
                break;
        }
        return result;
    }
}
