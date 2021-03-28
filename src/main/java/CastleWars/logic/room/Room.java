package CastleWars.logic.room;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;

public abstract class Room implements RoomComp {

    public static final int ROOM_SIZE = 3;
    public static final float ROOM_DRAW_SIZE = ROOM_SIZE / 2 * Vars.tilesize;
    public static final int PUDDLE = (ROOM_SIZE * 2 + 1 + 3);

    final int x, y;
    public float drawx, drawy, endDrawx, endDrawy, centreDrawx, centreDrawy;
    public int cost;

    public Team team;

    public Room(int x, int y) {
        this.x = x;
        this.y = y;
        this.drawx = (x - ROOM_SIZE) * Vars.tilesize;
        this.drawy = (y - ROOM_SIZE) * Vars.tilesize;
        this.endDrawx = (x + ROOM_SIZE) * Vars.tilesize;
        this.endDrawy = (y + ROOM_SIZE) * Vars.tilesize;
        this.centreDrawx = x * Vars.tilesize;
        this.centreDrawy = y * Vars.tilesize;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean rect(float x, float y) {
        return x >= drawx && y >= drawy && x <= endDrawx && y <= endDrawy;
    }

    @Override
    public boolean canBuy(int balance) {
        return balance - cost >= 0;
    }

    public enum ClassType {
        Attacker, Defender;
    }
}
