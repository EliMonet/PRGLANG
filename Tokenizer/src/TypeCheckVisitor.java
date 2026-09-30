import java.util.*;

public class TypeCheckVisitor implements Visitor<String> {

    private Stack<Map<String, Symbol>> scopes = new Stack<>();
    private String currentFunctionReturnType = null;
    private int loopDepth = 0;
    private boolean hasError = false;

    public TypeCheckVisitor() {
        enterScope(); // global
    }

    public boolean hasError() {
        return hasError;
    }

    private void error(String message) {
        hasError = true;
        System.err.println("Error: " + message);
    }

    private void enterScope() {
        scopes.push(new HashMap<>());
    }

    private void exitScope() {
        scopes.pop();
    }

    private void defineVariable(String name, String type) {
        Map<String, Symbol> current = scopes.peek();
        current.put(name, new Symbol(name, type, "variable"));
    }

    private void defineFunction(String name, List<String> paramTypes, String returnType) {
        Map<String, Symbol> current = scopes.peek();
        current.put(name, new Symbol(name, "function", paramTypes, returnType));
    }

    private Symbol resolve(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        error("undefined identifier " + name);
        return null;
    }

    private boolean isNumeric(String type) {
        return "int".equals(type);
    }

    private boolean isBoolean(String type) {
        return "boolean".equals(type);
    }

    private boolean isPokemon(String type) {
        return "Pokemon".equals(type);
    }

    @Override
    public String visitBinaryExpr(BinaryExpressionNode node) {
        String leftType = node.left.accept(this);
        String rightType = node.right.accept(this);
        String op = node.operator;

        if ("error".equals(leftType) || "error".equals(rightType)) {
            return "error";
        }

        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) {
            if (!isNumeric(leftType) || !isNumeric(rightType)) {
                error("arithmetic operators require int operands");
                return "error";
            }
            return "int";
        }

