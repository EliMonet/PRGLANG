import java.util.*;

public class Parser {
    ProgramNode parseProgram(token[] list) {

        List<StatementNode> statements = new ArrayList<>();

        while (token.currentIndex < list.length && list[token.currentIndex]!=null) {
            statements.add(parseStatement(list));
        }

        return new ProgramNode(statements);
    }
    StatementNode parseStatement(token[] list) {
        AST.parsedStatements++;
        //variable declaration
        if (list[token.currentIndex].content.equals("int") || list[token.currentIndex].content.equals("boolean") ||
                list[token.currentIndex].content.equals("String") || list[token.currentIndex].content.equals("Pokemon")) {
            return parseVariableDeclaration(list);
        }

        //if statement
        if (list[token.currentIndex].content.equals("if")){
            return parseIfStatement(list);
        }

        //while statement
        if (list[token.currentIndex].content.equals("while")){
            return parseWhileStatement(list);
        }

        //for statement
        if (list[token.currentIndex].content.equals("for")){
            return parseForStatement(list);
        }

        //print statement
        if (list[token.currentIndex].content.equals("print")){
            token.currentIndex++;
            ExpressionNode expr = parseExpression(list);
            if (!list[token.currentIndex].content.equals(";")){
                throw new RuntimeException("Expected ';'");
            }
            token.currentIndex++;
            return new PrintStatementNode(expr);
        }

        //identifier statement
        if (list[token.currentIndex].tokenTypeID.equals("identifier")){
            return parseIdentifierStatement(list);
        }

        //spelldatabase statement
        if (list[token.currentIndex].content.equals("spelldatabase")){
            return parseSpellDatabaseStatement(list);
        }

        //show statement
        if (list[token.currentIndex].content.equals("show")){
            return parseShowStatement(list);
        }

        //break statement
        if (list[token.currentIndex].content.equals("break")){
            return parseBreakStatement(list);
        }

        //continue
        if (list[token.currentIndex].content.equals("continue")){
            return parseContinueStatement(list);
        }

        //function
        if (list[token.currentIndex].content.equals("function")){
            return parseFunctionStatement(list);
        }

        //return
        if (list[token.currentIndex].content.equals("return")){
            return parseReturnStatement(list);
        }


        throw new RuntimeException("Unexpected token");
    }
    VariableDeclerationNode parseVariableDeclaration(token[] list) {

        // 1. type
        token typeToken = list[token.currentIndex];
        String type = typeToken.content;
        token.currentIndex++;

        // 2. identifier
        if (!list[token.currentIndex].tokenTypeID.equals("identifier")) {
            throw new RuntimeException("Expected identifier after type");
        }
        String name = list[token.currentIndex].content;
        token.currentIndex++;

        // 3. =
        if (!list[token.currentIndex].content.equals("=")) {
            throw new RuntimeException("Expected '=' in variable declaration");
        }
        token.currentIndex++;

        // 4. expression
        ExpressionNode value = parseExpression(list);

        // 5. ;
        if (!list[token.currentIndex].content.equals(";")) {
            throw new RuntimeException("Expected ';' after variable declaration");
        }
        token.currentIndex++;

        VariableDeclerationNode node = new VariableDeclerationNode(type, name, value);

        return node;
    }

    IfStatementNode parseIfStatement(token[] list){
        token.currentIndex++;
        if (!list[token.currentIndex].content.equals("(")){
            throw new RuntimeException("Expected '('");
        }
        token.currentIndex++;

        ExpressionNode condition = parseExpression(list);
        if (!list[token.currentIndex].content.equals(")")){
            throw new RuntimeException("Expected ')'");
        }
        token.currentIndex++;
        BlockNode thenBlock = parseBlock(list);
        List<ExpressionNode> elseifConditions = new ArrayList<>();
        List<BlockNode> elseifBlocks = new ArrayList<>();

        //else if list
        while (list[token.currentIndex]!=null && list[token.currentIndex].content.equals("elseif")){
            token.currentIndex++;

            if (!list[token.currentIndex].content.equals("(")){
                throw new RuntimeException("Expected '('");
            }
            token.currentIndex++;

            elseifConditions.add(parseExpression(list));

            if (!list[token.currentIndex].content.equals(")")){
                throw new RuntimeException("Expected ')'");
            }
            token.currentIndex++;
            elseifBlocks.add(parseBlock(list));
        }

        //else clause
        BlockNode elseBlock = null;
        if (list[token.currentIndex]!=null && list[token.currentIndex].content.equals("else")){
            token.currentIndex++;
            elseBlock = parseBlock(list);
        }
        return new IfStatementNode(condition,thenBlock,elseifConditions,elseifBlocks,elseBlock);
    }

