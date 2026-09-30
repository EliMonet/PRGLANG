public class PrintStatementNode extends StatementNode{
    ExpressionNode expression;

    PrintStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitPrintStatementNode(this);
    }
}
