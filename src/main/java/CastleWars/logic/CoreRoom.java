package CastleWars.logic;

import mindustry.content.Blocks;
import mindustry.game.Team;

public class CoreRoom extends TurretRoom{

    public CoreRoom(Team team, int x, int y) {
        super(team, Blocks.coreNucleus, x, y, 5000, 4);
    }
    
    @Override
    public void update() {
    }

}
