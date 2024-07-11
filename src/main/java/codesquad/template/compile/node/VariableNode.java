package codesquad.template.compile.node;

public class VariableNode extends ASTNode {

    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public String evaluate(EvaluatorContext context) {
        return context.getValue(name).toString();
    }

    public String getName() {
        return name;
    }
}