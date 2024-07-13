package codesquad.template.compile.node;

import codesquad.template.TemplateContext;

public class LiteralNode extends ASTNode {

    private final String value;

    public LiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String evaluate(TemplateContext context) {
        return value;
    }

    public String getValue() {
        return value;
    }
}