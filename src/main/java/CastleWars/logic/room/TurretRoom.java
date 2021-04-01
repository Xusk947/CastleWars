package CastleWars.logic.room;

import static CastleWars.game.Logic.SEC_TIMER;
import CastleWars.logic.PlayerData;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;
import arc.util.Interval;
import mindustry.world.blocks.defense.turrets.Turret;

public class TurretRoom extends Room {

    public final static int SIZE = 5;
    public static ObjectMap<Block, Item> items = new ObjectMap<>();

    public static Seq<TurretRoom> rooms = new Seq<>(new TurretRoom[]{
        new TurretRoom(8, 7, Blocks.lancer, 500),
        new TurretRoom(8, -7, Blocks.lancer, 500),
        new TurretRoom(10, -15, Blocks.foreshadow, 4000),
        new TurretRoom(20, -15, Blocks.foreshadow, 4000),
        new TurretRoom(20, 15, Blocks.foreshadow, 4000),
        new TurretRoom(10, 15, Blocks.foreshadow, 4000),
    });

    public Block block;
    public Item item = null;
    public Tile tile;
    public boolean buyyed = false;
    public Interval interval

    public static void init() {
        items.put(Blocks.ripple, Items.plastanium);
        items.put(Blocks.lancer, null);
        items.put(Blocks.foreshadow, Items.surgeAlloy);
        items.put(Blocks.fuse, Items.thorium);
    }

    public TurretRoom(int x, int y, Block turret, int cost) {
        super(x, y);
        this.block = turret;
        this.item = items.get(turret);
        this.cost = cost;
        this.interval = new Interval(1);
        /*
        if (Vars.netServer == null) {
            Timer.schedule(() -> {
                Vars.netServer.admins.addActionFilter(action -> (action.type == Administration.ActionType.breakBlock || action.type == Administration.ActionType.placeBlock) && (action.tile != this.tile));
            }, 5f);
        } else {
            Vars.netServer.admins.addActionFilter(action -> (action.type == Administration.ActionType.breakBlock || action.type == Administration.ActionType.placeBlock) && (action.tile != this.tile));
        }*/
    }

    public void buy(PlayerData data) {
        buyyed = true;
        data.money -= cost;
    }

    @Override
    public void onTouch(PlayerData data) {
        if (canBuy(data) && !buyyed) {
            buy(data);
            Call.label("[lime]built by:[white] " + data.player.name, 2, centreDrawx, centreDrawy);
        }
    }

    @Override
    public void update() {
        if (buyyed && tile.build != null && item != null && this.tile.build instanceof Turret.TurretBuild && interval.get(0, SEC_TIMER * 20)) {
            ((Turret.TurretBuild) this.tile.build).handleStack(this.item, Integer.MAX_VALUE, null);
        }
    }

    @Override
    public void generateLabel(Player player) {
        if (buyyed) {
            if (tile.build == null) {
                tile.setNet(block, team, 0);
            }
        } else {
            StringBuilder lab = new StringBuilder();

            lab.append("[accent]cost: ").append(cost);
            lab.append("\n[white]").append(block.name);
            Call.label(player.con, lab.toString(), SEC_TIMER * 10 / 60f, centreDrawx, centreDrawy - Vars.tilesize * (block.size + 1));
        }
    }

    @Override
    public void generate(Tiles tiles) {
        int end = block.size + ((block.size == 2) ? 0 : -1);
        int start = -1 + ((block.size == 1 || block.size == 3 || block.size == 4) ? -1 : 0);
        for (int xx = start; xx <= end; xx++) {
            for (int yy = start; yy <= end; yy++) {
                Floor floor = (Floor) Blocks.metalFloor;

                if (xx == start || yy == start || xx == end || yy == end) {
                    floor = (Floor) Blocks.metalFloor5;
                }
                tiles.getn(xx + x, yy + y).setFloor(floor);
                tiles.getn(xx + x, yy + y).setBlock(Blocks.air);
            }
        }
        tile = tiles.getn(x, y);
    }
}
