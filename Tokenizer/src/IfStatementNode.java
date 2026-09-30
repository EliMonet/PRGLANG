import java.util.List;

public class IfStatementNode extends StatementNode{
    ExpressionNode condition;
    BlockNode thenBlock;

    List<ExpressionNode> elseifConditions;
    List<BlockNode> elseifBlocks;

    BlockNode elseBlock;

    IfStatementNode(
            ExpressionNode condition,
            BlockNode thenBlock,
            List<ExpressionNode> elseifConditions,
            List<BlockNode> elseifBlocks,
            BlockNode elseBlock
    ) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseifConditions = elseifConditions;
        this.elseifBlocks = elseifBlocks;
        this.elseBlock = elseBlock;
    }
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitIfStatementNode(this);
    }
}
