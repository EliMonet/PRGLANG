public class MoveStatementNode extends StatementNode{
    String attacker;
    String move;
    String target;

    MoveStatementNode(String attacker, String move, String target) {
        this.attacker = attacker;
        this.move = move;
        this.target = target;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitMoveStatementNode(this);
    }
}
