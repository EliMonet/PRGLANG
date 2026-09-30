public class VariableDeclerationNode extends StatementNode{
    String type;
    String name;
    ExpressionNode value;

    VariableDeclerationNode(String type, String name, ExpressionNode value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitVariable(this);
    }
}