        if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
            if (!isNumeric(leftType) || !isNumeric(rightType)) {
                error("comparison operators require int operands");
                return "error";
            }
            return "boolean";
        }

        if (op.equals("==") || op.equals("!=")) {
            if (!leftType.equals(rightType)) {
                error("cannot compare " + leftType + " with " + rightType);
                return "error";
            }
            return "boolean";
        }

        if (op.equals("&&") || op.equals("||")) {
            if (!isBoolean(leftType) || !isBoolean(rightType)) {
                error("logical operators require boolean operands");
                return "error";
            }
            return "boolean";
        }

        return "error";
    }

    @Override
    public String visitLiteralExpr(LiteralNode node) {
        String v = node.value;

        if (v.equals("true") || v.equals("false")) {
            return "boolean";
        }

        if (v.matches("\\d+")) {
            return "int";
        }

        return "String";
    }

    @Override
    public String visitVariable(VariableDeclerationNode node) {
        defineVariable(node.name, node.type);

        if (node.value != null) {
            String declaredType = node.type.trim();
            String valueType = node.value.accept(this).trim();

            if ("Pokemon".equals(declaredType) && "String".equals(valueType)) {
                System.out.println("Debug: allowing pokemon assignment from string");
                return null;
            }

            if (!"error".equals(valueType) && !declaredType.equals(valueType)) {
                error("Var cannot assign " + valueType + " to variable of type " + declaredType);
            }
        }

        return null;
    }

    @Override
    public String visitAssignmentStatementNode(AssignmentStatementNode node) {
        String leftType = node.left.accept(this);
        String rightType = node.right.accept(this);

        if (!"error".equals(leftType) && !"error".equals(rightType) && !leftType.equals(rightType)) {
            error("type mismatch in assignment");
        }

        return null;
    }

    @Override
    public String visitUnaryExpr(UnaryExpressionNode node) {
        String operandType = node.operand.accept(this);

        if (node.operator.equals("!")) {
            if (!isBoolean(operandType)) {
                error("! requires a boolean operand");
                return "error";
            }
            return "boolean";
        }

        if (node.operator.equals("-")) {
            if (!isNumeric(operandType)) {
                error("unary - requires an int operand");
                return "error";
            }
            return "int";
        }

        return "error";
    }

    @Override
    public String visitWhileStatementNode(WhileStatementNode node) {
        String condType = node.condition.accept(this);
        if (!"error".equals(condType) && !isBoolean(condType)) {
            error("while condition must be boolean");
        }

        loopDepth++;
        node.body.accept(this);
        loopDepth--;

        return null;
    }

    @Override
    public String visitStatementNode(StatementNode node) {
        return null;
    }

    @Override
    public String visitSpellDatabaseNode(SpellDatabaseStatement node) {
        return null;
    }

    @Override
    public String visitShowStatementNode(ShowStatementNode node) {
        Symbol sym = resolve(node.identifier);
        if (sym == null) return null;
        return null;
    }

    @Override
    public String visitReturnStatementNode(ReturnStatementNode node) {
        if (currentFunctionReturnType == null) {
            error("return used outside of a function");
            return null;
        }

        String actualType = "void";
        if (node.value != null) {
            actualType = node.value.accept(this);
        }

        if (!"error".equals(actualType) && !currentFunctionReturnType.equals(actualType)) {
            error("function should return " + currentFunctionReturnType + " but returned " + actualType);
        }

        return null;
    }

    @Override
    public String visitProgramNode(ProgramNode node) {
        for (StatementNode stmt : node.statements) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public String visitPrintStatementNode(PrintStatementNode node) {
        node.expression.accept(this);
        return null;
    }

    @Override
    public String visitPokemonLoadStatementNode(PokemonLoadStatement node) {
        defineVariable(node.name, "Pokemon");
        return null;
    }

    @Override
    public String visitParameterNode(ParameterNode node) {
        return null;
    }

    @Override
    public String visitMoveStatementNode(MoveStatementNode node) {
        Symbol attacker = resolve(node.attacker);
        Symbol target = resolve(node.target);

        if (attacker != null && !isPokemon(attacker.type)) {
            error("attacker must be a Pokemon");
        }

        if (target != null && !isPokemon(target.type)) {
            error("target must be a Pokemon");
        }
        if (!node.move.equals("move1") &&
                !node.move.equals("move2") &&
                !node.move.equals("move3") &&
                !node.move.equals("move4")) {

            error("invalid move: " + node.move + " (must be move1–move4)");
        }

        return null;
    }

    @Override
    public String visitIfStatementNode(IfStatementNode node) {
        String condType = node.condition.accept(this);
        if (!"error".equals(condType) && !isBoolean(condType)) {
            error("if condition must be boolean");
        }

        node.thenBlock.accept(this);

        if (node.elseifConditions != null && node.elseifBlocks != null) {
            for (int i = 0; i < node.elseifConditions.size(); i++) {
                String t = node.elseifConditions.get(i).accept(this);
                if (!"error".equals(t) && !isBoolean(t)) {
                    error("elseif condition must be boolean");
                }
                node.elseifBlocks.get(i).accept(this);
            }
        }

        if (node.elseBlock != null) {
            node.elseBlock.accept(this);
        }

        return null;
    }

    @Override
    public String visitIdentifierNode(IdentifierNode node) {
        Symbol sym = resolve(node.name);
        if (sym == null) return "error";
        return sym.type;
    }

    @Override
    public String visitFunctionDeclerationNode(FunctionDeclarationNode node) {
        List<String> paramTypes = new ArrayList<>();
        for (ParameterNode param : node.parameters) {
            paramTypes.add(param.type);
        }

        defineFunction(node.name, paramTypes, node.returnType);

        enterScope();

        String oldReturnType = currentFunctionReturnType;
        currentFunctionReturnType = node.returnType;

        for (ParameterNode param : node.parameters) {
            defineVariable(param.name, param.type);
        }

        node.body.accept(this);

        currentFunctionReturnType = oldReturnType;
        exitScope();
        return null;
    }

    @Override
    public String visitFunctionCallExpressionNode(FunctionCallExpressionNode node) {
        Symbol sym = resolve(node.name);
        if (sym == null) return "error";

        if (!"function".equals(sym.kind)) {
            error(node.name + " is not a function");
            return "error";
        }

        if (sym.paramTypes == null) {
            sym.paramTypes = new ArrayList<>();
        }

        if (node.arguments.size() != sym.paramTypes.size()) {
            error("wrong number of arguments for function " + node.name);
        } else {
            for (int i = 0; i < node.arguments.size(); i++) {
                String actual = node.arguments.get(i).accept(this);
                String expected = sym.paramTypes.get(i);

                if (!"error".equals(actual) && !expected.equals(actual)) {
                    error("argument " + (i + 1) + " of " + node.name + " should be " + expected + " but got " + actual);
                }
            }
        }

        return sym.returnType != null ? sym.returnType : "error";
    }

    @Override
    public String visitForStatementNode(ForStatementNode node) {
        enterScope();

        if (node.init != null) node.init.accept(this);

        if (node.condition != null) {
            String condType = node.condition.accept(this);
            if (!"error".equals(condType) && !isBoolean(condType)) {
                error("for condition must be boolean");
            }
        }

        loopDepth++;

        if (node.update != null) node.update.accept(this);
        node.body.accept(this);

        loopDepth--;
        exitScope();
        return null;
    }

    @Override
    public String visitFieldAccessNode(FieldAccessNode node) {
        String objectType = node.object.accept(this);

        if (!isPokemon(objectType)) {
            error("field access ." + node.field + " is only allowed on Pokemon");
            return "error";
        }

        if (node.field.equals("hp") || node.field.equals("attack") || node.field.equals("defend")) {
            return "int";
        }

        if (node.field.equals("move1") || node.field.equals("move2")
                || node.field.equals("move3") || node.field.equals("move4")) {
            return "String";
        }

        error("invalid Pokemon field: " + node.field);
        return "error";
    }

    @Override
    public String visitContinueStatementNode(ContinueStatementNode node) {
        if (loopDepth <= 0){
            error("continue can only be used inside a loop");
        }
        return null;
    }

    @Override
    public String visitBreakNode(BreakStatementNode node) {
        if (loopDepth <=0){
            error("break can only be used inside a loop");
        }
        return null;
    }

    @Override
    public String visitBlockNode(BlockNode node) {
        enterScope();
        for (StatementNode stmt : node.statements) {
            stmt.accept(this);
        }
        exitScope();
        return null;
    }
}