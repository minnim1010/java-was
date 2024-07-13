package codesquad.template.compile.node;

import codesquad.template.TemplateContext;

public class VariableNode extends ASTNode {

    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public String evaluate(TemplateContext context) {
        return context.getValue(name).toString();
    }

    public String getName() {
        return name;
    }
}