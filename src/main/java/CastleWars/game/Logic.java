package CastleWars.game;

import CastleWars.Main;
import CastleWars.data.PlayerData;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import CastleWars.logic.Room;
import arc.util.Timer;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class Logic {

    public boolean worldLoaded = false;
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
            
            for (Unit unit : Groups.unit) {
                if (unit.tileOn().equals(Blocks.space)) {
                    unit.kill();
                }
            }
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
            if (block != null) {
                block.health = 999999999;
            }
        }
        UnitTypes.omura.abilities.clear();
        UnitTypes.crawler.defaultController = UnitTypes.fortress.defaultController;
        UnitTypes.mono.weapons.add(UnitTypes.crawler.weapons.get(0));
        Blocks.coreShard.unitCapModifier = 99999;
        Blocks.coreNucleus.unitCapModifier = 99999;
        Room.rooms.clear();

        PlayerData.datas.values().forEach(PlayerData::reset);

        Generator gen = new Generator();
        gen.run();
        cores = gen.cores.copy();
        Call.worldDataBegin();

        for (Player player : players) {
            Vars.netServer.assignTeam(player, players);
            Vars.netServer.sendWorldData(player);
            PlayerData.labels(player);
        }

        Call.setRules(Main.rules);
        Vars.logic.play();
        Vars.state.rules = Main.rules;
        Call.setRules(Vars.state.rules);
        Timer.schedule(() -> {
            worldLoaded = true;
        }, 3);
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
