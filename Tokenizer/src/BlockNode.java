import java.util.List;

public class BlockNode extends AST{
    List<StatementNode> statements;

    BlockNode(List<StatementNode> statements) {
        this.statements = statements;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitBlockNode(this);
    }
}
