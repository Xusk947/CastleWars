package CastleWars.logic;

import CastleWars.data.PlayerData;
import mindustry.Vars;

public abstract class Room implements RoomComp {
    public static int ROOM_SIZE = 8;
    
    public int size = ROOM_SIZE;
    public float drawSize = ROOM_SIZE * Vars.tilesize;
    
    public int x, y, endx, endy;
    public float drawx, drawy, endDrawx, endDrawy;

    public int cost;
            
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
    public void buy(PlayerData data) {
    }
}
