import java.util.*;

public class ScopeVisitor implements Visitor<String> {
    private Stack<Map<String, Symbol>> scopes = new Stack<>();

    private void enterScope(){
        scopes.push(new HashMap<>());
    }
    private void exitScope(){
        scopes.pop();
    }
    public ScopeVisitor(){
        enterScope();
    }

    private void define(String name, String type) {
        Map<String, Symbol> current = scopes.peek();

        if (current.containsKey(name)) {
            System.err.println("Error: Redeclaration of " + name);
            return;
        }

        current.put(name, new Symbol(name, type, "variable"));
    }

    private Symbol resolve(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }

        System.err.println("Error: Undefined variable " + name);
        return null;
    }

    @Override
    public String visitBinaryExpr(BinaryExpressionNode node) {

        String leftType = node.left.accept(this);
        String rightType = node.right.accept(this);
        String op = node.operator;

        // Arithmetic operators
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) {
            if (!leftType.equals("int") || !rightType.equals("int")) {
                System.err.println("Error: arithmetic operators require int operands");
                return "error";
            }
            return "int";
        }

        // Comparison operators
        if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
            if (!leftType.equals("int") || !rightType.equals("int")) {
                System.err.println("Error: comparison operators require int operands");
                return "error";
            }
            return "boolean";
        }
        // Equality operators
        if (op.equals("==") || op.equals("!=")) {
            if (!leftType.equals(rightType)) {
                System.err.println("Error: cannot compare different types");
                return "error";
            }
            return "boolean";
        }

        // Logical operators
        if (op.equals("&&") || op.equals("||")) {
            if (!leftType.equals("boolean") || !rightType.equals("boolean")) {
                System.err.println("Error: logical operators require boolean operands");
                return "error";
            }
            return "boolean";
        }

        return "error";
    }

    @Override
    public String visitLiteralExpr(LiteralNode node) {
        String v = node.value;
        if (v.equals("true") || v.equals("false")) return "boolean";
        if (v.matches("\\d+")) return "int";
        return "String";
    }

    @Override
    public String visitVariable(VariableDeclerationNode node) {
        define(node.name, node.type);
        return null;
    }

    @Override
    public String visitAssignmentStatementNode(AssignmentStatementNode node) {
        String leftType = node.left.accept(this);
        String rightType = node.right.accept(this);

        if (!leftType.equals("error") && !rightType.equals("error") && !leftType.equals(rightType)) {
            System.err.println("Error: type mismatch in assignment");
        }

        return null;
    }

    @Override
    public String visitUnaryExpr(UnaryExpressionNode node) {
        String operandType = node.operand.accept(this);

        if (node.operator.equals("!")) {
            if (!operandType.equals("boolean")) {
                System.err.println("Error: ! requires a boolean operand");
                return "error";
            }
            return "boolean";
        }

        if (node.operator.equals("-")) {
            if (!operandType.equals("int")) {
                System.err.println("Error: unary - requires an int operand");
                return "error";
            }
            return "int";
        }

        return "error";
    }

    @Override
    public String visitWhileStatementNode(WhileStatementNode node) {
        String condType = node.condition.accept(this);

        if (!condType.equals("error") && !condType.equals("boolean")) {
            System.err.println("Error: while condition must be boolean");
        }

        node.body.accept(this);
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
        resolve(node.identifier);
        return null;
    }

    @Override
    public String visitReturnStatementNode(ReturnStatementNode node) {
        if (node.value != null){
            node.value.accept(this);
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
        define(node.name, "Pokemon");
        return null;
    }

    @Override
    public String visitParameterNode(ParameterNode node) {
        return null;
    }

    @Override
    public String visitMoveStatementNode(MoveStatementNode node) {
        Symbol attackerSym = resolve(node.attacker);
        Symbol targetSym = resolve(node.target);

        if (attackerSym != null && !attackerSym.type.equals("Pokemon")) {
            System.err.println("Error: attacker must be a Pokemon");
        }

        if (targetSym != null && !targetSym.type.equals("Pokemon")) {
            System.err.println("Error: target must be a Pokemon");
        }

        return null;
    }

    @Override
    public String visitIfStatementNode(IfStatementNode node) {
        String condType = node.condition.accept(this);

        if (!condType.equals("error") && !condType.equals("boolean")) {
            System.err.println("Error: if condition must be boolean");
        }

        node.thenBlock.accept(this);

        if (node.elseifConditions != null && node.elseifBlocks != null) {
            for (int i = 0; i < node.elseifConditions.size(); i++) {
                String elseIfType = node.elseifConditions.get(i).accept(this);

                if (!elseIfType.equals("error") && !elseIfType.equals("boolean")) {
                    System.err.println("Error: elseif condition must be boolean");
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
        if (sym == null) {
            return "error";
        }
        return sym.type;
    }

    @Override
    public String visitFunctionDeclerationNode(FunctionDeclarationNode node) {
        define(node.name, "function");

        enterScope();

        for (ParameterNode param : node.parameters) {
            define(param.name, param.type);
        }

        node.body.accept(this);

        exitScope();
        return null;
    }

    @Override
    public String visitFunctionCallExpressionNode(FunctionCallExpressionNode node) {
        Symbol func = resolve(node.name);

        if (func == null) {
            return "error";
        }

        for (ExpressionNode arg : node.arguments) {
            arg.accept(this);
        }

        return func.type;
    }

    @Override
    public String visitForStatementNode(ForStatementNode node) {
        enterScope();

        if (node.init != null) {
            node.init.accept(this);
        }

        if (node.condition != null) {
            String condType = node.condition.accept(this);
            if (!condType.equals("error") && !condType.equals("boolean")) {
                System.err.println("Error: for condition must be boolean");
            }
        }

        if (node.update != null) {
            node.update.accept(this);
        }

        node.body.accept(this);

        exitScope();
        return null;
    }

    @Override
    public String visitFieldAccessNode(FieldAccessNode node) {
        String objectType = node.object.accept(this);

        if (objectType.equals("error")) {
            return "error";
        }

        return "unknown";
    }

    @Override
    public String visitContinueStatementNode(ContinueStatementNode node) {
        return null;
    }

    @Override
    public String visitBreakNode(BreakStatementNode node) {
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
