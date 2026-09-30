public class BinaryExpressionNode extends ExpressionNode{
    ExpressionNode left;
    String operator;
    ExpressionNode right;

    BinaryExpressionNode(ExpressionNode left, String operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitBinaryExpr(this);
    }
}
