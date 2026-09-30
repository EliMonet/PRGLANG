public class ReturnStatementNode extends StatementNode{
    ExpressionNode value;

    ReturnStatementNode(ExpressionNode value) {
        this.value = value;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitReturnStatementNode(this);
    }
}
