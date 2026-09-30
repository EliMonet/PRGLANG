import java.util.List;

public class ProgramNode extends AST{
    List<StatementNode> statements;
    ProgramNode(List<StatementNode> statements){
        this.statements = statements;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitProgramNode(this);
    }
}
