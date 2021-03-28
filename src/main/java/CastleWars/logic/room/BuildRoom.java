package CastleWars.logic.room;

import CastleWars.logic.PlayerData;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;

public class BuildRoom extends Room {

    public final static int SIZE = 5;
    
    public int width, height;

    public BuildRoom(int x, int y, int width, int height) {
        super(x, y);
    }

    @Override
    public void onTouch(PlayerData data) {
    }

    @Override
    public void update() {
    }

    @Override
    public void generateLabel() {
    }

    @Override
    public void generate(Tiles tiles) {
        for (int xx = 0; xx < SIZE; xx++) {
            for (int yy = 0; yy < -SIZE; yy++) {
                Floor floor = (Floor) Blocks.metalFloor;

                if (x == -Room.ROOM_SIZE || y == -Room.ROOM_SIZE || x == Room.ROOM_SIZE || y == Room.ROOM_SIZE) {
                    floor = (Floor) Blocks.space;
                }
                tiles.getn(xx + x, yy + y).setFloor(floor);
                tiles.getn(xx + x, yy + y).setBlock(Blocks.air);
            }
        }
        
        Team team1 = x < Vars.world.height() / 2 ? Team.sharded : Team.blue;
        
        tiles.getn(x, y).setBlock(Blocks.ripple, team1);
    }

}