    WhileStatementNode parseWhileStatement(token[] list){
        token.currentIndex++;
        if (!list[token.currentIndex].content.equals("(")){
            throw new RuntimeException("Expected '('");
        }
        token.currentIndex++;
        ExpressionNode condition = parseExpression(list);
        if (!list[token.currentIndex].content.equals(")")){
            throw new RuntimeException("Expected ')'");
        }
        token.currentIndex++;
        BlockNode body = parseBlock(list);
        return new WhileStatementNode(condition,body);
    }

    ForStatementNode parseForStatement(token[] list){
        token.currentIndex++;
        if (!list[token.currentIndex].content.equals("(")){
            throw new RuntimeException("Expected (");
        }
        token.currentIndex++;

        StatementNode init = parseVariableDeclaration(list);
        ExpressionNode condition = parseExpression(list);
        if (!list[token.currentIndex].content.equals(";")){
            throw new RuntimeException("Expected ;");
        }
        token.currentIndex++;

        //update: i++
        String name = list[token.currentIndex].content;
        token.currentIndex++;

        if (!list[token.currentIndex].content.equals("++")){
            throw new RuntimeException("Expected ++");
        }
        token.currentIndex++;

        ExpressionNode update = new UnaryExpressionNode("++",new IdentifierNode(name));

        if (!list[token.currentIndex].content.equals(")")){
            throw new RuntimeException("Expected ')'");
        }
        token.currentIndex++;

        BlockNode body = parseBlock(list);
        return new ForStatementNode(init,condition,update,body);
    }

    ExpressionNode parseExpression(token[] list){
        return parseLogicalOr(list);
    }

    StatementNode parseIdentifierStatement(token[] list) {
        String name = list[token.currentIndex].content;
        token.currentIndex++;

        //function call
        if (list[token.currentIndex].content.equals("(")) {
            token.currentIndex++;
            List<ExpressionNode> args = new ArrayList<>();
            if (!list[token.currentIndex].content.equals(")")) {
                args.add(parseExpression(list));
                token.currentIndex++;
                while (list[token.currentIndex].content.equals(",")) {
                    token.currentIndex++;
                    args.add(parseExpression(list));
                }
            }

            if (!list[token.currentIndex].content.equals(")")) {
                throw new RuntimeException("Expected ')'");
            }
            token.currentIndex++;
            if (!list[token.currentIndex].content.equals(";")) {
                throw new RuntimeException("Expected ';'");
            }
            token.currentIndex++;
            return new FunctionCallExpressionNode(name, args);
        }

        if (list[token.currentIndex].content.equals(".")) {
            ExpressionNode left = new IdentifierNode(list[token.currentIndex].content);
            token.currentIndex++;
            if (!list[token.currentIndex].tokenTypeID.equals("identifier")) {
                throw new RuntimeException("Expected field name after '.'");
            }
            String field = list[token.currentIndex].content;
            token.currentIndex++;
            left = new FieldAccessNode(left, field);
        }

        //assignment
        if (list[token.currentIndex].content.equals("=") ||
                list[token.currentIndex].content.equals("+=") ||
                list[token.currentIndex].content.equals("-=")) {
            String op = list[token.currentIndex].tokenTypeID;
            token.currentIndex++;

            ExpressionNode right = parseExpression(list);

            if (!list[token.currentIndex].content.equals(";")) {
                throw new RuntimeException("Expected ';'");
            }
            token.currentIndex++;
            return new AssignmentStatementNode(new IdentifierNode(name), op, right);
        }

        if (list[token.currentIndex].content.equals("move1") ||
                list[token.currentIndex].content.equals("move2") ||
                list[token.currentIndex].content.equals("move3") ||
                list[token.currentIndex].content.equals("move4")) {

            String move = list[token.currentIndex].content;
            token.currentIndex++;
            // target identifier
            if (!list[token.currentIndex].tokenTypeID.equals("identifier")) {
                throw new RuntimeException("Expected target after move");
            }

            String target = list[token.currentIndex].content;
            token.currentIndex++;

            // ;
            if (!list[token.currentIndex].content.equals(";")) {
                throw new RuntimeException("Expected ';'");
            }
            token.currentIndex++;

            MoveStatementNode node = new MoveStatementNode(name, move, target);
            return node;
        }
            throw new RuntimeException("Invalid identifier statement");
    }

