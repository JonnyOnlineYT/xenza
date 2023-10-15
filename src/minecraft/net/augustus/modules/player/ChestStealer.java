package net.augustus.modules.player;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import net.augustus.events.EventEarlyTick;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.RandomUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class ChestStealer extends Module {
   private final TimeHelper delayTimeHelper = new TimeHelper();
   private final TimeHelper startDelayTimeHelper = new TimeHelper();
   public DoubleValue startDelay = new DoubleValue(1, "StartDelay", this, 200.0, 0.0, 1000.0, 0);
   public DoubleValue delay = new DoubleValue(2, "Delay", this, 100.0, 0.0, 400.0, 0);
   public BooleanValue random = new BooleanValue(3, "Random", this, false);
   public BooleanValue intelligent = new BooleanValue(4, "Intelligent", this, true);
   public BooleanValue autoClose = new BooleanValue(4, "AutoClose", this, true);
   public BooleanValue spamClick = new BooleanValue(5, "SpamClick", this, true);
   public BooleanValue checkName = new BooleanValue(32859, "CheckName", this, false);
   public BooleanValue onWorld = new BooleanValue(123, "DisableOnWorld", this, false);
   @EventTarget
   public void onWorld(EventWorld eventWorld) {
      if(onWorld.getBoolean()) {
         setToggled(false);
      }
   }
   private int lastItemPos = Integer.MIN_VALUE;

   public ChestStealer() {
      super("ChestStealer", new Color(196, 184, 249, 255), Categorys.PLAYER);
   }

   @EventTarget
   public void onEventTick(EventEarlyTick eventEarlyTick) {
      if (mc.currentScreen instanceof GuiChest) {
         if (this.startDelayTimeHelper.reached((long)(this.startDelay.getValue() + (double)RandomUtil.nextLong(-35L, 35L)))) {
            ArrayList<Integer> itemPos = new ArrayList<>();
            GuiChest chest = (GuiChest)mc.currentScreen;

            for(int i = 0; i < chest.inventorySlots.inventorySlots.size() - 36; ++i) {
               ItemStack itemStack = chest.inventorySlots.getSlot(i).getStack();
               if (itemStack != null) {
                  if (this.intelligent.getBoolean()) {
                     if (this.isBestChestItem(itemStack) && this.isBestItem(itemStack)) {
                        itemPos.add(i);
                     }
                  } else {
                     itemPos.add(i);
                  }
               }
            }

            if (this.random.getBoolean()) {
               Collections.shuffle(itemPos);
            }

            if (this.delayTimeHelper.reached((long)(this.delay.getValue() + (double)RandomUtil.nextLong(-35L, 55L)))) {
               boolean b = false;

               for(Integer integer : itemPos) {
                  this.stealItem(integer);
                  this.lastItemPos = integer;
                  b = true;
                  if (this.delay.getValue() != 0.0) {
                     break;
                  }
               }

               if (!b && this.autoClose.getBoolean()) {
                  this.startDelayTimeHelper.reset();
                  mc.thePlayer.closeScreen();
               }
            } else if (this.lastItemPos != Integer.MIN_VALUE && this.spamClick.getBoolean()) {
               mc.playerController.windowClick(chest.inventorySlots.windowId, this.lastItemPos, 0, 1, mc.thePlayer);
               System.out.println("GG");
            }
         }
      } else {
         this.startDelayTimeHelper.reset();
         this.lastItemPos = Integer.MIN_VALUE;
      }
   }

   public boolean isBestChestItem(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ItemSword
         || itemStack.getItem() instanceof ItemBow
         || itemStack.getItem() instanceof ItemArmor
         || itemStack.getItem() instanceof ItemAxe
         || itemStack.getItem() instanceof ItemPickaxe
         || itemStack.getItem() instanceof ItemSpade
         || itemStack.getItem() instanceof ItemFishingRod) {
         ItemStack bestItem = null;
         GuiChest chest = (GuiChest)mc.currentScreen;

         for(int i = 0; i < chest.inventorySlots.inventorySlots.size() - 36; ++i) {
            ItemStack chestItem = chest.inventorySlots.getSlot(i).getStack();
            if (chestItem != null) {
               if (itemStack.getItem() instanceof ItemSword && chestItem.getItem() instanceof ItemSword) {
                  if (this.getDamageSword(itemStack) < this.getDamageSword(chestItem)) {
                     return false;
                  }
               } else if (itemStack.getItem() instanceof ItemBow && chestItem.getItem() instanceof ItemBow) {
                  if (this.getDamageBow(itemStack) < this.getDamageBow(chestItem)) {
                     return false;
                  }
               } else if (itemStack.getItem() instanceof ItemArmor && chestItem.getItem() instanceof ItemArmor) {
                  if (((ItemArmor)itemStack.getItem()).armorType == ((ItemArmor)chestItem.getItem()).armorType
                     && this.getDamageReduceAmount(itemStack) < this.getDamageReduceAmount(chestItem)) {
                     return false;
                  }
               } else if (itemStack.getItem() instanceof ItemFishingRod && chestItem.getItem() instanceof ItemFishingRod) {
                  if (this.getBestRod(itemStack) < this.getBestRod(chestItem)) {
                     return false;
                  }
               } else if (itemStack.getItem() instanceof ItemTool
                  && chestItem.getItem() instanceof ItemTool
                  && this.getToolSpeed(itemStack) < this.getToolSpeed(chestItem)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public boolean isBestItem(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ItemSword
         || itemStack.getItem() instanceof ItemBow
         || itemStack.getItem() instanceof ItemArmor
         || itemStack.getItem() instanceof ItemAxe
         || itemStack.getItem() instanceof ItemPickaxe
         || itemStack.getItem() instanceof ItemSpade
         || itemStack.getItem() instanceof ItemFishingRod) {
         for(int i = 0; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i) {
            ItemStack inventoryStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (inventoryStack != null) {
               if (itemStack.getItem() instanceof ItemSword && inventoryStack.getItem() instanceof ItemSword) {
                  if (this.getDamageSword(itemStack) <= this.getDamageSword(inventoryStack)) {
                     return false;
                  }
               } else if (itemStack.getItem() instanceof ItemBow && inventoryStack.getItem() instanceof ItemBow) {
                  if (this.getDamageBow(itemStack) <= this.getDamageBow(inventoryStack)) {
                     return false;
                  }
               } else if (itemStack.getItem() instanceof ItemArmor && inventoryStack.getItem() instanceof ItemArmor) {
                  if (((ItemArmor)itemStack.getItem()).armorType == ((ItemArmor)inventoryStack.getItem()).armorType
                     && this.getDamageReduceAmount(itemStack) <= this.getDamageReduceAmount(inventoryStack)) {
                     return false;
                  }
               } else if (itemStack.getItem() instanceof ItemFishingRod && inventoryStack.getItem() instanceof ItemFishingRod) {
                  if (this.getBestRod(itemStack) <= this.getBestRod(inventoryStack)) {
                     return false;
                  }
               } else if (itemStack.getItem() instanceof ItemTool
                  && inventoryStack.getItem() instanceof ItemTool
                  && this.getToolSpeed(itemStack) <= this.getToolSpeed(inventoryStack)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void stealItem(int slot) {
      GuiChest chest = (GuiChest)mc.currentScreen;
      Slot slot1 = chest.inventorySlots.getSlot(slot);

      try {
         chest.forceShift = true;
         chest.mouseClicked(slot1.xDisplayPosition + 2 + chest.guiLeft, slot1.yDisplayPosition + 2 + chest.guiTop, 0);
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      this.delayTimeHelper.reset();
   }

   private double getDamageSword(ItemStack itemStack) {
      double damage = 0.0;
      if (itemStack.getItem() instanceof ItemSword) {
         damage += (double)(
            ((ItemSword)itemStack.getItem()).getAttackDamage()
               + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F
         );
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
         damage -= (double)itemStack.getItemDamage() / 10000.0;
      }

      return damage;
   }

   private double getDamageBow(ItemStack itemStack) {
      double damage = 0.0;
      if (itemStack.getItem() instanceof ItemBow) {
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) / 8.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack) / 8.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
         damage -= (double)itemStack.getItemDamage() / 10000.0;
      }

      return damage;
   }

   private double getToolSpeed(ItemStack itemStack) {
      double damage = 0.0;
      if (itemStack.getItem() instanceof ItemTool) {
         if (itemStack.getItem() instanceof ItemAxe) {
            damage += (double)(
               itemStack.getItem().getStrVsBlock(itemStack, new Block(Material.wood, MapColor.woodColor))
                  + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)
            );
         } else if (itemStack.getItem() instanceof ItemPickaxe) {
            damage += (double)(
               itemStack.getItem().getStrVsBlock(itemStack, new Block(Material.rock, MapColor.stoneColor))
                  + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)
            );
         } else if (itemStack.getItem() instanceof ItemSpade) {
            damage += (double)(
               itemStack.getItem().getStrVsBlock(itemStack, new Block(Material.sand, MapColor.sandColor))
                  + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)
            );
         }

         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) / 33.0;
         damage -= (double)itemStack.getItemDamage() / 10000.0;
      }

      return damage;
   }

   private double getDamageReduceAmount(ItemStack itemStack) {
      double damageReduceAmount = 0.0;
      if (itemStack.getItem() instanceof ItemArmor) {
         damageReduceAmount += (double)(
            (float)((ItemArmor)itemStack.getItem()).damageReduceAmount
               + (float)(
                     6
                        + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack)
                           * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack)
                  )
                  / 3.0F
         );
         damageReduceAmount += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, itemStack) / 11.0;
         damageReduceAmount += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, itemStack) / 11.0;
         damageReduceAmount += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, itemStack) / 11.0;
         damageReduceAmount += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
         damageReduceAmount += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, itemStack) / 11.0;
         damageReduceAmount += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack) / 11.0;
         if (((ItemArmor)itemStack.getItem()).armorType == 0 && ((ItemArmor)itemStack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.GOLD) {
            damageReduceAmount -= 0.01;
         }

         damageReduceAmount -= (double)itemStack.getItemDamage() / 10000.0;
      }

      return damageReduceAmount;
   }

   private double getBestRod(ItemStack itemStack) {
      double damage = 0.0;
      if (itemStack.getItem() instanceof ItemFishingRod) {
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.lure.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, itemStack) / 33.0;
         damage -= (double)itemStack.getItemDamage() / 10000.0;
      }

      return damage;
   }
}
