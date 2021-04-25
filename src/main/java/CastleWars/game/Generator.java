package CastleWars.game;

import CastleWars.Main;
import CastleWars.logic.Room;
import arc.func.Cons;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.Tiles;

public class Generator implements Cons<Tiles> {

    Tiles saved;
    int width, height;

    public Generator() {
        Vars.world.loadMap(Vars.maps.getNextMap(Gamemode.pvp, Vars.state.map), Main.rules.copy());
        saved = Vars.world.tiles;
        width = saved.width;
        height = saved.height * 2 + (Room.ROOM_SIZE * 6);
    }

    public void run() {
        Vars.world.loadGenerator(width, height, this);
    }

    @Override
    public void get(Tiles t) {
        for (int x = 0; x < t.width; x++) {
            for (int y = 0; y < t.height; y++) {
                t.set(x, y, new Tile(x, y, Blocks.space, Blocks.air, Blocks.air));
            }
        }
        for (int x = 0; x < t.width; x++) {
            for (int y = 0; y < saved.height; y++) {
                t.getn(x, y).setFloor(saved.getn(x, y).floor());
                t.getn(x, y).setBlock(saved.getn(x, y).block());
                int yy = y + saved.height + (Room.ROOM_SIZE * 6);
                t.getn(x, yy).setFloor(saved.getn(x, y).floor());
                if (!saved.getn(x, y).block().isAir()) {
                    t.getn(x, yy).setBlock(saved.getn(x, y).block());
                }
            }
        }
        Timer.schedule(() -> {
            postGeneration(t);
        }, 1);
    }

    public void postGeneration(Tiles t) {
        for (int x = 0; x < t.width; x++) {
            for (int y = 0; y < saved.height; y++) {
                int yy = y + saved.height + (Room.ROOM_SIZE * 6);
                if (saved.getn(x, y).floor().equals(Blocks.darkPanel1)) {
                    t.getn(x, y).setNet(Blocks.coreShard, Team.sharded, 0);
                    t.getn(x, yy).setNet(Blocks.coreShard, Team.blue, 0);
                }
            }
        }
    }
}
