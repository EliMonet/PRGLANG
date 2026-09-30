public class BreakStatementNode extends StatementNode{
    BreakStatementNode(){}
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitBreakNode(this);
    }
}
