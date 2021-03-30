package CastleWars.logic;

import CastleWars.game.Logic;
import arc.util.Reflect;
import rhino.ScriptableObject;
import mindustry.Vars;

/*
I don't know what else to add.
Any method you add in here will
be added inside javascript.
*/
public class CastleJavaScript {
  public final logic;
  
  public CastleJavaScript(Logic logic) {
    this.logic = logic;
    ScriptableObject.putConstProperty(Reflect.get(Vars.mods.getScripts(), "scope"), "CW", this);
  }
  
  public void giveMoney(int amount) {
    logic.datas.each(p -> p.money += amount);
  }
  
  public void giveIncome(int amount) {
    logic.datas.each(p -> p.income += amount);
  }
}
