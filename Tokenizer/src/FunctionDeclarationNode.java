import java.util.List;

public class FunctionDeclarationNode extends StatementNode{
    String returnType;
    String name;
    List<ParameterNode> parameters;
    BlockNode body;

    FunctionDeclarationNode(String returnType, String name,
                            List<ParameterNode> parameters,
                            BlockNode body) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitFunctionDeclerationNode(this);
    }
}
