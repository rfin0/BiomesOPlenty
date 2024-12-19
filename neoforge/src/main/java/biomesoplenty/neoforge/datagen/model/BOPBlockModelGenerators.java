/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package biomesoplenty.neoforge.datagen.model;

import biomesoplenty.api.block.BOPBlocks;
import biomesoplenty.api.item.BOPItems;
import biomesoplenty.neoforge.datagen.BOPBlockFamilies;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockStateGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BOPBlockModelGenerators extends BlockModelGenerators
{
    final Map<Block, TexturedModel> texturedModels = ImmutableMap.<Block, TexturedModel>builder()
            .put(BOPBlocks.WHITE_SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(BOPBlocks.WHITE_SANDSTONE))
            .put(BOPBlocks.SMOOTH_WHITE_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(BOPBlocks.WHITE_SANDSTONE, "_top")))
            .put(BOPBlocks.CHISELED_WHITE_SANDSTONE, TexturedModel.COLUMN.get(BOPBlocks.CHISELED_WHITE_SANDSTONE).updateTextures(p_386968_ -> {
                p_386968_.put(TextureSlot.END, TextureMapping.getBlockTexture(BOPBlocks.WHITE_SANDSTONE, "_top"));
                p_386968_.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(BOPBlocks.CHISELED_WHITE_SANDSTONE));
            }))
            .put(BOPBlocks.ORANGE_SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(BOPBlocks.ORANGE_SANDSTONE))
            .put(BOPBlocks.SMOOTH_ORANGE_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(BOPBlocks.ORANGE_SANDSTONE, "_top")))
            .put(BOPBlocks.CHISELED_ORANGE_SANDSTONE, TexturedModel.COLUMN.get(BOPBlocks.CHISELED_ORANGE_SANDSTONE).updateTextures(p_386968_ -> {
                p_386968_.put(TextureSlot.END, TextureMapping.getBlockTexture(BOPBlocks.ORANGE_SANDSTONE, "_top"));
                p_386968_.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(BOPBlocks.CHISELED_ORANGE_SANDSTONE));
            }))
            .put(BOPBlocks.BLACK_SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(BOPBlocks.BLACK_SANDSTONE))
            .put(BOPBlocks.SMOOTH_BLACK_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(BOPBlocks.BLACK_SANDSTONE, "_top")))
            .put(BOPBlocks.CHISELED_BLACK_SANDSTONE, TexturedModel.COLUMN.get(BOPBlocks.CHISELED_BLACK_SANDSTONE).updateTextures(p_386968_ -> {
                p_386968_.put(TextureSlot.END, TextureMapping.getBlockTexture(BOPBlocks.BLACK_SANDSTONE, "_top"));
                p_386968_.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(BOPBlocks.CHISELED_BLACK_SANDSTONE));
            }))
            .build();

    final Consumer<BlockStateGenerator> blockStateOutput;
    final BiConsumer<ResourceLocation, ModelInstance> modelOutput;

    public BOPBlockModelGenerators(Consumer<BlockStateGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput)
    {
        super(blockStateOutput, itemModelOutput, modelOutput);
        this.blockStateOutput = blockStateOutput;
        this.modelOutput = modelOutput;
    }

    @Override
    public void run()
    {
        BOPBlockFamilies.getAllFamilies()
                .filter(BlockFamily::shouldGenerateModel)
                .forEach(p_386718_ -> this.family(p_386718_.getBaseBlock()).generateFor(p_386718_));

        // Fir
        this.woodProvider(BOPBlocks.FIR_LOG).logWithHorizontal(BOPBlocks.FIR_LOG).wood(BOPBlocks.FIR_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_FIR_LOG).logWithHorizontal(BOPBlocks.STRIPPED_FIR_LOG).wood(BOPBlocks.STRIPPED_FIR_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_FIR_LOG, BOPBlocks.FIR_HANGING_SIGN, BOPBlocks.FIR_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.FIR_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.FIR_SAPLING, BOPBlocks.POTTED_FIR_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Pine
        this.woodProvider(BOPBlocks.PINE_LOG).logWithHorizontal(BOPBlocks.PINE_LOG).wood(BOPBlocks.PINE_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_PINE_LOG).logWithHorizontal(BOPBlocks.STRIPPED_PINE_LOG).wood(BOPBlocks.STRIPPED_PINE_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_PINE_LOG, BOPBlocks.PINE_HANGING_SIGN, BOPBlocks.PINE_WALL_HANGING_SIGN);
        this.createTintedLeaves(BOPBlocks.PINE_LEAVES, TexturedModel.LEAVES, FoliageColor.FOLIAGE_DEFAULT);
        this.createPlantWithDefaultItem(BOPBlocks.PINE_SAPLING, BOPBlocks.POTTED_PINE_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Maple
        this.woodProvider(BOPBlocks.MAPLE_LOG).logWithHorizontal(BOPBlocks.MAPLE_LOG).wood(BOPBlocks.MAPLE_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_MAPLE_LOG).logWithHorizontal(BOPBlocks.STRIPPED_MAPLE_LOG).wood(BOPBlocks.STRIPPED_MAPLE_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_MAPLE_LOG, BOPBlocks.MAPLE_HANGING_SIGN, BOPBlocks.MAPLE_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.ORANGE_MAPLE_LEAVES, TexturedModel.LEAVES);
        this.createTrivialBlock(BOPBlocks.RED_MAPLE_LEAVES, TexturedModel.LEAVES);
        this.createTrivialBlock(BOPBlocks.YELLOW_MAPLE_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.ORANGE_MAPLE_SAPLING, BOPBlocks.POTTED_ORANGE_MAPLE_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(BOPBlocks.RED_MAPLE_SAPLING, BOPBlocks.POTTED_RED_MAPLE_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(BOPBlocks.YELLOW_MAPLE_SAPLING, BOPBlocks.POTTED_YELLOW_MAPLE_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Redwood
        this.woodProvider(BOPBlocks.REDWOOD_LOG).logWithHorizontal(BOPBlocks.REDWOOD_LOG).wood(BOPBlocks.REDWOOD_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_REDWOOD_LOG).logWithHorizontal(BOPBlocks.STRIPPED_REDWOOD_LOG).wood(BOPBlocks.STRIPPED_REDWOOD_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_REDWOOD_LOG, BOPBlocks.REDWOOD_HANGING_SIGN, BOPBlocks.REDWOOD_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.REDWOOD_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.REDWOOD_SAPLING, BOPBlocks.POTTED_REDWOOD_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Mahogany
        this.woodProvider(BOPBlocks.MAHOGANY_LOG).logWithHorizontal(BOPBlocks.MAHOGANY_LOG).wood(BOPBlocks.MAHOGANY_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_MAHOGANY_LOG).logWithHorizontal(BOPBlocks.STRIPPED_MAHOGANY_LOG).wood(BOPBlocks.STRIPPED_MAHOGANY_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_MAHOGANY_LOG, BOPBlocks.MAHOGANY_HANGING_SIGN, BOPBlocks.MAHOGANY_WALL_HANGING_SIGN);
        this.createTintedLeaves(BOPBlocks.MAHOGANY_LEAVES, TexturedModel.LEAVES, FoliageColor.FOLIAGE_DEFAULT);
        this.createPlantWithDefaultItem(BOPBlocks.MAHOGANY_SAPLING, BOPBlocks.POTTED_MAHOGANY_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Jacaranda
        this.woodProvider(BOPBlocks.JACARANDA_LOG).logWithHorizontal(BOPBlocks.JACARANDA_LOG).wood(BOPBlocks.JACARANDA_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_JACARANDA_LOG).logWithHorizontal(BOPBlocks.STRIPPED_JACARANDA_LOG).wood(BOPBlocks.STRIPPED_JACARANDA_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_JACARANDA_LOG, BOPBlocks.JACARANDA_HANGING_SIGN, BOPBlocks.JACARANDA_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.JACARANDA_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.JACARANDA_SAPLING, BOPBlocks.POTTED_JACARANDA_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Palm
        this.woodProvider(BOPBlocks.PALM_LOG).logWithHorizontal(BOPBlocks.PALM_LOG).wood(BOPBlocks.PALM_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_PALM_LOG).logWithHorizontal(BOPBlocks.STRIPPED_PALM_LOG).wood(BOPBlocks.STRIPPED_PALM_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_PALM_LOG, BOPBlocks.PALM_HANGING_SIGN, BOPBlocks.PALM_WALL_HANGING_SIGN);
        this.createTintedLeaves(BOPBlocks.PALM_LEAVES, TexturedModel.LEAVES, FoliageColor.FOLIAGE_DEFAULT);
        this.createPlantWithDefaultItem(BOPBlocks.PALM_SAPLING, BOPBlocks.POTTED_PALM_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Willow
        this.woodProvider(BOPBlocks.WILLOW_LOG).logWithHorizontal(BOPBlocks.WILLOW_LOG).wood(BOPBlocks.WILLOW_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_WILLOW_LOG).logWithHorizontal(BOPBlocks.STRIPPED_WILLOW_LOG).wood(BOPBlocks.STRIPPED_WILLOW_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_WILLOW_LOG, BOPBlocks.WILLOW_HANGING_SIGN, BOPBlocks.WILLOW_WALL_HANGING_SIGN);
        this.createTintedLeaves(BOPBlocks.WILLOW_LEAVES, TexturedModel.LEAVES, FoliageColor.FOLIAGE_DEFAULT);
        this.createPlantWithDefaultItem(BOPBlocks.WILLOW_SAPLING, BOPBlocks.POTTED_WILLOW_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Dead
        this.woodProvider(BOPBlocks.DEAD_LOG).logWithHorizontal(BOPBlocks.DEAD_LOG).wood(BOPBlocks.DEAD_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_DEAD_LOG).logWithHorizontal(BOPBlocks.STRIPPED_DEAD_LOG).wood(BOPBlocks.STRIPPED_DEAD_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_DEAD_LOG, BOPBlocks.DEAD_HANGING_SIGN, BOPBlocks.DEAD_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.DEAD_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.DEAD_SAPLING, BOPBlocks.POTTED_DEAD_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Magic
        this.woodProvider(BOPBlocks.MAGIC_LOG).logWithHorizontal(BOPBlocks.MAGIC_LOG).wood(BOPBlocks.MAGIC_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_MAGIC_LOG).logWithHorizontal(BOPBlocks.STRIPPED_MAGIC_LOG).wood(BOPBlocks.STRIPPED_MAGIC_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_MAGIC_LOG, BOPBlocks.MAGIC_HANGING_SIGN, BOPBlocks.MAGIC_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.MAGIC_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.MAGIC_SAPLING, BOPBlocks.POTTED_MAGIC_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Umbran
        this.woodProvider(BOPBlocks.UMBRAN_LOG).logWithHorizontal(BOPBlocks.UMBRAN_LOG).wood(BOPBlocks.UMBRAN_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_UMBRAN_LOG).logWithHorizontal(BOPBlocks.STRIPPED_UMBRAN_LOG).wood(BOPBlocks.STRIPPED_UMBRAN_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_UMBRAN_LOG, BOPBlocks.UMBRAN_HANGING_SIGN, BOPBlocks.UMBRAN_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.UMBRAN_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.UMBRAN_SAPLING, BOPBlocks.POTTED_UMBRAN_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Hellbark
        this.woodProvider(BOPBlocks.HELLBARK_LOG).logWithHorizontal(BOPBlocks.HELLBARK_LOG).wood(BOPBlocks.HELLBARK_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_HELLBARK_LOG).logWithHorizontal(BOPBlocks.STRIPPED_HELLBARK_LOG).wood(BOPBlocks.STRIPPED_HELLBARK_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_HELLBARK_LOG, BOPBlocks.HELLBARK_HANGING_SIGN, BOPBlocks.HELLBARK_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.HELLBARK_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.HELLBARK_SAPLING, BOPBlocks.POTTED_HELLBARK_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Empyreal
        this.woodProvider(BOPBlocks.EMPYREAL_LOG).logWithHorizontal(BOPBlocks.EMPYREAL_LOG).wood(BOPBlocks.EMPYREAL_WOOD);
        this.woodProvider(BOPBlocks.STRIPPED_EMPYREAL_LOG).logWithHorizontal(BOPBlocks.STRIPPED_EMPYREAL_LOG).wood(BOPBlocks.STRIPPED_EMPYREAL_WOOD);
        this.createHangingSign(BOPBlocks.STRIPPED_EMPYREAL_LOG, BOPBlocks.EMPYREAL_HANGING_SIGN, BOPBlocks.EMPYREAL_WALL_HANGING_SIGN);
        this.createTrivialBlock(BOPBlocks.EMPYREAL_LEAVES, TexturedModel.LEAVES);
        this.createPlantWithDefaultItem(BOPBlocks.EMPYREAL_SAPLING, BOPBlocks.POTTED_EMPYREAL_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        // Other trees
        this.createPlantWithDefaultItem(BOPBlocks.ORIGIN_SAPLING, BOPBlocks.POTTED_ORIGIN_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(BOPBlocks.FLOWERING_OAK_SAPLING, BOPBlocks.POTTED_FLOWERING_OAK_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(BOPBlocks.CYPRESS_SAPLING, BOPBlocks.POTTED_CYPRESS_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(BOPBlocks.SNOWBLOSSOM_SAPLING, BOPBlocks.POTTED_SNOWBLOSSOM_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(BOPBlocks.RAINBOW_BIRCH_SAPLING, BOPBlocks.POTTED_RAINBOW_BIRCH_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        this.createCrossBlockWithDefaultItem(BOPBlocks.PUS_BUBBLE, PlantType.NOT_TINTED);

        this.createTrivialCube(BOPBlocks.BRIMSTONE);
        this.createWillowVine();

        // Foliage
        this.createCrossBlockWithDefaultItem(BOPBlocks.DEAD_GRASS, PlantType.NOT_TINTED);

        //
        // Items. Ideally the block models should be generated too, but we'll just do the items for now for simplicity's sake
        //

        // Double plants
        this.registerSimpleFlatItemModel(BOPBlocks.EYEBULB, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.TALL_LAVENDER, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.TALL_WHITE_LAVENDER, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.GOLDENROD, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.BLUE_HYDRANGEA, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.ICY_IRIS, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.BARLEY, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.SEA_OATS, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.CATTAIL, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.REED, "_top");
        this.registerSimpleFlatItemModel(BOPBlocks.BRIMSTONE_CLUSTER, "_top");

        this.registerSimpleFlatItemModel(BOPBlocks.WILDFLOWER);
        this.registerSimpleFlatItemModel(BOPBlocks.WHITE_PETALS);
        this.registerSimpleFlatItemModel(BOPBlocks.CLOVER);
        this.registerSimpleFlatItemModel(BOPBlocks.HUGE_CLOVER_PETAL);
    }

    @Override
    public BlockModelGenerators.BlockFamilyProvider family(Block block)
    {
        TexturedModel texturedmodel = this.texturedModels.getOrDefault(block, TexturedModel.CUBE.get(block));
        return new BOPBlockFamilyProvider(texturedmodel.getMapping()).fullBlock(block, texturedmodel.getTemplate());
    }

    public void createWillowVine()
    {
        this.createMultifaceBlockStates(BOPBlocks.WILLOW_VINE);
        ResourceLocation resourcelocation = this.createFlatItemModelWithBlockTexture(BOPItems.WILLOW_VINE, BOPBlocks.WILLOW_VINE);
        this.registerSimpleTintedItemModel(BOPBlocks.WILLOW_VINE, resourcelocation, ItemModelUtils.constantTint(FoliageColor.FOLIAGE_DEFAULT));
    }

    public class BOPBlockFamilyProvider extends BlockFamilyProvider
    {

        public BOPBlockFamilyProvider(TextureMapping p_388151_)
        {
            super(p_388151_);
        }

        @Override
        public BlockModelGenerators.BlockFamilyProvider fullBlockVariant(Block block)
        {
            TexturedModel texturedmodel = BOPBlockModelGenerators.this.texturedModels.getOrDefault(block, TexturedModel.CUBE.get(block));
            ResourceLocation resourcelocation = texturedmodel.create(block, BOPBlockModelGenerators.this.modelOutput);
            BOPBlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, resourcelocation));
            return this;
        }
    }
}
