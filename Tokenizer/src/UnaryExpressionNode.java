public class UnaryExpressionNode extends ExpressionNode{
    String operator;
    ExpressionNode operand;

    UnaryExpressionNode(String operator, ExpressionNode operand) {
        this.operator = operator;
        this.operand = operand;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitUnaryExpr(this);
    }
}
