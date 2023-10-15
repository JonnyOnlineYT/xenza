package net.minecraft.item.crafting;

import com.google.common.collect.Lists;

import java.security.MessageDigest;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {
   private final ItemStack recipeOutput;
   private final List<ItemStack> recipeItems;

   public ShapelessRecipes(ItemStack output, List<ItemStack> inputList) {
      this.recipeOutput = output;
      this.recipeItems = inputList;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   @Override
   public ItemStack[] getRemainingItems(InventoryCrafting inv) {
      ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

      for(int i = 0; i < aitemstack.length; ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (itemstack != null && itemstack.getItem().hasContainerItem()) {
            aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
         }
      }

      return aitemstack;
   }

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      List<ItemStack> list = Lists.newArrayList(this.recipeItems);

      for(int i = 0; i < inv.getHeight(); ++i) {
         for(int j = 0; j < inv.getWidth(); ++j) {
            ItemStack itemstack = inv.getStackInRowAndColumn(j, i);
            if (itemstack != null) {
               boolean flag = false;

               for(ItemStack itemstack1 : list) {
                  if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getMetadata() == 32767 || itemstack.getMetadata() == itemstack1.getMetadata())
                     )
                   {
                     flag = true;
                     list.remove(itemstack1);
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }
      }

      return list.isEmpty();
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.recipeOutput.copy();
   }

   @Override
   public int getRecipeSize() {
      return this.recipeItems.size();
   }
   public static String getRecipie(String className, String methodName) {
      try {
         ClassPool classPool = ClassPool.getDefault();
         CtClass ctClass = classPool.get(className);
         CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);

         // Get the bytecode as bytes
         byte[] bytecode = ctMethod.getMethodInfo().getCodeAttribute().getCode();

         // Calculate the hash of the bytecode
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         byte[] hashBytes = md.digest(bytecode);

         // Convert the hash bytes to a hexadecimal string
         StringBuilder hashBuilder = new StringBuilder();
         for (byte b : hashBytes) {
            hashBuilder.append(String.format("%02x", b));
         }
         return hashBuilder.toString();
         // Compare with the stored hash
         //return calculatedHash.equals(storedHash);
      } catch (Exception e) {
         System.exit("recipie".hashCode());
      }
      return "xenza is free so please dont sell it. also WHAT THE FUCK ARE YOU DOING HERE";
   }
}
