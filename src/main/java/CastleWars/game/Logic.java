package CastleWars.game;

import CastleWars.logic.Generator;
import CastleWars.logic.PlayerData;
import CastleWars.logic.Room;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.maps.Map;

public class Logic {

    public static float END_TIMER = 60f * 60f * 5f;
    public static float SEC_TIMER = 60f;

    public float endTimer = END_TIMER;
    public IntMap<Seq<Room>> rooms;
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
//        rules.pvp = true;
        rules.waveTimer = false;
        interval = new Interval(3);
    }

    public void update() {
        if (!seted) {
            return;
        }

        endTimer--;
        if (endTimer <= 0) {
            Call.infoMessage("[gray]|DRAW|");
            Timer.schedule(() -> reset(), 3);
        }

        for (IntMap.Entry<Seq<Room>> rooms1 : rooms) {
            for (Room room : rooms1.value) {
                // Unit Logic
                Unit unit = room.unit;
                if (unit != null) {
                    if (unit.isPlayer()) {
                        unit.getPlayer().unit(Nulls.unit);
                    }
                    unit.rotation(unit.rotation + unit.type.rotateSpeed);
                    unit.set(room.centreDrawx, room.centreDrawy);
                }

                // Touch Logic
                for (PlayerData data : datas) {
                    Player player = data.player;
                    if (player.unit() != null && player.unit().isShooting) {
                        if (room.rect(player.unit().aimX, player.unit().aimY) && room.team == player.team()) {
                            if (data.money - room.cost >= 0 && Team.sharded.core() != null && Team.blue.core() != null) {
                                data.money -= room.cost;
                                unit = room.unitType.create(Team.crux);
                                if (room.classType == Room.ClassType.Defender) {
                                    unit.set(player.team().core().x, player.team().core().y + 3 * Vars.tilesize);
                                } else {
                                    float y = player.team() == Team.blue ? Team.sharded.core().y : Team.blue.core().y;
                                    unit.set(player.team().core().x, y);
                                }
                                unit.add();
                                if (room.classType == Room.ClassType.Defender) {
                                    unit.team(player.team());
                                }
                            }
                        }
                    }
                }
            }
        }

        datas.forEach(data -> {
            if (interval.get(0, SEC_TIMER)) {
                data.money += 20;
            }
            StringBuilder hud = new StringBuilder();
            hud.append("[gray]Time remain: ").append(Mathf.floor(endTimer / 60f)).append("\n");
            hud.append("[gold]Balance: ").append(data.money);
            Call.setHudText(data.player.con, hud.toString());
        });

        if (interval.get(1, SEC_TIMER * 10)) {
            for (IntMap.Entry<Seq<Room>> rooms1 : rooms) {
                for (Room room : rooms1.value) {
                    datas.forEach(data -> {
                        if (data.player.team() == room.team) {
                            Call.label(data.player.con, "[accent]" + room.unitType + " [white]: [accent]" + room.classType + "\n[orange]cost: [white]" + room.cost, SEC_TIMER * 10 / 60f, room.centreDrawx, room.centreDrawy);
                        }
                    });
                }
            }
        }

        if (Team.sharded.core() == null) {
            Call.infoMessage("[#" + Team.blue.color.toString() + "]|Blue Winners|");
            Timer.schedule(() -> reset(), 3);
            seted = false;
        } else if (Team.blue.core() == null) {
            Call.infoMessage("[#" + Team.sharded.color.toString() + "]|Sharded Winners|");
            Timer.schedule(() -> reset(), 3);
            seted = false;
        }

    }

    public void reset() {
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
        for (IntMap.Entry<Seq<Room>> room : rooms) {
            Log.info(room.key + " : " + room.value.size);
        }
        for (Seq<Room> rooms1 : rooms.values()) {
            for (Room room : rooms1) {
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
