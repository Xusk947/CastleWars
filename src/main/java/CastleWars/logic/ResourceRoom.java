package CastleWars.logic;

import CastleWars.data.Icon;
import CastleWars.data.PlayerData;
import mindustry.gen.Call;
import mindustry.gen.Nulls;
import mindustry.type.Item;
import mindustry.content.Items;

public class ResourceRoom extends Room {

    Item item;

    public ResourceRoom(Item item, int x, int y, int cost) {
        super(x, y, cost, 4);
        this.item = item;
        label = "[white]96x" + Icon.get(item) + " [white]: [gray]" + cost;
    }

    @Override
    public void buy(PlayerData data) {
        data.money -= cost;
        if (data.player.team().core() != null) {
            Call.transferItemTo(Nulls.unit, item, 96, centreDrawx, centreDrawy, data.player.team().core());
            if (item == Items.plastanium) {
                Call.transferItemTo(Nulls.unit, Items.metaglass, 28, centreDrawx, centreDrawy, data.player.team().core());
            }
        }
    }

    @Override
    public void update() {
    }

}
