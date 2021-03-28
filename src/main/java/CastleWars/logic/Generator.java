package CastleWars.logic;

import CastleWars.logic.room.Room;
import CastleWars.logic.room.TurretRoom;
import CastleWars.logic.room.UnitRoom;
import arc.func.Cons;
import arc.struct.IntMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;

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

        tiles.getn(bx, by).setBlock(Blocks.coreShard, Team.blue);

        Seq<Room> rm = new Seq<>();
        rm.addAll(UnitRoom.rooms);
        rm.addAll(TurretRoom.rooms);
        
        for (Team team : t) {
            Seq<Room> s = new Seq<>();
            for (Room room : rm) {
                if (room instanceof UnitRoom) {
                    int xx = (team == Team.blue ? bx : sx) + (team == Team.blue ? -room.getX() : room.getX());
                    int yy = (team == Team.blue ? by : sy) + room.getY();
                    UnitRoom room1 = (UnitRoom) room;
                    UnitRoom room2 = new UnitRoom(xx, yy, room1.unitType, room1.classType, room1.cost, room1.income);
                    room2.team = team;
                    room2.generate(tiles);
                    s.add(room2);
                }
                if (room instanceof TurretRoom) {
                    int xx = (team == Team.blue ? Vars.world.width() - room.getX() : room.getX());
                    int yy = (team == Team.blue ? Vars.world.height() - room.getY() : room.getY());
                    TurretRoom room1 = (TurretRoom) room;
                    TurretRoom room2 = new TurretRoom(xx, yy, room1.block);
                    room2.team = team;
                    room2.generate(tiles);
                    s.add(room);
                }
            }
            rooms.put(team.id, s);
        }

    }
}
