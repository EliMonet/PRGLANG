public class WhileStatementNode extends StatementNode{
    ExpressionNode condition;
    BlockNode body;

    WhileStatementNode(ExpressionNode condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitWhileStatementNode(this);
    }
}
