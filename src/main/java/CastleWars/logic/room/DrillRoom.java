package CastleWars.logic.room;

import static CastleWars.game.Logic.SEC_TIMER;
import CastleWars.logic.PlayerData;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.gen.Call;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;

public class DrillRoom extends Room {

    public static Seq<DrillRoom> rooms = new Seq<>(new DrillRoom[]{
        new DrillRoom(-3 * PUDDLE, 3 * PUDDLE),});

    public boolean buyyed = false;

    public Interval interval;

    public DrillRoom(int x, int y) {
        super(x, y);
        interval = new Interval(1);
        cost = 1000;
    }

    @Override
    public void onTouch(PlayerData data) {
        if (canBuy(data) && !buyyed) {
            buyyed = true;
            Vars.world.tile(x, y).setNet(Blocks.laserDrill, data.player.team(), 0);
            Call.label("[lime]built by:[white] " + data.player.name, 2, centreDrawx, centreDrawy);
        }
    }

    @Override
    public void update() {
        if (buyyed && interval.get(0, 60f) && team.core() != null) {
            Call.transferItemTo(Nulls.unit, Items.thorium, 4, drawx, drawy, team.core());
            Call.transferItemTo(Nulls.unit, Items.blastCompound, 4, drawx, drawy, team.core());
            Call.transferItemTo(Nulls.unit, Items.surgeAlloy, 4, drawx, drawy, team.core());
            Call.transferItemTo(Nulls.unit, Items.plastanium, 4, drawx, drawy, team.core());
        }
    }

    @Override
    public void generate(Tiles tiles) {
        for (int xx = -Room.ROOM_SIZE; xx <= Room.ROOM_SIZE; xx++) {
            for (int yy = -Room.ROOM_SIZE; yy <= Room.ROOM_SIZE; yy++) {
                Floor floor = (Floor) Blocks.metalFloor;

                if (xx == -Room.ROOM_SIZE || yy == -Room.ROOM_SIZE || xx == Room.ROOM_SIZE || yy == Room.ROOM_SIZE) {
                    floor = (Floor) Blocks.space;
                }
                tiles.getn(xx + x, yy + y).setFloor(floor);
                tiles.getn(xx + x, yy + y).setBlock(Blocks.air);
            }
        }
    }

    @Override
    public void generateLabel(Player player) {
        if (!buyyed) {
            StringBuilder lab = new StringBuilder();

            lab.append("[accent]cost: ").append(cost);
            lab.append("\n[#").append(Items.thorium.color.toString()).append("]miner");
            Call.label(player.con, lab.toString(), SEC_TIMER * 10 / 60f, centreDrawx, centreDrawy - Vars.tilesize * (Blocks.coreShard.size + 1));
        }
    }

}
