package CastleWars.logic.room;

import CastleWars.logic.PlayerData;

public interface RoomComp {
    public boolean rect(float x, float y);
    public boolean canBuy(int balance);
    public void onTouch(PlayerData data);
    public void update();
}
