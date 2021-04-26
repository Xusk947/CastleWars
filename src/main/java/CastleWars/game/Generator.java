package CastleWars.game;

import CastleWars.Main;
import CastleWars.logic.CoreRoom;
import CastleWars.logic.ResourceRoom;
import CastleWars.logic.Room;
import CastleWars.logic.TurretRoom;
import CastleWars.logic.UnitRoom;
import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.storage.CoreBlock;

public class Generator implements Cons<Tiles> {

    Tiles saved;
    int width, height;
    Seq<Tile> cores;

    public Generator() {
        cores = new Seq<>();

        Vars.world.loadMap(Vars.maps.getNextMap(Gamemode.pvp, Vars.state.map), Main.rules.copy());
        saved = Vars.world.tiles;
        width = saved.width;
        height = saved.height * 2 + (Room.ROOM_SIZE * 6);
    }

    public Seq<Tile> run() {
        Vars.world.loadGenerator(width, height, this);
        for (Teams.TeamData teamData : Vars.state.teams.active) {
            for (CoreBlock.CoreBuild core : teamData.cores) {
                core.kill();
            }
        }
        return cores;
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
                // Core Build
                if (saved.getn(x, y).floor().equals(Blocks.darkPanel1)) {
                    final int cx = x, cy = y, cyy = yy;
                    Timer.schedule(() -> {
                        t.getn(cx, cy).setNet(Blocks.coreShard, Team.sharded, 0);
                        t.getn(cx, cyy).setNet(Blocks.coreShard, Team.blue, 0);
                    }, 1);

                    cores.add(t.getn(x, y));
                    cores.add(t.getn(x, yy));

                    addCoreRoom(t.getn(x, y), yy);
                }
                // Turret Build
                if (saved.getn(x, y).floor().equals(Blocks.darkPanel3)) {
                    turretGen(t.getn(x, y), yy);
                }
                // Spawners place
                if (saved.getn(x, y).floor().equals(Blocks.darkPanel2)) {
                    UnitRoom.shardedSpawn = t.get(x, y);
                    UnitRoom.blueSpawn = t.get(x, yy);
                }
            }
        }
        // UnitShop in centre
        unitInit(t);

        for (Room room : Room.rooms) {
            room.spawn(t);
        }
    }

    private void unitInit(Tiles t) {
        int cx = 2, cy = saved.height + 2;
        int Padding = Room.ROOM_SIZE + 2;
        // Ground
        addUnit(UnitTypes.dagger, cx, cy + 2, 50, 0);
        addUnit(UnitTypes.mace, cx + Padding, cy + 2, 100, 1);
        addUnit(UnitTypes.fortress, cx + Padding * 2, cy + 2, 300, 3);
        addUnit(UnitTypes.scepter, cx + Padding * 3, cy + 2, 2400, 24);
        addUnit(UnitTypes.reign, cx + Padding * 4, cy + 2, 7000, 70);
        // Support 
        cx += 2;
        addUnit(UnitTypes.nova, cx + Padding * 5, cy + 2, 60, 0);
        addUnit(UnitTypes.pulsar, cx + Padding * 6, cy + 2, 120, 1);
        addUnit(UnitTypes.quasar, cx + Padding * 7, cy + 2, 400, 4);
        addUnit(UnitTypes.vela, cx + Padding * 8, cy + 2, 2000, 20);
        addUnit(UnitTypes.corvus, cx + Padding * 9, cy + 2, 8000, 80);
        // Spiders
        cx -= 2;
        addUnit(UnitTypes.crawler, cx, cy + 2 + Padding * 2, 40, 0);
        addUnit(UnitTypes.atrax, cx + Padding, cy + 2 + Padding * 2, 100, 1);
        addUnit(UnitTypes.spiroct, cx + Padding * 2, cy + 2 + Padding * 2, 300, 3);
        addUnit(UnitTypes.arkyid, cx + Padding * 3, cy + 2 + Padding * 2, 2600, 26);
        addUnit(UnitTypes.toxopid, cx + Padding * 4, cy + 2 + Padding * 2, 9000, 90);
        // Naval 
        cx += 2;
        addUnit(UnitTypes.risso, cx + Padding * 5, cy + 2 + Padding * 2, 250, 2);
        addUnit(UnitTypes.minke, cx + Padding * 6, cy + 2 + Padding * 2, 350, 3);
        addUnit(UnitTypes.bryde, cx + Padding * 7, cy + 2 + Padding * 2, 800, 8);
        addUnit(UnitTypes.sei, cx + Padding * 8, cy + 2 + Padding * 2, 3500, 32);
        addUnit(UnitTypes.omura, cx + Padding * 9, cy + 2 + Padding * 2, 10000, 100);
    }

    private void turretGen(Tile tile, int yy) {
        // ForeShadow
        if (tile.nearby(1, 1).floor().equals(Blocks.darkPanel6)
                && tile.nearby(-1, -1).floor().equals(Blocks.darkPanel6)
                && tile.nearby(-1, 1).floor().equals(Blocks.darkPanel6)
                && tile.nearby(1, -1).floor().equals(Blocks.darkPanel6)) {
            addTurret(Blocks.foreshadow, tile, yy, 4000, 5);
        } // Spectre
        else if (tile.nearby(1, 1).floor().equals(Blocks.darkPanel4)
                && tile.nearby(-1, -1).floor().equals(Blocks.darkPanel4)
                && tile.nearby(-1, 1).floor().equals(Blocks.darkPanel4)
                && tile.nearby(1, -1).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.spectre, tile, yy, 3000, 5);
        } // MeltDown 
        else if (tile.nearby(1, 1).floor().equals(Blocks.darkPanel6)
                && tile.nearby(-1, -1).floor().equals(Blocks.darkPanel6)) {
            addTurret(Blocks.meltdown, tile, yy, 2500, 5);
        } // Cyclone        
        else if (tile.nearby(1, 1).floor().equals(Blocks.darkPanel4)
                && tile.nearby(-1, -1).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.cyclone, tile, yy, 1300, 4);
        } // Ripple
        else if (tile.nearby(0, 1).floor().equals(Blocks.darkPanel4)
                && tile.nearby(0, -1).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.ripple, tile, yy, 1700, 4);
        } // Fuse
        else if (tile.nearby(1, 0).floor().equals(Blocks.darkPanel4)
                && tile.nearby(-1, 0).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.fuse, tile, yy, 750, 4);
        }// Segment
        else if (tile.nearby(0, 1).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.segment, tile, yy, 1400, 3);
        } // Lancer
        else if (tile.nearby(-1, -1).floor().equals(Blocks.darkPanel4)) {
            addTurret(Blocks.lancer, tile, yy, 350, 3);
        }
    }

    private void addTurret(Block block, Tile tile, int yy, int cost, int size) {
        Room.rooms.add(new TurretRoom(Team.sharded, block, tile.x - size / 2, tile.y - size / 2, cost, size));
        Room.rooms.add(new TurretRoom(Team.blue, block, tile.x - size / 2, yy - size / 2, cost, size));
    }

    private void addUnit(UnitType type, int x, int y, int cost, int income) {
        Room.rooms.add(new UnitRoom(type, x, y, cost, income, UnitRoom.Type.Attacker));
        if (type != UnitTypes.nova && type != UnitTypes.dagger && type != UnitTypes.crawler) {
            Room.rooms.add(new UnitRoom(type, x, y + Room.ROOM_SIZE + 2, cost, -income, UnitRoom.Type.Defender));
        }
        // Some Resource Room Why be not? xd
        if (type == UnitTypes.nova) {
            Room.rooms.add(new ResourceRoom(Items.plastanium, x, y + Room.ROOM_SIZE + 2, 350));
        } else if (type == UnitTypes.dagger) {
            Room.rooms.add(new ResourceRoom(Items.thorium, x, y + Room.ROOM_SIZE + 2, 250));
        } else if (type == UnitTypes.crawler) {
            Room.rooms.add(new ResourceRoom(Items.phaseFabric, x, y + Room.ROOM_SIZE + 2, 450));
        }
    }

    private void addCoreRoom(Tile tile, int yy) {
        Room.rooms.add(new CoreRoom(Team.sharded, tile.x - 2, tile.y - 2));
        Room.rooms.add(new CoreRoom(Team.blue, tile.x - 2, yy - 2));
    }

}
