package CastleWars.logic;

import CastleWars.game.Logic;
import arc.Events;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.type.UnitType;

public class UnitCost {
    public static ObjectMap<UnitType, Integer> cost = new ObjectMap<>();
    public static Logic logic;
    
    public static void init(Logic logicc) {
        logic = logicc;
        // Ground
        cost.put(UnitTypes.dagger, 25);
        cost.put(UnitTypes.mace, 50);
        cost.put(UnitTypes.fortress, 75);
        cost.put(UnitTypes.scepter, 700);
        cost.put(UnitTypes.reign, 4000);
        // Support
        cost.put(UnitTypes.nova, 30);
        cost.put(UnitTypes.pulsar, 60);
        cost.put(UnitTypes.quasar, 100);
        cost.put(UnitTypes.vela, 500);
        cost.put(UnitTypes.corvus, 3000);
        // Spider
        cost.put(UnitTypes.crawler, 15);
        cost.put(UnitTypes.atrax, 55);
        cost.put(UnitTypes.spiroct, 80);
        cost.put(UnitTypes.arkyid, 800);
        cost.put(UnitTypes.toxopid, 7000);
        
        Events.on(EventType.UnitDestroyEvent.class, event -> {
            if (event.unit.team == Team.crux && cost.containsKey(event.unit.type)) {
                int bonus = cost.get(event.unit.type);
                for (PlayerData data : logic.datas) {
                    if (data.player.team() == Team.blue && event.unit.y() > Vars.world.height() * Vars.tilesize / 2) {
                        //Call.label(data.player.con, "[lime]+" + cost.get(event.unit.type), 1, event.unit.x, event.unit.y);
                        data.money += bonus;
                    } else if(data.player.team() == Team.sharded && event.unit.y() < Vars.world.height() * Vars.tilesize / 2) {
                        data.money += bonus;
                        //Call.label(data.player.con, "[lime]+" + cost.get(event.unit.type), 1, event.unit.x, event.unit.y);
                    }
                }
            }
        });
    }
}
