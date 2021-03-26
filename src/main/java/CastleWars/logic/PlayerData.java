package CastleWars.logic;

import mindustry.gen.Player;

public class PlayerData {
    public int money = 0;
    public int income = 20;
    public Player player;
    
    public PlayerData(Player player) {
        this.player = player;
    }
}
