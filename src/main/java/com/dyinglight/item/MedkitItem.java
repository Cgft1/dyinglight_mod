package com.dyinglight.item;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
public class MedkitItem extends Item {
    public MedkitItem(Settings settings) { super(settings.maxDamage(0)); }
    @Override public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user.getHealth() < user.getMaxHealth()) {
            user.heal(8.0f);
            if (!user.getAbilities().creativeMode) stack.decrement(1);
            user.getItemCooldownManager().set(this, 80);
        }
        return TypedActionResult.success(stack, world.isClient);
    }
    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.DRINK; }
}
