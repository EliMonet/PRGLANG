public class ContinueStatementNode extends StatementNode{
    ContinueStatementNode(){}
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitContinueStatementNode(this);
    }
}
