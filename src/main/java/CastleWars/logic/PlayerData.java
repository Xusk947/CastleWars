package CastleWars.logic;

import mindustry.gen.Player;

public class PlayerData {

    public static int basicIncome = 20;
    public int money = 0;
    public int income = basicIncome;
    public Player player;
    
    public PlayerData(Player player) {
        this.player = player;
    }
}
