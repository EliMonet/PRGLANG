public class ASTPrinterVisitor implements Visitor<String>{

    @Override
    public String visitBinaryExpr(BinaryExpressionNode node) {
        return "("
                + node.left.accept(this)
                + " " + node.operator + " "
                + node.right.accept(this)
                + ")";
    }

    @Override
    public String visitLiteralExpr(LiteralNode node) {
        return node.value;
    }

    @Override
    public String visitVariable(VariableDeclerationNode node) {
        return node.name;
    }

    @Override
    public String visitAssignmentStatementNode(AssignmentStatementNode node) {
        return "("
                + node.left.accept(this) + " "
                + node.operator + " "
                + node.right.accept(this)
                + ")";
    }

    @Override
    public String visitUnaryExpr(UnaryExpressionNode node) {
        return "(" + node.operator + " " + node.operand.accept(this) + ")";
    }

    @Override
    public String visitWhileStatementNode(WhileStatementNode node) {
        return "(while "
                + node.condition.accept(this) + " "
                + node.body.accept(this)
                + ")";
    }

    @Override
    public String visitStatementNode(StatementNode node) {
        return null;
    }

    @Override
    public String visitSpellDatabaseNode(SpellDatabaseStatement node) {
        return "(spelldatabase " + node.path + ")";
    }

    @Override
    public String visitShowStatementNode(ShowStatementNode node) {
        return "(show " + node.identifier+ ")";
    }

    @Override
    public String visitReturnStatementNode(ReturnStatementNode node) {
        return "return " + node.value.accept(this) + ")";
    }

    @Override
    public String visitProgramNode(ProgramNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Program\n");
        for (AST stmt : node.statements){
            sb.append(stmt.accept(this)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visitPrintStatementNode(PrintStatementNode node) {
        return "Print " + node.expression.accept(this);
    }

    @Override
    public String visitPokemonLoadStatementNode(PokemonLoadStatement node) {
        return "Pokemon " + node.name + " " + node.source + ")";
    }

    @Override
    public String visitParameterNode(ParameterNode node) {
        return "Param: " + node.name;
    }

    @Override
    public String visitMoveStatementNode(MoveStatementNode node) {
        return "(" + node.attacker + " " + node.move + " " + node.target + ")";
    }

    @Override
    public String visitIfStatementNode(IfStatementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("If\n");

        sb.append("Condition: ")
                .append(node.condition.accept(this)).append("\n");

        sb.append("Then:\n");
        sb.append(node.thenBlock.accept(this)).append("\n");

        if (node.elseBlock != null) {
            sb.append("Else:\n");
            sb.append(node.elseBlock.accept(this)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visitIdentifierNode(IdentifierNode node) {
        return node.name;
    }

    @Override
    public String visitFunctionDeclerationNode(FunctionDeclarationNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Function ").append(node.name).append("\n");

        sb.append("Params:\n");
        for (ParameterNode param : node.parameters) {
            sb.append(param.accept(this)).append("\n");
        }

        sb.append("Body:\n");
        sb.append(node.body.accept(this)).append("\n");

        return sb.toString();
    }

    @Override
    public String visitFunctionCallExpressionNode(FunctionCallExpressionNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Call ").append(node.name).append("(");

        for (int i = 0; i < node.arguments.size(); i++) {
            sb.append(node.arguments.get(i).accept(this));
            if (i < node.arguments.size() - 1) sb.append(", ");
        }

        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitForStatementNode(ForStatementNode node) {
            StringBuilder sb = new StringBuilder();
            sb.append("For Loop\n");

            sb.append("Init: ").append(node.init.accept(this)).append("\n");
            sb.append("Condition: ").append(node.condition.accept(this)).append("\n");
            sb.append("Update: ").append(node.update.accept(this)).append("\n");

            sb.append("Body:\n");
            sb.append(node.body.accept(this)).append("\n");

            return sb.toString();
    }

    @Override
    public String visitFieldAccessNode(FieldAccessNode node) {
        return node.object.accept(this) + "." + node.field;
    }

    @Override
    public String visitContinueStatementNode(ContinueStatementNode node) {
        return "continue";
    }

    @Override
    public String visitBreakNode(BreakStatementNode node) {
        return "break";
    }

    @Override
    public String visitBlockNode(BlockNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Block\n");
        for (AST stmt : node.statements) {
            sb.append(stmt.accept(this)).append("\n");
        }
        return sb.toString();
    }
}
