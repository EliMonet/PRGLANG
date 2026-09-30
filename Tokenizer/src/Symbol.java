import java.util.List;

public class Symbol {
    String name;
    String type; // int, boolean, String, Pokemon, void
    String kind; // variable, function, parameter
    List<String> paramTypes;
    String returnType;

    public Symbol(String name, String type, String kind) {
        this.name = name;
        this.type = type;
        this.kind = kind;
    }

    public Symbol(String name, String kind, List<String> paramTypes, String returnType) {
        this.name = name;
        this.kind = kind;
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }
}