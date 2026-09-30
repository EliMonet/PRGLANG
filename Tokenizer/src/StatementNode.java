public class StatementNode extends AST {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitStatementNode(this);
    }
}
