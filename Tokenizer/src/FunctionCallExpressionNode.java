import java.util.List;

public class FunctionCallExpressionNode extends StatementNode{
    String name;
    List<ExpressionNode> arguments;

    FunctionCallExpressionNode(String name, List<ExpressionNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitFunctionCallExpressionNode(this);
    }
}
