public class Pokemon {
    private String name;
    private int hp;
    private int attack;
    private int defend;

    public Pokemon(String name, int hp, int attack, int defend){
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.defend = defend;
    }

    public String getName(){
        return name;
    }

    public int getHp(){
        return hp;
    }

    public int getAttack(){
        return attack;
    }

    public int getDefend(){
        return defend;
    }

    public void takeDamage(int damage){
        hp -= damage;
        if (hp<0){
            hp = 0;
        }
    }

    public boolean isDead(){
        return hp<=0;
    }

    public void changeDefend(int defense){
        defend+=defense;
    }
}
