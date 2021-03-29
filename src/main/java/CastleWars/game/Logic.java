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
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.maps.Map;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.content.UnitTypes;

public class Logic {

    public static float END_TIMER = 60f * 60f * 10f;
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
        rules.waveTimer = false;
        rules.unitCap = 999999;
        rules.teams.get(Team.blue).cheat = true;
        rules.teams.get(Team.sharded).cheat = true;
        rules.loadout.clear();
        rules.loadout.add(new ItemStack(Items.surgeAlloy, 99999), new ItemStack(Items.plastanium, 99999), new ItemStack(Items.blastCompound, 99999));
        for (Block block : Vars.content.blocks()) {
            if (block != Blocks.thoriumWall && block != Blocks.thoriumWallLarge) {
                rules.bannedBlocks.add(block);
            }
        }
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
        
        //only player loop in trigger.update
        boolean label = interval.get(1, SEC_TIMER * 10);
        for (int i = 0;i < datas.size; i++) {
            PlayerData data = datas.get(i);
            StringBuilder hud = new StringBuilder();
            hud.append("[gray]Time left: ").append(Mathf.floor(endTimer / 60f)).append("\n");
            hud.append("[gold]Balance: ").append(data.money).append("\n");
            if (data.income > 0) {
                hud.append("[lime]");
            } else {
                hud.append("[red]");
            }
            hud.append("Income: ").append(data.income);
            Call.setHudText(data.player.con, hud.toString());
            
            Player player = data.player;
            
            /*
            touch logic
            since rooms is constant in size its
            better to loop the rooms inside playerdata loop
            then loop playerdata inside rooms loop
            */
            for (IntMap.Entry<Seq<Room>> entry : rooms) {
                
                for (Room room : entry.value) {
                    if (i == 0) {
                        if (label) room.generateLabel(player);
                        room.update();
                    }
                    
                    if (player.team() == room.team && player.unit() != null && room.rect(player.mouseX, player.mouseY) && player.unit().isShooting) {
                        room.onTouch(data);
                    }
                }
            }
            
            if (player.unit() != null) {
                if ((player.unit().type == UnitTypes.gamma || player.unit().type == UnitTypes.alpha) && player.team().core() != null) {
                    Unit unit = UnitTypes.risso.create(Team.crux);
                    unit.set(player.team().core().x, player.team().core().y + 4 * Vars.tilesize);
                    unit.add();
                    unit.team(player.team());
                    unit.spawnedByCore = true;
                    player.unit(unit);
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
            data.income = PlayerData.basicIncome;
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
        for (Seq<Room> rooms1 : rooms.values()) {
            for (Room room : rooms1) {
                if (room instanceof UnitRoom) {
                    ((UnitRoom) room).spawn(2);
                }
            }
        }

        seted = true;
    }
}
