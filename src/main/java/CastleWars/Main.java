package CastleWars;

import CastleWars.data.Icon;
import arc.util.CommandHandler;
import mindustry.mod.Plugin;
import mindustry.gen.Player;
import CastleWars.data.PlayerData;
import CastleWars.data.UnitDeathData;
import CastleWars.game.Logic;
import arc.Events;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.world.Block;

public class Main extends Plugin {

    public static Rules rules;
    public Logic logic;

    @Override
    public void init() {
        rules = new Rules();
        rules.pvp = true;
        rules.canGameOver = false;
        rules.teams.get(Team.sharded).cheat = true;
        rules.teams.get(Team.blue).cheat = true;
        rules.waves = true;

        for (Block block : Vars.content.blocks()) {
            if (block == Blocks.thoriumWall || block == Blocks.thoriumWallLarge || block == Blocks.plastaniumWall || block == Blocks.plastaniumWallLarge || block == Blocks.phaseWall || block == Blocks.phaseWallLarge) {
                continue;
            }
            rules.bannedBlocks.add(block);
        }

        UnitDeathData.init();
        PlayerData.init();
        Icon.load();

        logic = new Logic();

        Events.on(EventType.ServerLoadEvent.class, e -> {
            Logic.blocks = Vars.content.blocks().copy();
            logic.restart();
            Vars.netServer.openServer();
        });

        Events.run(EventType.Trigger.update, () -> {
            logic.update();
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("info", "Info for Castle Wars", (args, player) -> {
            player.sendMessage("[orange]" + Icon.get(Blocks.commandCenter) + "Defender [white]units defend the core.\n"
                    + "[scarlet]" + Icon.get(Blocks.duo) + "Attacker[white] units attack the [scarlet]enemy[lime] team.\n"
                    + "[accent]Income [white]is your money per second [scarlet]don't ever let it go negative.\n"
                    + "[accent]Shoot [white]at units to buy units.\n"
                    + "[accent]Why can't I buy this unit? [white]If your income is below the income of the unit and its a defender you can't buy it.");
        });
        handler.<Player>register("inforu", "Информация по игре в Castle Wars", (args, player) -> {
            player.sendMessage("[orange]" + Icon.get(Blocks.commandCenter) + "Защитники [white] защищают ядро.\n"
                    + "[scarlet]" + Icon.get(Blocks.duo) + "Атакующие[white] атакуют ядро[scarlet] вражеской[lime] комманды.\n"
                    + "[accent]Income [white]ваш доход в секунду [scarlet] не дайте ему стать негативным.\n"
                    + "[accent]Стреляйте в магазин [white]чтобы купить содержимое.\n"
                    + "[accent]Почему я не могу купить юнитов? [white]если при покупке ваш доход меньше чем доход от юнита то вы не сможете купить его.");
        });
                handler.<Player>register("label", "Show shops label | use when labbels hidden", (args, player) -> {
                    PlayerData.labels(player);
                });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("restart", "start new game", (t) -> {
            logic.restart();
        });
    }
}
