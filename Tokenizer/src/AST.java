public abstract class AST {
    static int parsedStatements=0;
    public abstract <R> R accept(Visitor<R> visitor);
}