    ExpressionNode parseAdditive(token[] list){
        ExpressionNode left = parseMultiplicative(list);
        while (token.currentIndex<list.length &&
                (list[token.currentIndex].content.equals("+") || list[token.currentIndex].content.equals("-"))){
            String op = list[token.currentIndex].content;
            token.currentIndex++;
            ExpressionNode right = parseMultiplicative(list);
            left = new BinaryExpressionNode(left,op,right);
        }
        return left;
    }

    ExpressionNode parseMultiplicative(token[] list){
        ExpressionNode left = primaryParse(list);

        while (token.currentIndex < list.length && (list[token.currentIndex].content.equals("*")
        || list[token.currentIndex].content.equals("/"))) {
            String op = list[token.currentIndex].tokenTypeID;
            token.currentIndex++;
            ExpressionNode right = primaryParse(list);
            left = new BinaryExpressionNode(left, op, right);
        }
        return left;
    }

    ExpressionNode primaryParse(token[] list){
        token t = list[token.currentIndex];

        //literal
        if (t.tokenTypeID.equals("digit") || t.tokenTypeID.equals("string") || t.content.equals("true")
        || t.content.equals("false")){
            token.currentIndex++;
            return new LiteralNode(t.content);
        }

        //identifier
        if (t.tokenTypeID.equals("identifier")){
            token.currentIndex++;
            return new IdentifierNode(t.content);
        }

        //parenthesis
        if(t.content.equals("(")){
            token.currentIndex++;
            ExpressionNode expr = parseExpression(list);

            if (!list[token.currentIndex].content.equals(")")){
                throw new RuntimeException("Expected ')'");
            }
            token.currentIndex++;

            return expr;
        }
        throw new RuntimeException("Invalid Expression");
    }

    BlockNode parseBlock(token[] list){
        if (!list[token.currentIndex].content.equals("{")){
            throw new RuntimeException("Expected '{'");
        }
        token.currentIndex++;

        List<StatementNode> statements = new ArrayList<>();

        while(!list[token.currentIndex].content.equals("}")){
            statements.add(parseStatement(list));
        }

        token.currentIndex++;
        return new BlockNode(statements);
    }

    ExpressionNode parseLogicalOr(token[] list){
        ExpressionNode left = parseLogicalAnd(list);
        while (token.currentIndex < list.length && list[token.currentIndex].content.equals("||")){
            String op = list[token.currentIndex].content;
            token.currentIndex++;
            ExpressionNode right = parseLogicalAnd(list);
            left = new BinaryExpressionNode(left, op, right);
        }
        return left;
    }

    ExpressionNode parseLogicalAnd(token[] list){
        ExpressionNode left = parseRelational(list);
        while (token.currentIndex < list.length && list[token.currentIndex].content.equals("&&")){
            String op = list[token.currentIndex].content;
            token.currentIndex++;
            ExpressionNode right = parseRelational(list);
            left = new BinaryExpressionNode(left, op, right);
        }
        return left;
    }

    ExpressionNode parseRelational(token[] list) {

        ExpressionNode left = parseAdditive(list);

        if (token.currentIndex < list.length &&
                (list[token.currentIndex].content.equals(">") ||
                        list[token.currentIndex].content.equals("<"))) {

            String op = list[token.currentIndex].content;
            token.currentIndex++;

            ExpressionNode right = parseAdditive(list);

            return new BinaryExpressionNode(left, op, right);
        }

        return left;
    }

