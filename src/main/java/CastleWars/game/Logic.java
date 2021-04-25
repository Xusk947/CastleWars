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
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.blocks.storage.CoreBlock;

public class Logic {

    public void update() {
        if (Vars.state.isPaused()) {
            return;
        }
        
        for (PlayerData data : PlayerData.datas.values()) {
            data.update();
        }
        
        for (Room room : Room.rooms) {
            room.update();
        }
        
        if (Team.sharded.cores().size <= 0) {
            Log.info("sharded lost");
        } 
        if (Team.blue.cores().size <= 0) {
            Log.info("blue lost");
        }
    }

    public void restart() {
        Seq<Player> players = new Seq<>();
        Groups.player.copy(players);

        Vars.logic.reset();
        
        for (Block block : Vars.content.blocks()) {
            if (block instanceof CoreBlock) continue;
            block.health = 999999999;
        }
        UnitTypes.omura.abilities.clear();
        UnitTypes.mono.weapons.add(UnitTypes.crawler.weapons.get(0));
        Blocks.coreShard.unitCapModifier = 99999;
        Blocks.coreNucleus.unitCapModifier = 99999;
        Room.rooms.clear();
        
        PlayerData.datas.values().forEach(PlayerData::reset);
        
        Generator gen = new Generator();
        gen.run();
        Call.worldDataBegin();

        for (Player player : players) {
            Vars.netServer.sendWorldData(player);
            PlayerData.labels(player);
        }
        Call.setRules(Main.rules);
        Vars.logic.play();
        Vars.state.rules = Main.rules;
        Call.setRules(Vars.state.rules);
    }
}
