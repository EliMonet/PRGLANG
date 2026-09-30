public class FieldAccessNode extends ExpressionNode{
    ExpressionNode object;
    String field;

    FieldAccessNode(ExpressionNode object, String field) {
        this.object = object;
        this.field = field;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitFieldAccessNode(this);
    }
}
