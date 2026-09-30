public class ForStatementNode extends StatementNode{
    StatementNode init;
    ExpressionNode condition;
    ExpressionNode update;
    BlockNode body;

    ForStatementNode(
            StatementNode init,
            ExpressionNode condition,
            ExpressionNode update,
            BlockNode body
    ) {
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitForStatementNode(this);
    }
}
