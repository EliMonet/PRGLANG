public class AssignmentStatementNode extends StatementNode{
    ExpressionNode left;   // Identifier or FieldAccess
    String operator;      // =, +=, -=
    ExpressionNode right;

    AssignmentStatementNode(ExpressionNode left, String operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitAssignmentStatementNode(this);
    }
}
