public class ParameterNode extends AST{
    String type;
    String name;

    ParameterNode(String type, String name) {
        this.type = type;
        this.name = name;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitParameterNode(this);
    }
}
