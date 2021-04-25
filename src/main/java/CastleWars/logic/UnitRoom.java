package CastleWars.logic;

import CastleWars.data.PlayerData;
import arc.math.Mathf;
import arc.util.Timer;
import mindustry.content.UnitTypes;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.Tiles;

public class UnitRoom extends Room {

    public static Tile blueSpawn, shardedSpawn;
    public enum Type {
        Attacker, Defender;
    }

    public UnitType unit;
    public Type type;
    public int income = 0;

    public UnitRoom(UnitType type, int x, int y, int cost, int income, Type clas) {
        super(x, y, cost);
        this.unit = type;
        this.type = clas;
        this.income = income;
        StringBuilder str = new StringBuilder();
        str.append("[white]").append(clas);
        str.append("[white] : ").append(cost).append(" [white]: ");
        if (income > 0) {
            str.append("[lime]").append(income);
        }
        if (income < 0) {
            str.append("[crimson]").append(income);
        }
        label = str.toString();
    }

    @Override
    public void buy(PlayerData data) {
        data.money -= cost;
        if (type == Type.Attacker) {
            Unit u = unit.spawn(data.player.team(), (data.player.team() == Team.sharded ? blueSpawn.drawx() : shardedSpawn.drawx()) + Mathf.random(-40, 40),  (data.player.team() == Team.sharded ? blueSpawn.drawy() : shardedSpawn.drawy()) + Mathf.random(-40, 40));
            if (u.type == UnitTypes.crawler) {
                u.type = UnitTypes.mono;
            }
            u.team(data.player.team());
        } else if (data.player.team().core() != null) {
            Unit u = unit.spawn(data.player.team(), data.player.team().core().x + 30, data.player.team().core().y + Mathf.random(-40, 40));
            if (u.type == UnitTypes.crawler) {
                u.type = UnitTypes.mono;
            }
            u.team(data.player.team());
        }

    }

    @Override
    public void spawn(Tiles t) {
        super.spawn(t); //To change body of generated methods, choose Tools | Templates.
        Timer.schedule(() -> {
            Unit u = unit.spawn(Team.sharded, centreDrawx, centreDrawy);
            u.team = Team.crux;
            u.health = 9999999f;
            u.mounts = new WeaponMount[0];
        }, 1);
    }

    @Override
    public void update() {
    }

}
