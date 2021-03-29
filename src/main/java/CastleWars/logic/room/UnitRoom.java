package CastleWars.logic.room;

import static CastleWars.game.Logic.SEC_TIMER;
import CastleWars.logic.PlayerData;
import static CastleWars.logic.room.Room.PUDDLE;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;

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
        // Attacker
        new UnitRoom(-1 * PUDDLE, 0 * PUDDLE, UnitTypes.scepter, ClassType.Attacker, 1700, 17),
        new UnitRoom(-1 * PUDDLE, 1 * PUDDLE, UnitTypes.dagger, ClassType.Attacker, 50, 0),
        new UnitRoom(-2 * PUDDLE, 1 * PUDDLE, UnitTypes.mace, ClassType.Attacker, 100, 1),
        new UnitRoom(-3 * PUDDLE, 1 * PUDDLE, UnitTypes.fortress, ClassType.Attacker, 300, 3),
        new UnitRoom(-1 * PUDDLE, 2 * PUDDLE, UnitTypes.atrax, ClassType.Attacker, 100, 1),
        new UnitRoom(-2 * PUDDLE, 2 * PUDDLE, UnitTypes.spiroct, ClassType.Attacker, 150, 1),
        new UnitRoom(-3 * PUDDLE, 2 * PUDDLE, UnitTypes.arkyid, ClassType.Attacker, 2200, 20),
        new UnitRoom(-1 * PUDDLE, 3 * PUDDLE, UnitTypes.bryde, ClassType.Attacker, 1200, 10),
        new UnitRoom(-2 * PUDDLE, 3 * PUDDLE, UnitTypes.sei, ClassType.Attacker, 5000, 45),
        new UnitRoom(-3 * PUDDLE, 0 * PUDDLE, UnitTypes.toxopid, ClassType.Attacker, 7000, 70),
        new UnitRoom(-1 * PUDDLE, -1 * PUDDLE, UnitTypes.pulsar, ClassType.Attacker, 200, 2),
        new UnitRoom(0, 2 * PUDDLE, UnitTypes.reign, ClassType.Attacker, 5000, 50),
        // Defender
        new UnitRoom(-2 * PUDDLE, 0 * PUDDLE, UnitTypes.scepter, ClassType.Defender, 2000, -20),
        new UnitRoom(-2 * PUDDLE, -1 * PUDDLE, UnitTypes.quasar, ClassType.Defender, 450, -1),
        new UnitRoom(-3 * PUDDLE, -1 * PUDDLE, UnitTypes.fortress, ClassType.Defender, 300, -1),
        new UnitRoom(-1 * PUDDLE, -2 * PUDDLE, UnitTypes.atrax, ClassType.Defender, 100, -1),
        new UnitRoom(-2 * PUDDLE, -2 * PUDDLE, UnitTypes.spiroct, ClassType.Defender, 150, -1),
        new UnitRoom(-3 * PUDDLE, -2 * PUDDLE, UnitTypes.arkyid, ClassType.Defender, 2200, -22),
        new UnitRoom(-1 * PUDDLE, -3 * PUDDLE, UnitTypes.bryde, ClassType.Defender, 1200, -10),
        new UnitRoom(-2 * PUDDLE, -3 * PUDDLE, UnitTypes.sei, ClassType.Defender, 4500, -40),
        new UnitRoom(0, -2 * PUDDLE, UnitTypes.reign, ClassType.Defender, 5000, -50)
    });

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
        }
    }

    @Override
    public boolean canBuy(PlayerData data) {
        if (income > 0) {
            return data.money - cost >= 0;
        }
        return data.income - income >= 0 && data.money - cost >= 0;
    }

    public void buy(PlayerData data) {
        Player player = data.player;

        data.income += income;
        data.money -= cost;
        Unit unit1 = unitType.create(Team.crux);
        if (classType == Room.ClassType.Defender) {
            unit1.set(player.team().core().x, player.team().core().y + 3 * Vars.tilesize);
        } else {
            float y = player.team() == Team.blue ? Team.sharded.core().y : Team.blue.core().y;
            unit1.set(player.team().core().x, y + Mathf.random(-16, 16));
        }
        unit1.add();
        if (classType == Room.ClassType.Defender) {
            unit1.team(player.team());
        }
    }

    public void spawn(int sec) {
        Timer.schedule(() -> {
            Unit unit1 = unitType.spawn(team, team == Team.blue ? centreDrawx : centreDrawx, centreDrawy);
            unit1.health = 999999f;
            unit1.mounts = new WeaponMount[0];
            unit = unit1;
        }, 2);
    }

    @Override
    public void generateLabel(Player player) {
        StringBuilder lab = new StringBuilder();
        lab.append("[orange]").append(classType).append("\n[accent]cost: [white]").append(cost);
        if (income > 0) {
            lab.append("\n[lime]income: ").append(income);
        } else if (income < 0) {
            lab.append("\n[red]income: ").append(income);
        }

        Call.label(player.con, lab.toString(), SEC_TIMER * 10 / 60f, centreDrawx, centreDrawy - Vars.tilesize * (Room.ROOM_SIZE + 1));
    }

    @Override
    public void generate(Tiles tiles) {
        for (int xx = -Room.ROOM_SIZE; xx <= Room.ROOM_SIZE; xx++) {
            for (int yy = -Room.ROOM_SIZE; yy <= Room.ROOM_SIZE; yy++) {
                Floor floor = (Floor) Blocks.metalFloor;

                if (xx == -Room.ROOM_SIZE || yy == -Room.ROOM_SIZE || xx == Room.ROOM_SIZE || yy == Room.ROOM_SIZE) {
                    floor = (Floor) Blocks.space;
                }
                tiles.getn(xx + x, yy + y).setFloor(floor);
                tiles.getn(xx + x, yy + y).setBlock(Blocks.air);
            }
        }
    }
}
