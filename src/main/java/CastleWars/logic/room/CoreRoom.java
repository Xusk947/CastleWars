package CastleWars.logic.room;

import static CastleWars.game.Logic.SEC_TIMER;
import CastleWars.logic.PlayerData;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.Tiles;

public class CoreRoom extends Room {

    boolean buyyed = false;

    public static Seq<CoreRoom> rooms = new Seq<>(new CoreRoom[]{
        new CoreRoom(0, 0),
    });

    public CoreRoom(int x, int y) {
        super(x, y);
        cost = 10000;
    }

    @Override
    public void onTouch(PlayerData data) {
        if (!buyyed && canBuy(data)) {
            if (data.player.team().core() != null) {
                data.money -= this.cost;
                
                Tile tile = data.player.team().core().tile;
                tile.setNet(Blocks.coreNucleus, data.player.team(), 0);
                buyyed = true;
                Call.label("[lime]builded by:[white] " + data.player.name, 2, centreDrawx, centreDrawy);
                for (ItemStack itemStack : Vars.state.rules.loadout) {
                    Call.transferItemTo(data.player.unit(), itemStack.item, itemStack.amount, data.player.x, data.player.y, tile.build);
                }
            }
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void generate(Tiles tiles) {
    }

    @Override
    public void generateLabel(Player player) {
        if (!buyyed) {
            StringBuilder lab = new StringBuilder();

            lab.append("[accent]cost: ").append(cost);
            lab.append("\n[white]").append(Blocks.coreNucleus.name);
            Call.label(player.con, lab.toString(), SEC_TIMER * 10 / 60f, centreDrawx, centreDrawy - Vars.tilesize * (Blocks.coreShard.size + 1));
        }
    }

}
