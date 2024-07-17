package codesquad.template.compile.node;

import codesquad.template.TemplateContext;

public abstract class ASTNode {

    public abstract String evaluate(TemplateContext context);
}
