import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class InterpreterVisitor implements Visitor<Object> {

    private Map<String, Object> variables = new HashMap<>();
    private Map<String, Pokemon> pokemonMap = new HashMap<>();
    private Map<String, Spell> spellDatabase = new HashMap<>();

    private void loadSpellDatabase(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath));

        String currentName = null;
        String currentType = null;
        Integer currentValue = null;

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }

            if (line.endsWith("{")) {
                currentName = line.substring(0, line.length() - 1).trim();
            }
            else if (line.equals("}")) {
                if (currentName != null && currentType != null && currentValue != null) {
                    Spell spell = new Spell(currentName, currentType, currentValue);
                    spellDatabase.put(currentName, spell);
                } else {
                    throw new RuntimeException("Invalid spell definition: " + currentName);
                }

                currentName = null;
                currentType = null;
                currentValue = null;
            }
            else if (line.equals("Attack,") || line.equals("Attack")) {
                currentType = "Attack";
            }
            else if (line.equals("Defend,") || line.equals("Defend")) {
                currentType = "Defend";
            }
            else if (line.startsWith("Value")) {
                String[] parts = line.split("\\s+");
                currentValue = Integer.parseInt(parts[1]);
            }
        }

        System.out.println("Spell database loaded");
    }
    @Override
    public Object visitProgramNode(ProgramNode node) {
        for (StatementNode stmt : node.statements) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Object visitVariable(VariableDeclerationNode node) {
        Object value = node.value.accept(this);

        // Special handling for Pokemon variables
        if (node.type.equals("Pokemon")) {

            String displayName = String.valueOf(value);

            Pokemon pokemon =
                    new Pokemon(displayName, 100, 30, 10);

            variables.put(node.name, pokemon);
            pokemonMap.put(node.name, pokemon);

            System.out.println(displayName + " enters the battle!");

            return null;
        }

        // Normal variables
        variables.put(node.name, value);
        return null;
    }

    @Override
    public Object visitAssignmentStatementNode(AssignmentStatementNode node) {
        Object value = node.right.accept(this);
        variables.put(String.valueOf(node.left), value);
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpressionNode node) {
        Object value = node.operand.accept(this);

        switch (node.operator) {
            case "!":
                return !(Boolean) value;

            case "-":
                return -(Integer) value;

            default:
                throw new RuntimeException("Unknown unary operator: " + node.operator);
        }
    }

    @Override
    public Object visitIdentifierNode(IdentifierNode node) {
        if (!variables.containsKey(node.name)) {
            throw new RuntimeException("Undefined variable: " + node.name);
        }

        return variables.get(node.name);
    }

    @Override
    public Object visitFunctionDeclerationNode(FunctionDeclarationNode node) {
        return null;
    }

    @Override
    public Object visitFunctionCallExpressionNode(FunctionCallExpressionNode node) {
        return null;
    }

    @Override
    public Object visitForStatementNode(ForStatementNode node) {
        node.init.accept(this);

        while ((Boolean) node.condition.accept(this)) {
            node.body.accept(this);
            node.update.accept(this);
        }

        return null;
    }

    @Override
    public Object visitFieldAccessNode(FieldAccessNode node) {
        String pokemonName = String.valueOf(node.object);
        String field = node.field;

        System.out.println("Accessing " + field + " of " + pokemonName);

        if (field.equals("hp")) return 100;
        if (field.equals("attack")) return 50;
        if (field.equals("defend")) return 30;

        throw new RuntimeException("Unknown Pokemon field: " + field);
    }

    @Override
    public Object visitContinueStatementNode(ContinueStatementNode node) {
        return null;
    }

    @Override
    public Object visitBreakNode(BreakStatementNode node) {
        return null;
    }

    @Override
    public Object visitBlockNode(BlockNode node) {
        for(StatementNode stmt : node.statements){
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Object visitLiteralExpr(LiteralNode node) {
        if (node.value.equals("true")) return true;
        if (node.value.equals("false")) return false;

        try {
            return Integer.parseInt(node.value);
        } catch (NumberFormatException e) {
            return node.value;
        }
    }

    @Override
    public Object visitBinaryExpr(BinaryExpressionNode node) {
        Object left = node.left.accept(this);
        Object right = node.right.accept(this);

        switch (node.operator) {
            case "+":
                if (left instanceof String || right instanceof String) {
                    return String.valueOf(left) + String.valueOf(right);
                }
                return (Integer) left + (Integer) right;

            case "-":
                return (Integer) left - (Integer) right;

            case "*":
                return (Integer) left * (Integer) right;

            case "/":
                return (Integer) left / (Integer) right;

            case "==":
                return left.equals(right);

            case "!=":
                return !left.equals(right);

            case "<":
                return (Integer) left < (Integer) right;

            case ">":
                return (Integer) left > (Integer) right;

            case "<=":
                return (Integer) left <= (Integer) right;

            case ">=":
                return (Integer) left >= (Integer) right;

            case "&&":
                return (Boolean) left && (Boolean) right;

            case "||":
                return (Boolean) left || (Boolean) right;

            default:
                throw new RuntimeException("Unknown operator: " + node.operator);
        }
    }

    @Override
    public Object visitPrintStatementNode(PrintStatementNode node) {
        Object value = node.expression.accept(this);
        System.out.println(value);
        return null;
    }

    @Override
    public Object visitShowStatementNode(ShowStatementNode node) {
        System.out.println("Showing: " + node.identifier);
        return null;
    }

    @Override
    public Object visitReturnStatementNode(ReturnStatementNode node) {
        return null;
    }

    @Override
    public Object visitMoveStatementNode(MoveStatementNode node) {

        // 1. Get Pokemon
        Pokemon attacker = pokemonMap.get(node.attacker);
        Pokemon target = pokemonMap.get(node.target);

        if (attacker == null) {
            throw new RuntimeException("Unknown attacker: " + node.attacker);
        }

        if (target == null) {
            throw new RuntimeException("Unknown defender: " + node.target);
        }

        // 2. Ensure spell database is loaded
        if (spellDatabase.isEmpty()) {
            throw new RuntimeException("Spell database not loaded.");
        }

        // 3. Get spell
        Spell spell = spellDatabase.get(node.move);

        if (spell == null) {
            throw new RuntimeException("Unknown spell: " + node.move);
        }

        // 4. Check if attacker can act
        if (attacker.isDead()) {
            System.out.println(attacker.getName() + " cannot attack because it has fainted!");
            return null;
        }

        // 5. Announce move
        System.out.println(attacker.getName() + " uses " + spell.name + "!");

        // 6. Apply spell effect
        if (spell.type.equals("Attack")) {

            int damage = attacker.getAttack() + spell.value - target.getDefend();

            // Optional: allow healing if negative
            if (damage < 0) {
                target.takeDamage(-damage);
                System.out.println(target.getName() + " is healed for " + (-damage) + " HP.");
            } else {
                damage = Math.max(1, damage);
                target.takeDamage(damage);

                System.out.println(target.getName() + " takes " + damage + " damage.");
            }

            System.out.println(target.getName() + " HP is now " + target.getHp() + ".");

            if (target.isDead()) {
                System.out.println(target.getName() + " fainted!");
            }

        } else if (spell.type.equals("Defend")) {

            attacker.changeDefend(spell.value);

            System.out.println(attacker.getName() + "'s defense changed by " + spell.value + ".");
        }

        return null;
    }

    @Override
    public Object visitPokemonLoadStatementNode(PokemonLoadStatement node) {
        Pokemon p = new Pokemon(node.name, 100, 30, 10);

        pokemonMap.put(node.name, p);
        variables.put(node.name, p);

        System.out.println(node.name + " enters the battle!");
        System.out.println("HP: " + p.getHp() + ", Attack: " + p.getAttack() + ", Defense: " + p.getDefend());

        return null;
    }

    @Override
    public Object visitParameterNode(ParameterNode node) {
        return null;
    }

    @Override
    public Object visitSpellDatabaseNode(SpellDatabaseStatement node) {
        try {
            loadSpellDatabase(node.path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Object visitIfStatementNode(IfStatementNode node) {
        Object condition = node.condition.accept(this);

        if ((Boolean) condition) {
            node.thenBlock.accept(this);
        } else {
            if (node.elseBlock != null) {
                node.elseBlock.accept(this);
            }
        }

        return null;
    }

    @Override
    public Object visitWhileStatementNode(WhileStatementNode node) {
        while ((Boolean) node.condition.accept(this)) {
            node.body.accept(this);
        }

        return null;
    }


    @Override
    public Object visitStatementNode(StatementNode node) {
        return null;
    }
}