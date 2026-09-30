
public class SpellDatabaseStatement extends StatementNode{
    String path;

    SpellDatabaseStatement(String path) {
        this.path = path;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitSpellDatabaseNode(this);
    }
}
