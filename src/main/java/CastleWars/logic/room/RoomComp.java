package CastleWars.logic.room;

import CastleWars.logic.PlayerData;
import mindustry.world.Tiles;

public interface RoomComp {
    public boolean rect(float x, float y);
    public boolean canBuy(int balance);
    public boolean canBuy(PlayerData data);
    public void onTouch(PlayerData data);
    public void update();
    public void generate(Tiles tiles);
    public void generateLabel();
}
