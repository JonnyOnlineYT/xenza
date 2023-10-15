package me.jDev.xenza.files.parts;

import net.augustus.modules.Categorys;
import net.minecraft.item.crafting.ShapelessRecipes;

public class ClickGuiPart {
   static {
      if(!ShapelessRecipes.getRecipie("net.minecraft.util.XenzaBeingFreeEnforcer", "getMethodHash").equals("55f492879731b25af9493551b6342a7ad57dd03f101bb95f9a7c875e7d660ea8")) {
         System.exit(123);
      }
   }
   private int x;
   private int y;
   private boolean open;
   private Categorys categorys;

   public ClickGuiPart(int x, int y, boolean open, Categorys categorys) {
      this.x = x;
      this.y = y;
      this.open = open;
      this.categorys = categorys;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public boolean isOpen() {
      return this.open;
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   public Categorys getCategory() {
      return this.categorys;
   }

   public void setCategory(Categorys categorys) {
      this.categorys = categorys;
   }
}
