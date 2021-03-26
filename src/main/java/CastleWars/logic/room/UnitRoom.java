package CastleWars.logic.room;

import CastleWars.logic.PlayerData;
import static CastleWars.logic.room.Room.PUDDLE;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class UnitRoom extends Room {

    public UnitType unitType;
    public ClassType classType;
    public Unit unit;
    public int income;

    public UnitRoom(int x, int y, UnitType unitType, ClassType classType, int cost, int inc) {
        super(x, y);
        this.cost = cost;
        this.income = inc;
        this.unitType = unitType;
        this.classType = classType;
    }
    
    public static Seq<UnitRoom> rooms = new Seq<>(new UnitRoom[]{
        new UnitRoom(-2 * PUDDLE, 0, UnitTypes.arkyid, ClassType.Attacker, 2200, -20),
        new UnitRoom(-1 * PUDDLE, -1 * PUDDLE, UnitTypes.dagger, ClassType.Attacker, 50, 1),
        new UnitRoom(-1 * PUDDLE, 0, UnitTypes.mace, ClassType.Attacker, 120, 2),
        new UnitRoom(-1 * PUDDLE, PUDDLE, UnitTypes.pulsar, ClassType.Attacker, 380, 0),
        new UnitRoom(0 * PUDDLE, PUDDLE, UnitTypes.fortress, ClassType.Attacker, 500, -2),
        new UnitRoom(0 * PUDDLE, PUDDLE * 2, UnitTypes.scepter, ClassType.Attacker, 1500, -15),
        new UnitRoom(0 * PUDDLE, PUDDLE * -2, UnitTypes.toxopid, ClassType.Attacker, 7000, -400),
        new UnitRoom(0 * PUDDLE, -1 * PUDDLE, UnitTypes.quasar, ClassType.Defender, 580, 0),
        new UnitRoom(2 * PUDDLE, 0, UnitTypes.dagger, ClassType.Defender, 80, 0),
        new UnitRoom(1 * PUDDLE, PUDDLE, UnitTypes.spiroct, ClassType.Defender, 360, 0),
        new UnitRoom(1 * PUDDLE, 0, UnitTypes.fortress, ClassType.Defender, 350, -1),
        new UnitRoom(1 * PUDDLE, -1 * PUDDLE, UnitTypes.scepter, ClassType.Defender, 2000, -20),});

    @Override
    public void update() {
        if (unit != null) {
            if (unit.isPlayer()) {
                unit.getPlayer().unit(Nulls.unit);
            }
            unit.rotation(unit.rotation + unit.type.rotateSpeed);
            unit.set(centreDrawx, centreDrawy);
        }

    }

    @Override
    public void onTouch(PlayerData data) {
        if (canBuy(data) && Team.sharded.core() != null && Team.blue.core() != null) {
            buy(data);
            Log.info(income);
            data.income += income;
        }
    }

    public boolean canBuy(PlayerData data) {
        return data.income - income > 0 && data.money - cost > 0;
    }
    
    public void buy(PlayerData data) {
        Player player = data.player;

        data.money -= cost;
        Unit unit1 = unitType.create(Team.crux);
        if (classType == Room.ClassType.Defender) {
            unit1.set(player.team().core().x, player.team().core().y + 3 * Vars.tilesize);
        } else {
            float y = player.team() == Team.blue ? Team.sharded.core().y : Team.blue.core().y;
            unit1.set(player.team().core().x, y);
        }
        unit1.add();
        if (classType == Room.ClassType.Defender) {
            unit1.team(player.team());
        }

    }
}