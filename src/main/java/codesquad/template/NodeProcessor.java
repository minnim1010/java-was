package codesquad.template;

import codesquad.template.compile.node.ASTNode;
import codesquad.template.compile.node.EvaluatorContext;
import java.util.ArrayList;
import java.util.List;

public class NodeProcessor {

    private final SimpleParser simpleParser = new SimpleParser();

    public void processConditions(Node node, EvaluatorContext context) {
        if (node == null) {
            return;
        }

        List<Node> nodesToRemove = new ArrayList<>();
        for (Node child : node.children) {
            String ifCondition = child.getAttribute("my-if");
            if (ifCondition != null && !evaluateCondition(ifCondition, context)) {
                nodesToRemove.add(child);
                child.removeAttribute("my-if");
            }
            processConditions(child, context);
        }
        node.children.removeAll(nodesToRemove);
    }

    private boolean evaluateCondition(String condition, EvaluatorContext context) {
        ASTNode ast = simpleParser.parse(condition);
        String evaluate = ast.evaluate(context);

        return Boolean.parseBoolean(evaluate);
    }
}
