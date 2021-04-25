package CastleWars.data;

import CastleWars.logic.Room;
import arc.Events;
import arc.struct.IntMap;
import arc.util.Interval;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;

public class PlayerData {

    public static IntMap<PlayerData> datas = new IntMap<PlayerData>();
    public static float MoneyInterval = 60f;
    public static float LabelInterval = 60f * 10f;

    public Player player;
    public int money, income = 1;
    Interval interval;

    public PlayerData(Player player) {
        this.player = player;
        interval = new Interval(2);
    }

    public void update() {
        // Income Math
        if (interval.get(0, MoneyInterval)) {
            money += income;
        }
        if (interval.get(1, LabelInterval)) {
            labels(player);
        }
        // For Room Rect
        for (Room room : Room.rooms) {
            if (room.check(player.mouseX, player.mouseY)) {

            }
        }

        // Set Hud Text
        StringBuilder str = new StringBuilder();
        str.append("[gold]money: [white]").append(money).append("\n")
                .append("[lime]income: [white]").append(income);
        Call.setHudText(player.con, str.toString());
    }

    public static void init() {
        Events.on(EventType.PlayerJoin.class, event -> {
            datas.put(event.player.id, new PlayerData(event.player));
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            datas.remove(event.player.id);
        });
    }

    public static void labels(Player player) {
        for (Room room : Room.rooms) {
            Call.label(player.con, room.label, LabelInterval / 60f, room.centreDrawy, room.centreDrawy);
        }
    }
}
