package CastleWars.logic;

import CastleWars.data.PlayerData;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Block;

public class TurretRoom extends Room {

    public boolean buyyed = false;
    Block block;
    Team team;

    public TurretRoom(Team team, Block block, int x, int y, int cost, int size) {
        super(x, y, cost, size);
        this.team = team;
        this.block = block;
        
        label = "[gray]Turret: [white]" + block.name + "\n[gold]Cost: [white]" + cost;
    }
    
    @Override
    public void buy(PlayerData data) {
        buyyed = true;
        Vars.world.tile(x, y).setNet(block);
    }

    @Override
    public boolean canBuy(PlayerData data) {
        return super.canBuy(data) && !buyyed;
    }

    @Override
    public void update() {
        if (buyyed) {
            if (Vars.world.tile(x + 1, y + 1).build == null) {
                Vars.world.tile(x + 1, y + 1).setNet(block, team, 0);
            }
        };
    }

}
