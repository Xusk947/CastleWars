package CastleWars.game;

import CastleWars.Main;
import CastleWars.data.PlayerData;
import arc.struct.Seq;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import static CastleWars.data.PlayerData.labels;

public class Logic {

    public void update() {
        if (Vars.state.isPaused()) {
            return;
        }
        
        for (PlayerData data : PlayerData.datas.values()) {
            data.update();
        }
    }

    public void restart() {
        Seq<Player> players = new Seq<>();
        Groups.player.copy(players);

        Vars.logic.reset();

        Generator gen = new Generator();
        Call.worldDataBegin();
        gen.run();

        for (Player player : players) {
            Vars.netServer.sendWorldData(player);
            Timer.schedule(() -> {
                labels(player);
            }, 1);
        }
        Call.setRules(Main.rules);
        Vars.logic.play();
        Vars.state.rules = Main.rules;
        Call.setRules(Vars.state.rules);

    }
}
