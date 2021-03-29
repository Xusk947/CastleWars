package CastleWars;

import CastleWars.game.Logic;
import CastleWars.logic.PlayerData;
import CastleWars.logic.UnitCost;
import CastleWars.logic.room.TurretRoom;
import arc.Events;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Nulls;
import mindustry.gen.Unit;
import mindustry.mod.Plugin;
import arc.util.CommandHandler;
import mindustry.graphics.Pal;
import arc.graphics.Colors;
import arc.util.Strings;
import mindustry.gen.Player;
import mindustry.core.NetClient;

public class Main extends Plugin {

    Logic logic;

    static {

        //the UI puts these in colors and the server never inits the UI meaning that the plugin needs to put these in
        Colors.put("accent", Pal.accent);
        Colors.put("unlaunched", Color.valueOf("8982ed"));
        Colors.put("highlight", Pal.accent.cpy().lerp(Color.white, 0.3F));
        Colors.put("stat", Pal.stat);
    }

    @Override
    public void init() {
        logic = new Logic();
        UnitCost.init(logic);
        TurretRoom.init();

        Events.run(EventType.Trigger.update, () -> {
            Groups.unit.intersect(0, (Vars.world.height() * Vars.tilesize) / 2, Vars.world.width() * Vars.tilesize, 1, unit -> {
                if (unit.team.core() != null) {
                    unit.set(unit.team().data().core().x, unit.team().data().core().y + 4 * Vars.tilesize);
                    if (unit.isPlayer()) {
                        unit.getPlayer().unit(Nulls.unit);
                    }
                };
            });

            Groups.player.each(player -> {
                if (player.unit() != null) {
                    if (player.unit().type == UnitTypes.alpha && player.team().core() != null) {
                        Unit unit = UnitTypes.dagger.create(Team.crux);
                        unit.set(player.team().core().x, player.team().core().y + 4 * Vars.tilesize);
                        unit.add();
                        unit.team(player.team());
                        unit.spawnedByCore = true;
                        player.unit(unit);
                    }
                }
            });
            logic.update();
        });

        Events.on(EventType.PlayerJoin.class, event -> {
            logic.datas.add(new PlayerData(event.player));
            if (Groups.player.count(p -> p.team() == Team.sharded) > Groups.player.count(p -> p.team() == Team.blue)) {
                event.player.team(Team.blue);
            } else {
                event.player.team(Team.sharded);
            }
            Call.sendMessage(event.player.con, "You in: [#" + event.player.team().color.toString() + "]" + event.player.team().name + " [white]team", "[sky][Omni]", Nulls.player);
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            logic.datas.remove(data -> data.player.equals(event.player));
        });

        Events.on(EventType.ServerLoadEvent.class, event -> {
            logic.reset();
            Vars.netServer.openServer();

            Blocks.coreShard.unitCapModifier = 999999;
            Blocks.coreNucleus.unitCapModifier = 999999;
            Blocks.coreFoundation.unitCapModifier = 999999;
            Vars.content.units().each(u -> u.weapons.each(w -> w.bullet.recoil = 0));
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("pay", "<amount> <username...>", "pay someone money", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("[scarlet]You don't have permission to run this command.");
            }
            
            int amount = 0;
            try {
                amount = Integer.parseInt(args[0]);
                if (amount <= 0) {
                    player.sendMessage("Invalid payment amount.");
                }
            } catch (NumberFormatException ignored) {
                player.sendMessage("Invalid payment amount.");
                return;
            }

            //playerddata
            PlayerData give = null;
            PlayerData remove = null;
            //only loop playerdata once instead of using .find twice
            for (PlayerData p : logic.datas) {
                if (Strings.stripColors(p.player.name).equalsIgnoreCase(args[1])) {
                    give = p;
                } else if (p.player.id == player.id) {
                    remove = p;
                }
            }
            
            if (give == null || remove == null) {
                player.sendMessage("Could not find " + args[1]);
            }
            if (amount > remove.money) {
                player.sendMessage("[scarlet]You do not have enough money to give " + NetClient.colorizeName(give.player.id, give.player.name) + "[white] " + amount);
            }
            
            give.money += amount;
            remove.money -= amount;
            
            player.sendMessage("Successfully sent " + NetClient.colorizeName(give.player.id, give.player.name) + "[white] " + amount);
        });
        handler.<Player>register("info", "Info for Castle Wars", (args, player) -> {
            player.sendMessage("[lime]Defender[white] units defend the core.\n"
                    + "[scarlet]Attacker[white] units attack the [scarlet]enemy[white] team.\n"
                    + "Income is your money per second [scarlet]don't ever let it go negative.[white]\n"
                    + "Shoot at units to buy units.\n"
                    + "Why can't I buy this unit? If your income is below the income of the unit you can't buy it.");
        });
    }
}
