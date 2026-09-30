import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
            try {
                System.out.print("Enter file path: ");
                String path = input.nextLine();

                File file = new File(path);

                if (!file.exists()) {
                    System.out.println("File not found.");
                    return;
                }

                Scanner scanner = new Scanner(file);
                StringBuilder program = new StringBuilder();

                while (scanner.hasNextLine()) {
                    program.append(scanner.nextLine()).append("\n");
                }

                scanner.close();

                token[] tokens = tokenizer(program.toString());

               /*for (token t : tokens) {
                    if (t != null) {
                        System.out.println(t.tokenTypeID + " : " + t.content);
                    }
                }*/
                Parser parser = new Parser();
                ProgramNode root = parser.parseProgram(tokens);
                //System.out.println("Parsed Statements: "+AST.parsedStatements);
                ASTPrinterVisitor printer = new ASTPrinterVisitor();
                System.out.println("\nAst:\n");
                System.out.println(root.accept(printer));
                ScopeVisitor scopeVisitor = new ScopeVisitor();
                System.out.println("\nSemantic Scope Analysis:\n");
                root.accept(scopeVisitor);
                System.out.println("No scope errors");
                TypeCheckVisitor typeVisitor = new TypeCheckVisitor();
                System.out.println("\n Semantic type check Analysis\n");
                root.accept(typeVisitor);
                if (!typeVisitor.hasError()){
                    System.out.println("No type errors");
                }

                InterpreterVisitor interpreter = new InterpreterVisitor();
                root.accept(interpreter);

            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            }
    }

    public static token[] tokenizer(String text) {
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! maybe change to array list
        token[] tokenArray = new token[255];
        int tokenIndex = 0;
        //Stack<Character> bracketStack = new Stack<>();
        boolean wasDigitLast = false;
        boolean wasOperatorLast = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            //labels token as a digit
            if (Character.isDigit(c)) {
                if (!wasDigitLast) {
                    token piece = new token();
                    piece.tokenTypeID = "digit";
                    piece.content = String.valueOf(c);
                    tokenArray[tokenIndex++] = piece;
                    wasDigitLast = true;
                } else {
                    tokenIndex--;
                    tokenArray[tokenIndex].content = tokenArray[tokenIndex].content + c;
                    tokenIndex++;
                }
                wasOperatorLast=false;
            } else if (Character.isLetter(c)) {
                token piece = new token();
                String word = String.valueOf(c);
                Set<String> keywords = Set.of(
                        "int", "String", "Pokemon", "boolean",
                        "function", "void", "if", "elseif", "else",
                        "while", "for", "break", "continue", "return",
                        "print", "show", "spelldatabase",
                        "move1", "move2", "move3", "move4",
                        "true", "false"
                );
                while (i + 1 < text.length() && (Character.isLetterOrDigit(text.charAt(i + 1)))) {
                    i++;
                    if (Character.isLetterOrDigit(text.charAt(i))) {
                        word = word + text.charAt(i);
                    }
                    if (word.equals("else")){
                        continue;
                    } else if (keywords.contains(word)){
                        wasDigitLast = false;
                        break;
                    }
                    wasDigitLast = false;
                }

                if (keywords.contains(word)) {
                    piece.tokenTypeID = "keyword";
                } else {
                    piece.tokenTypeID = "identifier";
                }

                piece.content = word;
                tokenArray[tokenIndex++] = piece;
                wasOperatorLast=false;
            } else if (Character.isWhitespace(c)) {
                continue;
            } else {
                switch (c) {
                    case '!': {
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = "!";
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '"': {
                        String str = "";

                        while (i + 1 < text.length() && text.charAt(i + 1) != '"') {
                            i++;
                            str += text.charAt(i);
                        }

                        if (i + 1 >= text.length()) {
                            throw new RuntimeException("Unclosed string literal");
                        }

                        i++; // move past closing quote

                        token piece = new token();
                        piece.tokenTypeID = "string";
                        piece.content = str;
                        tokenArray[tokenIndex++] = piece;

                        break;
                    }
                    case '*': {
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = String.valueOf(c);
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '+': {
                            if (tokenArray[tokenIndex-1].content.equals("+")){
                                tokenIndex--;
                                tokenArray[tokenIndex].content="++";
                                tokenIndex++;
                                break;
                            }
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = String.valueOf(c);
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '-': {
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = String.valueOf(c);
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '.': {
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = String.valueOf(c);
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '[': {
                        //bracketStack.push('[');
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = "[";
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case ']': {
                        //if (bracketStack.isEmpty() || bracketStack.pop() != '[') {
                          //  throw new RuntimeException("Mismatched ] at position " + i);
                        //}
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = "]";
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '(': {
                        //bracketStack.push('(');
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = "(";
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case ')': {
                        //if (bracketStack.isEmpty() || bracketStack.pop() != '(') {
                        //    throw new RuntimeException("Mismatched ) at position " + i);
                        //}
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = ")";
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '{': {
                        //bracketStack.push('{');
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = "{";
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '}': {
                        //if (bracketStack.isEmpty() || bracketStack.pop() != '{') {
                        //    throw new RuntimeException("Mismatched } at position " + i);
                        //}
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = "}";
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    case '=': {
                        if (!wasOperatorLast) {
                            token piece = new token();
                            piece.tokenTypeID = "operator";
                            tokenArray[tokenIndex++] = piece;
                            piece.content = String.valueOf(c);
                            wasOperatorLast = true;
                        } else if (tokenArray[tokenIndex - 1].content.equals("=") || tokenArray[tokenIndex - 1].content.equals(">") ||
                                tokenArray[tokenIndex - 1].content.equals("<")) {

                            tokenIndex--;
                            tokenArray[tokenIndex].content = tokenArray[tokenIndex].content + "=";

                        }
                        break;
                    }
                    case '<': {
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        tokenArray[tokenIndex++] = piece;
                        piece.content = String.valueOf(c);
                        wasOperatorLast = true;
                        break;
                    }
                    case '>': {
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = String.valueOf(c);
                        tokenArray[tokenIndex++] = piece;
                        wasOperatorLast = true;
                        break;
                    }
                    //logic for near operator?
                    case '/': {
                        token piece = new token();
                        piece.tokenTypeID = "operator";
                        piece.content = String.valueOf(c);
                        tokenArray[tokenIndex++] = piece;
                        wasOperatorLast = true;
                        break;
                    }
                    case '&': {
                        if (!wasOperatorLast) {
                            token piece = new token();
                            piece.tokenTypeID = "operator";
                            tokenArray[tokenIndex++] = piece;
                            piece.content = String.valueOf(c);
                            wasOperatorLast = true;
                        } else if (tokenArray[tokenIndex - 1].content.equals("&")) {
                            tokenIndex--;
                            tokenArray[tokenIndex].content = tokenArray[tokenIndex].content + "&";

                        }
                        break;
                    }
                    case '|': {
                        if (!wasOperatorLast) {
                            token piece = new token();
                            piece.tokenTypeID = "operator";
                            tokenArray[tokenIndex++] = piece;
                            piece.content = String.valueOf(c);
                            wasOperatorLast = true;
                        } else if (tokenArray[tokenIndex - 1].content.equals("|")) {
                            tokenIndex--;
                            tokenArray[tokenIndex].content = tokenArray[tokenIndex].content + "|";

                        }
                        break;
                    }
                    case ';': {
                        token piece = new token();
                        piece.tokenTypeID = "semicolon";
                        piece.content = String.valueOf(c);
                        tokenArray[tokenIndex++] = piece;
                        break;
                    }
                    default:
                        break;
                }
                wasOperatorLast = true;
                wasDigitLast = false;
            }
        }
        //if (!bracketStack.isEmpty()) {
        //    throw new RuntimeException("Missing closing bracket(s)");
        //}


        return tokenArray;
    }
}