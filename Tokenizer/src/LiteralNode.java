public class LiteralNode extends ExpressionNode{
    String value;

    LiteralNode(String value) {
        this.value = value;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }
}
