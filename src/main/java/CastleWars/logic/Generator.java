package CastleWars.logic;

import arc.func.Cons;
import arc.struct.IntMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
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

        // Generate: rooms for cores
        int sx = tiles.width / 8,
                sy = tiles.height / 4;
        int bx = tiles.width - tiles.width / 8,
                by = tiles.height - tiles.height / 4;

        Team[] t = new Team[]{Team.sharded, Team.blue};

        tiles.getn(sx, sy).setBlock(Blocks.coreShard, Team.sharded);
        tiles.getn(sx, by).setOverlay(Blocks.spawn);

        tiles.getn(bx, by).setBlock(Blocks.coreShard, Team.blue);
        tiles.getn(bx, sy).setOverlay(Blocks.spawn);

        for (Team team : t) {
            Seq<Room> s = new Seq<>();
            for (Room room : Room.rooms) {
                int xx = (team == Team.blue ? bx : sx) + room.getX();
                int yy = (team == Team.blue ? by : sy) + room.getY();
                for (int x = -Room.ROOM_SIZE; x <= Room.ROOM_SIZE; x++) {
                    for (int y = -Room.ROOM_SIZE; y <= Room.ROOM_SIZE; y++) {
                        Floor floor = (Floor) Blocks.metalFloor;

                        if (x == -Room.ROOM_SIZE || y == -Room.ROOM_SIZE || x == Room.ROOM_SIZE || y == Room.ROOM_SIZE) {
                            floor = (Floor) Blocks.space;
                        }
                        tiles.getn(xx + x, yy + y).setFloor(floor);
                    }
                }
                Room room1 = new Room(xx, yy, room.unitType, room.classType, room.cost);
                room1.team = team;
                s.add(room1);
            }
            rooms.put(team.id, s);
        }

    }
}