    SpellDatabaseStatement parseSpellDatabaseStatement(token[] list) {
        token.currentIndex++;

        // string literal
        if (!list[token.currentIndex].tokenTypeID.equals("string")) {
            throw new RuntimeException("Expected string literal after spelldatabase");
        }

        String path = list[token.currentIndex].content;
        token.currentIndex++;

        // ;
        if (!list[token.currentIndex].content.equals(";")) {
            throw new RuntimeException("Expected ';' after spelldatabase statement");
        }
        token.currentIndex++;

        SpellDatabaseStatement node = new SpellDatabaseStatement(path);

        return node;
    }

    ShowStatementNode parseShowStatement(token[] list) {

        token showToken = list[token.currentIndex]; // "show"
        token.currentIndex++;

        // identifier
        if (!list[token.currentIndex].tokenTypeID.equals("identifier")) {
            throw new RuntimeException("Expected identifier after 'show'");
        }

        String name = list[token.currentIndex].content;
        token.currentIndex++;

        // ;
        if (!list[token.currentIndex].content.equals(";")) {
            throw new RuntimeException("Expected ';' after show statement");
        }
        token.currentIndex++;

        ShowStatementNode node = new ShowStatementNode(name);

        return node;
    }

    BreakStatementNode parseBreakStatement(token[] list){
        token.currentIndex++;
        if (!list[token.currentIndex].content.equals(";")){
            throw new RuntimeException("Expected ';'");
        }
        token.currentIndex++;
        BreakStatementNode node = new BreakStatementNode();
        return node;
    }

    ContinueStatementNode parseContinueStatement(token[] list){
        token.currentIndex++;
        if (!list[token.currentIndex].content.equals(";")){
            throw new RuntimeException("Expected ';'");
        }
        token.currentIndex++;
        ContinueStatementNode node = new ContinueStatementNode();
        return node;
    }

    FunctionDeclarationNode parseFunctionStatement(token[] list){
        token.currentIndex++;
        // 1. return type
        String returnType = list[token.currentIndex].content;
        token.currentIndex++;

        // 2. function name
        if (!list[token.currentIndex].tokenTypeID.equals("identifier")) {
            throw new RuntimeException("Expected function name");
        }
        String name = list[token.currentIndex].content;
        token.currentIndex++;

        // 3. (
        if (!list[token.currentIndex].content.equals("(")) {
            throw new RuntimeException("Expected '('");
        }
        token.currentIndex++;

        // 4. parameter list
        List<ParameterNode> params = new ArrayList<>();

        if (!list[token.currentIndex].content.equals(")")) {

            while (true) {
                // type
                String paramType = list[token.currentIndex].content;
                token.currentIndex++;

                // identifier
                if (!list[token.currentIndex].tokenTypeID.equals("identifier")) {
                    throw new RuntimeException("Expected parameter name");
                }
                String paramName = list[token.currentIndex].content;
                token.currentIndex++;

                params.add(new ParameterNode(paramType, paramName));

                if (list[token.currentIndex].content.equals(",")) {
                    token.currentIndex++;
                } else {
                    break;
                }
            }
        }

        // 5. )
        token.currentIndex+=2;
        if (!list[token.currentIndex].content.equals(")")) {
            throw new RuntimeException("Expected ')'");
        }
        token.currentIndex++;

        // 6. block
        BlockNode body = parseBlock(list);

        FunctionDeclarationNode node = new FunctionDeclarationNode(returnType, name, params, body);
        return node;
    }

    ReturnStatementNode parseReturnStatement(token[] list) {
        token.currentIndex++;

        // expression
        ExpressionNode value = parseExpression(list);

        // ;
        if (!list[token.currentIndex].content.equals(";")) {
            throw new RuntimeException("Expected ';' after return");
        }
        token.currentIndex++;

        ReturnStatementNode node = new ReturnStatementNode(value);
        return node;
    }
}