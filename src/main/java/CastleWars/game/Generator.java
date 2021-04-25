package CastleWars.game;

import CastleWars.Main;
import CastleWars.logic.Room;
import CastleWars.logic.TurretRoom;
import CastleWars.logic.UnitRoom;
import arc.func.Cons;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.type.UnitType;
import mindustry.world.Block;
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
        postGeneration(t);
    }

    public void postGeneration(Tiles t) {
        for (int x = 0; x < t.width; x++) {
            for (int y = 0; y < saved.height; y++) {
                int yy = y + saved.height + (Room.ROOM_SIZE * 6);
                if (saved.getn(x, y).floor().equals(Blocks.darkPanel1)) {
                    t.getn(x, y).setNet(Blocks.coreShard, Team.sharded, 0);
                    t.getn(x, yy).setNet(Blocks.coreShard, Team.blue, 0);
                }
                if (saved.getn(x, y).floor().equals(Blocks.darkPanel3)) {
                    turretGen(t.getn(x, y), yy);
                }
                if (saved.getn(x, y).floor().equals(Blocks.darkPanel2)) {
                    UnitRoom.shardedSpawn = t.get(x, y);
                    UnitRoom.blueSpawn = t.get(x, yy);
                }
            }
        }

        unitInit(t);

        for (Room room : Room.rooms) {
            room.spawn(t);
        }
    }

    public void unitInit(Tiles t) {
        int cx = 2, cy = saved.height + 2;
        int Padding = Room.ROOM_SIZE + 2;
        // Ground
        addUnit(UnitTypes.dagger, cx, cy + 2, 12, 0);
        addUnit(UnitTypes.mace, cx + Padding, cy + 2, 12, 0);
        addUnit(UnitTypes.fortress, cx + Padding * 2, cy + 2, 12, 0);
        addUnit(UnitTypes.scepter, cx + Padding * 3, cy + 2, 12, 0);
        addUnit(UnitTypes.reign, cx + Padding * 4, cy + 2, 12, 0);
        // Support 
        cx += 2;
        addUnit(UnitTypes.nova, cx + Padding * 5, cy + 2, 12, 0);
        addUnit(UnitTypes.pulsar, cx + Padding * 6, cy + 2, 12, 0);
        addUnit(UnitTypes.quasar, cx + Padding * 7, cy + 2, 12, 0);
        addUnit(UnitTypes.vela, cx + Padding * 8, cy + 2, 12, 0);
        addUnit(UnitTypes.corvus, cx + Padding * 9, cy + 2, 12, 0);
        // Spiders
        cx -= 2;
        addUnit(UnitTypes.crawler, cx, cy + 2 + Padding * 2, 12, 0);
        addUnit(UnitTypes.atrax, cx + Padding, cy + 2 + Padding * 2, 12, 0);
        addUnit(UnitTypes.spiroct, cx + Padding * 2, cy + 2 + Padding * 2, 12, 0);
        addUnit(UnitTypes.arkyid, cx + Padding * 3, cy + 2 + Padding * 2, 12, 0);
        addUnit(UnitTypes.toxopid, cx + Padding * 4, cy + 2 + Padding * 2, 12, 0);
        // Naval 
        cx += 2;
        addUnit(UnitTypes.risso, cx + Padding * 5, cy + 2 + Padding * 2, 12, 0);
        addUnit(UnitTypes.minke, cx + Padding * 6, cy + 2 + Padding * 2, 12, 0);
        addUnit(UnitTypes.bryde, cx + Padding * 7, cy + 2 + Padding * 2, 12, 5);
        addUnit(UnitTypes.sei, cx + Padding * 8, cy + 2 + Padding * 2, 12, 50);
        addUnit(UnitTypes.omura, cx + Padding * 9, cy + 2 + Padding * 2, 12, 100);
    }

    public void turretGen(Tile tile, int yy) {
        // ForeShadow
        if (tile.nearby(1, 1).floor().equals(Blocks.darkPanel6)
                && tile.nearby(-1, -1).floor().equals(Blocks.darkPanel6)
                && tile.nearby(-1, 1).floor().equals(Blocks.darkPanel6)
                && tile.nearby(1, -1).floor().equals(Blocks.darkPanel6)) {
            addTurret(Blocks.foreshadow, tile, yy, 12, 5);
        } // Cyclone
        else if (tile.nearby(1, 1).floor().equals(Blocks.darkPanel4)
                && tile.nearby(-1, -1).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.cyclone, tile, yy, 12, 4);
        } // Segment
        else if (tile.nearby(0, 1).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.segment, tile, yy, 12, 3);
        } // Lancer
        else if (tile.nearby(-1, -1).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.lancer, tile, yy, 12, 3);
        }
    }

    private void addTurret(Block block, Tile tile, int yy, int cost, int size) {
        Room.rooms.add(new TurretRoom(Team.sharded, block, tile.x - size / 2, tile.y - size / 2, cost, size));
        Room.rooms.add(new TurretRoom(Team.blue, block, tile.x - size / 2, yy - size / 2, cost, size));
    }

    public void addUnit(UnitType type, int x, int y, int cost, int income) {
        Room.rooms.add(new UnitRoom(type, x, y, cost, income, UnitRoom.Type.Attacker));
        Room.rooms.add(new UnitRoom(type, x, y + Room.ROOM_SIZE + 2, cost, -income, UnitRoom.Type.Defender));
    }

}
