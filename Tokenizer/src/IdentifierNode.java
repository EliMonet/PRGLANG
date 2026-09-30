public class IdentifierNode extends ExpressionNode{
    String name;

    IdentifierNode(String name) {
        this.name = name;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitIdentifierNode(this);
    }
}
