package CastleWars.logic;

import CastleWars.logic.room.CoreRoom;
import CastleWars.logic.room.DrillRoom;
import CastleWars.logic.room.Room;
import CastleWars.logic.room.TurretRoom;
import CastleWars.logic.room.UnitRoom;
import arc.func.Cons;
import arc.struct.IntMap;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.WaterMovec;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;

public class Generator implements Cons<Tiles> {

    public static int MARGIN = 8;

    public IntMap<Seq<Room>> rooms;

    @Override
    public void get(Tiles tiles) {
        rooms = new IntMap<>();

        // Generate: main walls
        for (int x = 0; x < tiles.width; x++) {
            for (int y = 0; y < tiles.height; y++) {
                Block floor = Blocks.stone,
                        wall = Blocks.air;
                // centre
                if ((y > tiles.height / 2 - MARGIN && y < tiles.height / 2 + MARGIN)) {
                    wall = Blocks.stoneWall;
                }
                // sides up - bottom
                if (y < MARGIN || y > tiles.height - MARGIN) {
                    wall = Blocks.stoneWall;
                }
                // sides right - left
                if (x < MARGIN || x > tiles.width - MARGIN) {
                    wall = Blocks.stoneWall;
                }

                tiles.set(x, y, new Tile(x, y, floor, Blocks.air, wall));
            }
        }

        for (int x = 0; x < tiles.width; x++) {
            for (int y = tiles.height / 4 - tiles.height / 8; y < tiles.height / 4 + tiles.height / 8; y++) {
                tiles.getn(x, y).setFloor((Floor) (Blocks.sandWater));
            }
        }

        for (int x = 0; x < tiles.width; x++) {
            for (int y = tiles.height / 2 + (tiles.height / 4 - tiles.height / 8); y < tiles.height / 2 + (tiles.height / 4 + tiles.height / 8); y++) {
                tiles.getn(x, y).setFloor((Floor) (Blocks.sandWater));
            }
        }

        // Generate: rooms for cores
        int sx = (int) ((float) tiles.width / 7.8f),
                sy = tiles.height / 4;
        int bx = tiles.width - (int) ((float) tiles.width / 7.8f),
                by = tiles.height - tiles.height / 4;

        Team[] t = new Team[]{Team.sharded, Team.blue};

        tiles.getn(sx, sy).setBlock(Blocks.coreShard, Team.sharded);
        Building core = tiles.getn(sx, sy).build;
        if (core != null) core.health = core.maxHealth;

        tiles.getn(bx, by).setBlock(Blocks.coreShard, Team.blue);
        core = tiles.getn(bx, by).build;
        if (core != null) core.health = core.maxHealth;

        Seq<Room> rm = new Seq<>();
        rm.addAll(UnitRoom.rooms);
        rm.addAll(TurretRoom.rooms);
        rm.addAll(CoreRoom.rooms);
        rm.addAll(DrillRoom.rooms);

        for (Team team : t) {
            Seq<Room> s = new Seq<>();
            for (Room room : rm) {
                int xx = (team == Team.blue ? bx : sx) + (team == Team.blue ? -room.getX() : room.getX());
                int yy = (team == Team.blue ? by : sy) + room.getY();
                if (room instanceof UnitRoom) {
                    UnitRoom room1 = (UnitRoom) room;
                    UnitRoom room2 = new UnitRoom(xx, yy, room1.unitType, room1.classType, room1.cost, room1.income);
                    room2.team = team;
                    room2.generate(tiles);
                    if (room2.unitType == UnitTypes.bryde || room2.unitType == UnitTypes.sei) {
                        tiles.getn(xx, yy).setFloor((Floor) (Blocks.water));
                    }
                    s.add(room2);
                }
                if (room instanceof TurretRoom) {
                    TurretRoom room1 = (TurretRoom) room;
                    TurretRoom room2 = new TurretRoom(xx, yy, room1.block, room1.cost);
                    room2.team = team;
                    room2.generate(tiles);
                    s.add(room2);
                }
                if (room instanceof CoreRoom) {
                    CoreRoom room1 = new CoreRoom(xx, yy);
                    room1.team = team;
                    room1.generate(tiles);
                    s.add(room1);
                }
                if (room instanceof DrillRoom) {
                    DrillRoom room1 = new DrillRoom(xx, yy);
                    room1.team = team;
                    room1.generate(tiles);
                    s.add(room1);
                }
            }
            rooms.put(team.id, s);
        }

    }
}
