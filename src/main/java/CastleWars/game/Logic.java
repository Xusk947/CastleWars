package CastleWars.game;

import CastleWars.logic.Generator;
import CastleWars.logic.PlayerData;
import CastleWars.logic.room.Room;
import CastleWars.logic.room.UnitRoom;
import arc.math.Mathf;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Interval;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.maps.Map;

public class Logic {

    public static float END_TIMER = 60f * 60f * 10f;
    public static float SEC_TIMER = 60f;

    public float endTimer = END_TIMER;
    public IntMap<Seq<UnitRoom>> rooms;
    public Rules rules;
    public Seq<PlayerData> datas;

    Seq<Unit> privateUnits;
    Interval interval;
    boolean seted = false;

    public Logic() {
        datas = new Seq<>();
        rules = new Rules();
        rules.canGameOver = false;
        rules.waves = true;
        rules.waveTimer = false;
        rules.unitCap = 999999;
        interval = new Interval(3);
    }

    public void update() {
        if (!seted || Groups.player.size() < 1) {
            return;
        }

        endTimer--;
        if (endTimer <= 0) {
            Call.infoMessage("[gray]|DRAW|");
            Timer.schedule(() -> reset(), 3);
            seted = false;
        }

        if (interval.get(0, SEC_TIMER)) {
            datas.forEach(data -> {
                data.money += data.income;
            });
        }

        for (PlayerData data : datas) {
            StringBuilder hud = new StringBuilder();
            hud.append("[gray]Time remain: ").append(Mathf.floor(endTimer / 60f)).append("\n");
            hud.append("[gold]Balance: ").append(data.money).append("\n");
            if (data.income > 0) {
                hud.append("[lime]");
            } else {
                hud.append("[red]");
            }
            hud.append("Income: ").append(data.income);
            Call.setHudText(data.player.con, hud.toString());
        }

        for (IntMap.Entry<Seq<UnitRoom>> rooms1 : rooms) {
            for (Room room : rooms1.value) {
                room.update();
                // Touch logic
                for (PlayerData data : datas) {
                    Player player = data.player;
                    if (player.team() == room.team && player.unit() != null && room.rect(player.unit().aimX, player.unit().aimY) && player.unit().isShooting) {
                        room.onTouch(data);
                    }
                }
            }
        }

        if (interval.get(1, SEC_TIMER * 10)) {
            for (IntMap.Entry<Seq<UnitRoom>> rooms1 : rooms) {
                for (UnitRoom room : rooms1.value) {
                    StringBuilder lab = new StringBuilder();
                    lab.append("[orange]").append(room.classType).append("\n[accent]cost: [white]").append(room.cost);
                    if (room.income > 0) {
                        lab.append("\n[lime]");
                    } else if (room.income < 0) {
                        lab.append("\n[red]");
                    }
                    lab.append("income: ").append(room.income);
                    datas.forEach(data -> {
                        if (data.player.team() == room.team) {
                            Call.label(data.player.con, lab.toString(), SEC_TIMER * 10 / 60f, room.centreDrawx, room.centreDrawy - Vars.tilesize * (Room.ROOM_SIZE + 1));
                        }
                    });
                }
            }
        }

        if (Team.sharded.core()
                == null) {
            Call.infoMessage("[#" + Team.blue.color.toString() + "]| Blue Winners |");
            Timer.schedule(() -> reset(), 3);
            seted = false;
        } else if (Team.blue.core()
                == null) {
            Call.infoMessage("[#" + Team.sharded.color.toString() + "]| Sharded Winners |");
            Timer.schedule(() -> reset(), 3);
            seted = false;
        }

    }

    public void reset() {
        for (PlayerData data : datas) {
            data.money = 0;
            data.income = 20;
        }
        seted = false;
        endTimer = END_TIMER;
        privateUnits = new Seq<>();

        Seq<Player> players = new Seq<>();
        Groups.player.copy(players);

        Vars.logic.reset();
        Call.worldDataBegin();

        Generator gen = new Generator();

        Vars.world.loadGenerator(350, 200, gen);
        rooms = gen.rooms;
        Vars.state.map = new Map(StringMap.of("[white]Castle:[gray]Wars", "[red]GA[orange]ME"));

        Vars.state.rules = rules.copy();
        Vars.logic.play();

        for (Player player : players) {
            Vars.netServer.sendWorldData(player);
        }
        for (Seq<UnitRoom> rooms1 : rooms.values()) {
            for (UnitRoom room : rooms1) {
                Timer.schedule(() -> {
                    Unit unit = room.unitType.spawn(room.team, room.centreDrawx, room.centreDrawy);
                    unit.health = 999999f;
                    unit.mounts = new WeaponMount[0];
                    room.unit = unit;
                }, 2);
            }
        }

        seted = true;
    }
}
