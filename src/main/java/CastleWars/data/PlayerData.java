package CastleWars.data;

import arc.Events;
import arc.struct.IntMap;
import mindustry.game.EventType;
import mindustry.gen.Player;

public class PlayerData {
    public static IntMap<PlayerData> datas = new IntMap<PlayerData>();
    
    public Player player;
    public int money;

    public PlayerData(Player player) {
        this.player = player;
    }
    
    public static void init() {
        Events.on(EventType.PlayerJoin.class, event -> {
            datas.put(event.player.id, new PlayerData(event.player));
        });
        
        Events.on(EventType.PlayerLeave.class, event -> {
            datas.remove(event.player.id);
        });
    }
}
