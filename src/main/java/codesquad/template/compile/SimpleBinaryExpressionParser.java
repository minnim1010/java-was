package codesquad.template.compile;

import codesquad.template.compile.node.ASTNode;
import codesquad.template.compile.node.BinaryOperationNode;
import codesquad.template.compile.node.LiteralNode;
import codesquad.template.compile.node.VariableNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleBinaryExpressionParser {

    private SimpleBinaryExpressionParser() {
    }

    public static ASTNode parse(String expression) {
        String[] tokens = tokenize(expression);
        Stack<ASTNode> nodes = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (token.equals("==") || token.equals("!=")) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    nodes.push(createNode(operators.pop(), nodes.pop(), nodes.pop()));
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.peek().equals("(")) {
                    nodes.push(createNode(operators.pop(), nodes.pop(), nodes.pop()));
                }
                operators.pop();
            } else if (isLiteral(token)) {
                nodes.push(new LiteralNode(token));
            } else {
                nodes.push(new VariableNode(token));
            }
        }

        while (!operators.isEmpty()) {
            nodes.push(createNode(operators.pop(), nodes.pop(), nodes.pop()));
        }

        return nodes.pop();
    }

    private static String[] tokenize(String expression) {
        Matcher matcher = Pattern.compile("\\w+|==|!=|\\(|\\)").matcher(expression);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens.toArray(new String[0]);
    }

    private static boolean isLiteral(String token) {
        return token.equals("null") || token.equals("true") || token.equals("false");
    }

    private static int precedence(String operator) {
        return switch (operator) {
            case "==", "!=" -> 1;
            default -> 0;
        };
    }

    private static ASTNode createNode(String operator, ASTNode right, ASTNode left) {
        return new BinaryOperationNode(left, right, operator);
    }
}
