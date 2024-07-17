package codesquad.template.compile.node;

import codesquad.template.TemplateContext;

public class BinaryOperationNode extends ASTNode {

    private final ASTNode left;
    private final ASTNode right;
    private final String operator;

    public BinaryOperationNode(ASTNode left, ASTNode right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String evaluate(TemplateContext context) {
        String leftValue = left.evaluate(context);
        String rightValue = right.evaluate(context);

        return switch (operator) {
            case "==" -> String.valueOf(leftValue.equals(rightValue));
            case "!=" -> String.valueOf(!leftValue.equals(rightValue));
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }
}