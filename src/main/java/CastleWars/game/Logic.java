package CastleWars.game;

import CastleWars.Main;
import CastleWars.data.PlayerData;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import CastleWars.logic.Room;
import arc.util.Log;
import arc.util.Timer;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Nulls;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class Logic {

    public static Seq<Block> blocks = new Seq<>();
    public boolean worldLoaded = false;
    public float x = 0, y = 0, endx = 0, endy = 0;
    
    Seq<Tile> cores = new Seq<>();

    public void update() {
        if (Vars.state.isPaused()) {
        } else {
            for (PlayerData data : PlayerData.datas.values()) {
                data.update();
            }

            for (Room room : Room.rooms) {
                room.update();
            }
            // Kill all units in centre
            Groups.unit.intersect(x, y, endx, endy, u -> {
                if (u.isPlayer()) {
                    u.getPlayer().unit(Nulls.unit);
                }
                u.kill();
            });
        }
        gameOverUpdate();
    }

    public void restart() {
        Seq<Player> players = new Seq<>();
        Groups.player.copy(players);

        Vars.logic.reset();

        for (Block block : Vars.content.blocks()) {
            if (block instanceof CoreBlock) {
                continue;
            }
            if (block != null && blocks.contains(block)) {
                block.health = blocks.find(b -> b.equals(block)).health * 10;
            }
        }
        UnitTypes.omura.abilities.clear();
        UnitTypes.mono.weapons.add(UnitTypes.crawler.weapons.get(0));
        Blocks.coreShard.unitCapModifier = 99999;
        Blocks.itemSource.health = 999999;
        Blocks.coreNucleus.unitCapModifier = 99999;
        Room.rooms.clear();

        PlayerData.datas.values().forEach(PlayerData::reset);

        Generator gen = new Generator();
        gen.run();
        cores = gen.cores.copy();
        Call.worldDataBegin();
        
        int half = gen.height - (Room.ROOM_SIZE * 6);
        half = half / 2;
        y = half * Vars.tilesize;
        endy = (Room.ROOM_SIZE * 6) * Vars.tilesize;
        x = -5 * Vars.tilesize;
        endx = (5 + gen.width) * Vars.tilesize;
        
        for (Player player : players) {
            Vars.netServer.assignTeam(player, players);
            Vars.netServer.sendWorldData(player);
            PlayerData.labels(player);
        }

        Call.setRules(Main.rules);
        Vars.logic.play();
        Vars.state.rules = Main.rules;
        Call.setRules(Vars.state.rules);
        // AntiInstantGameStart
        Timer.schedule(() -> {
            worldLoaded = true;
        }, 5);
    }

    public void gameOverUpdate() {
        if (!worldLoaded) {
            return;
        }
        int s = 0, b = 0;
        for (Tile core : cores) {
            if (core.build != null) {
                if (core.build.team == Team.sharded) {
                    s++;
                } else if (core.build.team == Team.blue) {
                    b++;
                }
            }
        }
        if (s == 0) {
            gameOver(Team.blue);
        } else if (b == 0) {
            gameOver(Team.sharded);
        }
    }

    public void gameOver(Team team) {
        String winner = "[gold]Winner: ";

        if (team != Team.derelict) {
            winner += "[#" + team.color.toString() + "]" + team.name;
        }
        Call.infoMessage(winner);
        Timer.schedule(() -> {
            restart();
        }, 3);
        worldLoaded = false;
    }
}
