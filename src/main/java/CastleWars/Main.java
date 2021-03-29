package CastleWars;

import CastleWars.game.Logic;
import CastleWars.logic.PlayerData;
import CastleWars.logic.UnitCost;
import CastleWars.logic.room.*;
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
import arc.struct.*;
import static CastleWars.game.Logic.SEC_TIMER;

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
                    if ((player.unit().type == UnitTypes.gamma || player.unit().type == UnitTypes.alpha) && player.team().core() != null) {
                        Unit unit = UnitTypes.risso.create(Team.crux);
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
        
        handler.<Player>register("buy", "<unit/building>", "buy something", (args, player) -> {
            args[0] = Strings.stripColors(args[0]);
            PlayerData data = logic.datas.find(p -> p.player.id == player.id);
            if (data.buyLimiter.get(0, SEC_TIMER * 10)) {
                player.sendMessage("[scarlet]You can only use this command every 10 seconds.");
                return;
            }
            
            for (IntMap.Entry<Seq<Room>> entry : logic.rooms) {
                
                for (Room room : entry.value) {
                    
                    //could add extra method to room like is(String name)
                    if (room instanceof UnitRoom) {
                        if (((UnitRomm)room).unitType.name.equalsIgnoreCase(args[0])) {
                            room.onTouch(data);
                            player.sendMessage("Successfully bought " + args[0]);
                            return;
                        }
                    } else if (room instanceof TurretRoom) {
                        if (((TurretRoom)room).block.name.equalsIgnoreCase(args[0])) {
                            room.onTouch(data);
                            player.sendMessage("Successfully bought " + args[0]);
                            return;
                        }
                    } else if (args[0].equalsIgnoreCase("miner") && room instanceof DrillRoom) {
                        room.onTouch(data);
                        player.sendMessage("Successfully bought " + args[0]);
                        return;
                    } else if (room instanceof CoreRoom && args[0].equalsIgnoreCase(Blocks.coreNucleus.name)) {
                        room.onTouch(data);
                        player.sendMessage("Successfully bought " + args[0]);
                        return;
                    }
                }
            }
            player.sendMessage("Could not buy " + args[0]);
        });
        
        //useful if you need to defend but want some passive income although might become slightly laggy
         handler.<Player>register("buyforever", "<unit/building/stop>", "buy something repeatedly until you run /buyforever stop", (args, player) -> {
            args[0] = Strings.stripColors(args[0]);
            PlayerData data = logic.datas.find(p -> p.player.id == player.id)
            
            if (data.buyLimiter.get(0, SEC_TIMER * 10)) {
                player.sendMessage("[scarlet]You can only use this command every 10 seconds.");
                return;
            }
            
            if (args[0].equalsIgnoreCase("stop")) {
                if (data.buying != null) data.buying.cancel();
                player.sendMessage("Successfully stopped buying.");
                return;
            }
             
            Room room1 = null;
            
            roomLoop: for (IntMap.Entry<Seq<Room>> entry : logic.rooms) {
                
                for (Room room : entry.value) {
                    
                    //could add extra method to room like is(String name)
                    if (room instanceof UnitRoom) {
                        if (((UnitRomm)room).unitType.name.equalsIgnoreCase(args[0])) {
                            room1 = room;
                            break roomLoop;
                        }
                    } else if (room instanceof TurretRoom) {
                        if (((TurretRoom)room).block.name.equalsIgnoreCase(args[0])) {
                            room1 = room;
                            break roomLoop;
                        }
                    } else if (args[0].equalsIgnoreCase("miner") && room instanceof DrillRoom) {
                        room1 = room;
                        break roomLoop;
                    } else if (room instanceof CoreRoom && args[0].equalsIgnoreCase(Blocks.coreNucleus.name)) {
                        room1 = room;
                        break roomLoop;
                    }
                }
            }
             
             if (room1 == null) {
                player.sendMessage("Could not buy " + args[0]);
                return;
             }
             
             data.buying = Timer.schedule(() -> room1.onTouch(data), 0f, 1f);
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
