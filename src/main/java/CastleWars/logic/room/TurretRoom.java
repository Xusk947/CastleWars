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

public class TurretRoom extends Room {

    public final static int SIZE = 5;
    public static ObjectMap<Block, Item> items = new ObjectMap<>();

    public static Seq<TurretRoom> rooms = new Seq<>(new TurretRoom[]{
        new TurretRoom(8, 7, Blocks.lancer, 500),
        new TurretRoom(8, 0, Blocks.ripple, 1500),
        new TurretRoom(8, -7, Blocks.lancer, 500),
        new TurretRoom(0, -10, Blocks.foreshadow, 4000),
        new TurretRoom(0, 10, Blocks.foreshadow, 4000),});

    public Block block;
    public Item item = null;
    public Tile tile, itemTile;
    public boolean buyyed = false;

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
        
        if (Vars.netServer == null) {
            Timer.schedule(() -> {
                Vars.netServer.admins.addActionFilter(action -> (action.type == Administration.ActionType.breakBlock || action.type == Administration.ActionType.placeBlock) && (action.tile != this.tile && action.tile != this.itemTile));
            }, 5f);
        } else {
            Vars.netServer.admins.addActionFilter(action -> (action.type == Administration.ActionType.breakBlock || action.type == Administration.ActionType.placeBlock) && (action.tile != this.tile && action.tile != this.itemTile));
        }
    }

    public void buy(PlayerData data) {
        buyyed = true;
        data.money -= cost;
    }

    @Override
    public void onTouch(PlayerData data) {
        if (canBuy(data)) {
            buy(data);
            Call.label("[lime]builded by:[white] " + data.player.name, 2, centreDrawx, centreDrawy);
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void generateLabel() {
        if (buyyed) {
            if (tile.build == null) {
                tile.setNet(block, team, 0);
                if (item != null) {
                    itemTile.setNet(Blocks.itemSource, team, 0);
                    Timer.schedule(() -> {
                        itemTile.build.configure(item);
                    }, 1.5f);
                }
            }
        } else {
            StringBuilder lab = new StringBuilder();

            lab.append("[accent]cost: ").append(cost);
            lab.append("\n[white]").append(block.name);

            for (Player player : Groups.player) {
                if (player.team() == team) {
                    Call.label(player.con, lab.toString(), SEC_TIMER * 10 / 60f, centreDrawx, centreDrawy - (block.size + 1) * 8);
                }
            }
        }

    }

    @Override
    public void generate(Tiles tiles) {
        int end = block.size + ((block.size == 2 || block.size == 4) ? 0 : -1);
        int start = -1 + ((block.size == 1 || block.size == 3) ? -1 : 0);
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
        itemTile = tiles.getn(x, y + end);
    }
}
