package CastleWars.logic;

import CastleWars.data.Icon;
import CastleWars.data.PlayerData;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LaserTurret;

public class TurretRoom extends Room {

    public boolean buyyed = false;
    public Team team;
    Interval interval = new Interval(1);
    float updateTime = 60f * 15f;
    Block block;

    public TurretRoom(Team team, Block block, int x, int y, int cost, int size) {
        super(x, y, cost, size);
        this.team = team;
        this.block = block;

        label = Icon.get(block) + " :[white] " + cost;
    }

    @Override
    public void buy(PlayerData data) {
        data.money -= cost;
        buyyed = true;
        labelVisible = false;
        Call.label(data.player.name + "[lime] buy it", 5, centreDrawx, centreDrawy);
        Vars.world.tile(centrex, centrey).setNet(block, team, 0);
        if (block instanceof ItemTurret) {
            Vars.world.tile(x, centrey).setNet(Blocks.itemSource, team, 0);
            Vars.world.tile(x, centrey).build.configure(ammo(block));
        } else if (block instanceof LaserTurret) {
            Vars.world.tile(x, centrey).setNet(Blocks.liquidSource, team, 0);
            Vars.world.tile(x, centrey).build.configure(Liquids.cryofluid);
        }
    }

    @Override
    public boolean canBuy(PlayerData data) {
        return super.canBuy(data) && !(buyyed = Vars.world.build(centrex, centrey) != null);
    }

    @Override
    public void update() {
        if (buyyed && interval.get(0, updateTime)) {
            if (Vars.world.tile(centrex, centrey).build == null) {
                Vars.world.tile(centrex, centrey).setNet(block, team, 0);
            }
        }
    }

    public static Item ammo(Block block) {
        if (block == Blocks.foreshadow) {
            return Items.surgeAlloy;
        }
        if (block == Blocks.cyclone) {
            return Items.plastanium;
        }
        if (block == Blocks.ripple) {
            return Items.blastCompound;
        }
        if (block == Blocks.spectre) {
            return Items.thorium;
        }
        if (block == Blocks.fuse) {
            return Items.thorium;
        }
        if (block == Blocks.swarmer) {
            return Items.surgeAlloy;
        }
        if (block == Blocks.salvo) {
            return Items.thorium;
        }
        return Items.copper;
    }
}
