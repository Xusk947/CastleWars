package CastleWars.logic.room;

import CastleWars.logic.PlayerData;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Nulls;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;

public class TurretRoom extends Room {

    public final static int SIZE = 5;
    public static ObjectMap<Block, Item> items = new ObjectMap<>();

    public static Seq<TurretRoom> rooms = new Seq<>(new TurretRoom[]{
        new TurretRoom(Vars.world.width() / 6, Vars.world.height() / 4 - 10, Blocks.lancer),
        new TurretRoom(Vars.world.width() / 6, Vars.world.height() / 4, Blocks.ripple),
        new TurretRoom(Vars.world.width() / 6, Vars.world.height() / 4 + 10, Blocks.lancer)
    });

    public Block block;
    public Item item = null;
    public Building build;

    public static void init() {
        items.put(Blocks.ripple, Items.plastanium);
        items.put(Blocks.lancer, null);
        items.put(Blocks.foreshadow, Items.surgeAlloy);
        items.put(Blocks.fuse, Items.thorium);
    }
    
    public TurretRoom(int x, int y, Block turret) {
        super(x, y);
        this.block = turret;
        this.item = items.get(turret);
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

        tiles.getn(x, y).setBlock(block, team);
        build = tiles.getn(x, y).build;
        
        if (item != null) {
            Timer.schedule(() -> {
            }, 3);
        }
    }
}
