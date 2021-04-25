package CastleWars.logic;

import CastleWars.data.PlayerData;
import arc.struct.Seq;
import mindustry.Vars;

public abstract class Room implements RoomComp {

    public static Seq<Room> rooms = new Seq<Room>();
    public static int ROOM_SIZE = 8;

    public int size = ROOM_SIZE;
    public float drawSize = ROOM_SIZE * Vars.tilesize;
    public String label = "";

    public int x, y, centrex, centrey, endx, endy;
    public float drawx, drawy, centreDrawx, centreDrawy, endDrawx, endDrawy;


    public int cost;

    public Room(int x, int y, int cost, int size) {

        this.cost = cost;
        this.size = size;

        this.x = x;
        this.y = y;
        this.centrex = x + size;
        this.centrey = y + size;
        this.endx = x + size;
        this.endy = y + size;
        this.drawx = x * Vars.tilesize;
        this.drawy = y * Vars.tilesize;
        this.centreDrawx = (x + size) * Vars.tilesize;
        this.centreDrawy = (y + size) * Vars.tilesize;
        this.endDrawx = endx * Vars.tilesize;
        this.endDrawy = endy * Vars.tilesize;
    }

    public Room(int x, int y, int cost) {
        this(x, y, cost, ROOM_SIZE);
    }

    @Override
    public int cost() {
        return cost;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public float drawSize() {
        return drawSize();
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int endx() {
        return endx;
    }

    @Override
    public int endy() {
        return endy;
    }

    @Override
    public float drawx() {
        return drawx;
    }

    @Override
    public float drawy() {
        return drawy;
    }

    @Override
    public float endDrawx() {
        return endDrawy;
    }

    @Override
    public float endDrawy() {
        return endDrawy;
    }
    
    @Override
    public int centrex() {
        return centrex;
    }

    @Override
    public int centrey() {
        return centrey;
    }

    @Override
    public float centreDrawx() {
        return centreDrawx;
    }

    @Override
    public float centreDrawy() {
        return centreDrawy;
    }

    @Override
    public String label() {
        return label;
    }
}
