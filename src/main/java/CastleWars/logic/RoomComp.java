package CastleWars.logic;

import CastleWars.data.PlayerData;
import mindustry.content.Blocks;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;

public interface RoomComp {
    public int cost();
    
    public int size();
    public float drawSize();
    
    public int x();
    public int y();
    
    public int centrex();
    public int centrey();
    
    public int endx();
    public int endy();
    
    public float drawx();
    public float drawy();
    
    public float centreDrawx();
    public float centreDrawy();
    
    public float endDrawx();
    public float endDrawy();
    
    public String label();
    
    public void buy(PlayerData data);
    
    public void update();
    
    default boolean canBuy(PlayerData data) {
        return data.money >= cost();
    }
    
    default boolean check(float x, float y) {
        return (x > x() && y > y() && x < endDrawx() && y < endDrawy());
    }
    
    default void spawn(Tiles t) {
        for (int x = 0; x <= size(); x++) {
            for (int y = 0; y <= size(); y++) {
                if (x == 0 || y == 0 || x == size() || y == size()) {
                    t.getn(x, y).setFloor((Floor) Blocks.metalFloor5);
                } else {
                    t.getn(x, y).setFloor((Floor) Blocks.metalFloor);
                }
            }
        }
    }
}
