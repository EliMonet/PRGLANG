public class ShowStatementNode extends StatementNode{
    String identifier;

    ShowStatementNode(String identifier) {
        this.identifier = identifier;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitShowStatementNode(this);
    }
}
