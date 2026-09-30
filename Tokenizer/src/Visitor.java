public interface Visitor<R> {
    R visitBinaryExpr(BinaryExpressionNode node);
    R visitLiteralExpr(LiteralNode node);
    R visitVariable(VariableDeclerationNode node);
    R visitAssignmentStatementNode(AssignmentStatementNode node);
    R visitUnaryExpr(UnaryExpressionNode node);
    R visitWhileStatementNode(WhileStatementNode node);
    R visitStatementNode(StatementNode node);
    R visitSpellDatabaseNode(SpellDatabaseStatement node);
    R visitShowStatementNode(ShowStatementNode node);
    R visitReturnStatementNode(ReturnStatementNode node);
    R visitProgramNode(ProgramNode node);
    R visitPrintStatementNode(PrintStatementNode node);
    R visitPokemonLoadStatementNode(PokemonLoadStatement node);
    R visitParameterNode(ParameterNode node);
    R visitMoveStatementNode(MoveStatementNode node);
    R visitIfStatementNode(IfStatementNode node);
    R visitIdentifierNode(IdentifierNode node);
    R visitFunctionDeclerationNode(FunctionDeclarationNode node);
    R visitFunctionCallExpressionNode(FunctionCallExpressionNode node);
    R visitForStatementNode(ForStatementNode node);
    R visitFieldAccessNode(FieldAccessNode node);
    R visitContinueStatementNode(ContinueStatementNode node);
    R visitBreakNode(BreakStatementNode node);
    R visitBlockNode(BlockNode node);
}
