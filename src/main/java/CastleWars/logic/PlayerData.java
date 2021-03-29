package CastleWars.logic;

import mindustry.gen.Player;
import arc.util.Timer.Task;
import arc.util.Interval;

public class PlayerData {

    public static int basicIncome = 20;
    public int money = 0;
    public int income = basicIncome;
    public Player player;
    public Task buying = null;
    public Interval buyLimiter = new Interval(1);
    
    public PlayerData(Player player) {
        this.player = player;
    }
}
