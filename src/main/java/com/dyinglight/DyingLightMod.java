package com.dyinglight;
import com.dyinglight.entity.InfectedEntity;
import com.dyinglight.item.MedkitItem;
import com.dyinglight.item.PipeWeaponItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.block.Blocks;

public class DyingLightMod implements ModInitializer {
    public static final String MOD_ID = "dyinglight";
    public static final Item SCRAP = registerItem("scrap", new Item(new Item.Settings()));
    public static final Item MEDKIT = registerItem("medkit", new MedkitItem(new Item.Settings().maxCount(8)));
    public static final Item PIPE = registerItem("pipe", new PipeWeaponItem(ToolMaterials.IRON, 4, -2.7f, new Item.Settings().maxCount(1)));
    public static final Item BAT = registerItem("reinforced_bat", new PipeWeaponItem(ToolMaterials.IRON, 5, -2.6f, new Item.Settings().maxCount(1)));
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
    }
    @Override public void onInitialize() {
        FabricDefaultAttributeRegistry.register(InfectedEntity.TYPE, InfectedEntity.createInfectedAttributes());
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(PIPE); entries.add(BAT); entries.add(MEDKIT); entries.add(SCRAP);
        });
        BiomeModifications.addSpawn(context -> true, SpawnGroup.MONSTER, InfectedEntity.TYPE, 35, 1, 4);
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof InfectedEntity && entity.getWorld() instanceof ServerWorld world && world.random.nextFloat() < 0.55f) {
                world.spawnEntity(new net.minecraft.entity.ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(SCRAP, 1 + world.random.nextInt(2))));
            }
        });
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            boolean night = world.getTimeOfDay() % 24000L >= 13000L && world.getTimeOfDay() % 24000L <= 23000L;
            for (InfectedEntity infected : world.getEntitiesByType(InfectedEntity.TYPE, e -> true)) {
                EntityAttributeInstance speed = infected.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (speed != null) speed.setBaseValue(night ? 0.32 : 0.27);
                EntityAttributeInstance damage = infected.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                if (damage != null) damage.setBaseValue(night ? 6.0 : 4.0);
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(CommandManager.literal("dlcity").requires(source -> source.hasPermissionLevel(2)).executes(context -> {
                var source = context.getSource();
                ServerWorld world = source.getWorld();
                BlockPos center = BlockPos.ofFloored(source.getPosition());
                Random random = world.getRandom();
                int buildings = 0;
                for (int gx = -3; gx <= 3; gx++) for (int gz = -3; gz <= 3; gz++) {
                    if (Math.abs(gx) < 2 && Math.abs(gz) < 2) continue;
                    int x = center.getX() + gx * 18;
                    int z = center.getZ() + gz * 18;
                    int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
                    int w = 7 + random.nextInt(5), d = 7 + random.nextInt(5), h = 8 + random.nextInt(13);
                    buildBuilding(world, new BlockPos(x, y, z), w, d, h, random);
                    buildings++;
                }
                final int count = buildings;
                source.sendFeedback(() -> Text.literal("Harran district generated: " + count + " buildings. Explore the rooftops!"), true);
                return count;
            })));
    }
    private static void buildBuilding(ServerWorld world, BlockPos p, int w, int d, int h, Random random) {
        var wall = random.nextBoolean() ? Blocks.GRAY_CONCRETE : Blocks.LIGHT_GRAY_CONCRETE;
        var accent = random.nextBoolean() ? Blocks.CRACKED_STONE_BRICKS : Blocks.MOSSY_STONE_BRICKS;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) for (int z = 0; z < d; z++) {
            boolean edge = x == 0 || z == 0 || x == w - 1 || z == d - 1;
            BlockPos at = p.add(x, y, z);
            if (y == 0 || y == h - 1) world.setBlockState(at, y == h - 1 ? Blocks.STONE_BRICK_SLAB.getDefaultState() : Blocks.GRAY_CONCRETE.getDefaultState(), 3);
            else if (edge) {
                boolean window = y > 1 && y < h - 1 && y % 3 != 0 && (x % 3 == 1 || z % 3 == 1);
                world.setBlockState(at, window ? Blocks.BLACK_STAINED_GLASS.getDefaultState() : (y % 4 == 0 ? accent.getDefaultState() : wall.getDefaultState()), 3);
            } else if (y == 1) world.setBlockState(at, Blocks.POLISHED_ANDESITE.getDefaultState(), 3);
        }
        for (int y = 1; y < 3; y++) world.setBlockState(p.add(w / 2, y, 0), Blocks.AIR.getDefaultState(), 3);
        for (int y = 1; y < h - 1; y++) world.setBlockState(p.add(w - 2, y, d - 2),
            Blocks.LADDER.getDefaultState().with(net.minecraft.state.property.Properties.HORIZONTAL_FACING, net.minecraft.util.math.Direction.NORTH), 3);
        world.setBlockState(p.add(w / 2, h, d / 2), Blocks.CHEST.getDefaultState(), 3);
    }
}
