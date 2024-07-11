package codesquad.template.compile.node;

public class LiteralNode extends ASTNode {

    private final String value;

    public LiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String evaluate(EvaluatorContext context) {
        return value;
    }

    public String getValue() {
        return value;
    }
}