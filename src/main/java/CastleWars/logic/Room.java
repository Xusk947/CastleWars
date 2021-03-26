package CastleWars.logic;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class Room {

    public static final int ROOM_SIZE = 3;
    public static final float ROOM_DRAW_SIZE = ROOM_SIZE / 2 * Vars.tilesize;
    public static final int PUDDLE = (ROOM_SIZE * 2 + 1 + 3);

    public static Seq<Room> rooms = new Seq<>(new Room[]{
        new Room(-2 * PUDDLE, 0, UnitTypes.arkyid, ClassType.Attacker, 1800),
        new Room(-1 * PUDDLE, -1 * PUDDLE, UnitTypes.dagger, ClassType.Attacker, 50),
        new Room(-1 * PUDDLE, 0, UnitTypes.toxopid, ClassType.Attacker, 8000),
        new Room(-1 * PUDDLE, PUDDLE, UnitTypes.pulsar, ClassType.Attacker, 380),
        new Room(0 * PUDDLE, PUDDLE, UnitTypes.fortress, ClassType.Attacker, 500),
        new Room(0 * PUDDLE, PUDDLE * 2, UnitTypes.scepter, ClassType.Attacker, 1500),
        new Room(0 * PUDDLE, PUDDLE * -2, UnitTypes.vela, ClassType.Defender, 1700),
        new Room(0 * PUDDLE, -1 * PUDDLE, UnitTypes.dagger, ClassType.Defender, 80),
        new Room(2 * PUDDLE, 0, UnitTypes.quasar, ClassType.Defender, 580),
        new Room(1 * PUDDLE, PUDDLE, UnitTypes.spiroct, ClassType.Defender, 360),
        new Room(1 * PUDDLE, 0, UnitTypes.fortress, ClassType.Defender, 350),
        new Room(1 * PUDDLE, -1 * PUDDLE, UnitTypes.atrax, ClassType.Defender, 120),});

    private final int x, y;
    public float drawx, drawy, endDrawx, endDrawy, centreDrawx, centreDrawy;
    public int cost;
    public Team team;
    public UnitType unitType;
    public ClassType classType;
    public Unit unit;

    public Room(int x, int y, UnitType unitType, ClassType classType, int cost) {
        this.x = x;
        this.y = y;
        this.drawx = (x - ROOM_SIZE) * Vars.tilesize;
        this.drawy = (y - ROOM_SIZE) * Vars.tilesize;
        this.endDrawx = (x + ROOM_SIZE) * Vars.tilesize;
        this.endDrawy = (y + ROOM_SIZE) * Vars.tilesize;
        this.centreDrawx = x * Vars.tilesize;
        this.centreDrawy = y * Vars.tilesize;
        this.unitType = unitType;
        this.classType = classType;
        this.cost = cost;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean rect(float x, float y) {
        return x >= drawx && y >= drawy && x <= endDrawx && y <= endDrawy;
    }

    public enum ClassType {
        Attacker, Defender;
    }
}
