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

    public void processForEach(Node root, EvaluatorContext context) {
        List<Node> nodesWithForEach = root.findNodesWithAttribute("my-forEach");
        for (Node node : nodesWithForEach) {
            String forEachValue = node.getAttribute("my-forEach");
            String[] parts = forEachValue.split(" in ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid my-forEach attribute format: " + forEachValue);
            }

            String itemName = parts[0].trim();
            String listName = parts[1].trim();
            Object listObject = context.getValue(listName);
            if (!(listObject instanceof List)) {
                throw new IllegalArgumentException("Cannot render: " + listName + " is not a list");
            }

            List<?> list = (List<?>) listObject;
            StringBuilder repeatedContent = new StringBuilder();
            for (Object item : list) {
                EvaluatorContext newContext = new EvaluatorContext();
                newContext.setValue(itemName, item);
                String content = node.getInnerHtml();
                repeatedContent.append(TemplateEngine.getInstance().processTemplate(content, newContext));
            }
            node.setInnerHtml(repeatedContent.toString());
            node.removeAttribute("my-forEach");
        }
    }
}
