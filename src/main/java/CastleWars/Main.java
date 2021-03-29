package CastleWars;

import CastleWars.game.Logic;
import CastleWars.logic.PlayerData;
import CastleWars.logic.UnitCost;
import CastleWars.logic.room.TurretRoom;
import arc.Events;
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

public class Main extends Plugin {

    Logic logic;

    @Override
    public void init() {
        logic = new Logic();
        UnitCost.init(logic);
        TurretRoom.init();

        Events.run(EventType.Trigger.update, () -> {
            Groups.unit.intersect((Vars.world.height() * Vars.tilesize) / 2, (Vars.world.height() * Vars.tilesize) / 2, 1, 1, unit -> {
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
            Vars.content.units().each(u->u.weapons.each(w->w.bullet.recoil = 0));
        });
    }
}
