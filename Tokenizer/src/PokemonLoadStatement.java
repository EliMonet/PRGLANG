public class PokemonLoadStatement extends StatementNode{
    String name;
    String source;

    PokemonLoadStatement(String name, String source) {
        this.name = name;
        this.source = source;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitPokemonLoadStatementNode(this);
    }
}
