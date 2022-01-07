/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package biomesoplenty.common.biome;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.common.worldgen.BOPBiomeProvider;
import terrablender.api.BiomeProviders;
import biomesoplenty.common.util.biome.BiomeUtil;
import terrablender.worldgen.BiomeProviderUtils;
import terrablender.worldgen.TBClimate;
import com.mojang.datafixers.util.Pair;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

public final class BOPOverworldBiomeBuilder
{
    private static final float VALLEY_SIZE = 0.05F;
    private static final float LOW_START = 0.26666668F;
    public static final float HIGH_START = 0.4F;
    private static final float HIGH_END = 0.93333334F;
    private static final float PEAK_SIZE = 0.1F;
    public static final float PEAK_START = 0.56666666F;
    private static final float PEAK_END = 0.7666667F;
    public static final float NEAR_INLAND_START = -0.11F;
    public static final float MID_INLAND_START = 0.03F;
    public static final float FAR_INLAND_START = 0.3F;
    public static final float EROSION_INDEX_1_START = -0.78F;
    public static final float EROSION_INDEX_2_START = -0.375F;

    private final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
    private final Climate.Parameter DEFAULT_DEPTH_RANGE = Climate.Parameter.span(0.2F, 0.9F);

    private final Climate.Parameter vanillaUniqueness = BiomeProviderUtils.getUniquenessParameter(BiomeProviders.getIndex(BiomeProviders.DEFAULT_PROVIDER_LOCATION));
    private final Climate.Parameter bopUniqueness = BiomeProviderUtils.getUniquenessParameter(BiomeProviders.getIndex(BOPBiomeProvider.LOCATION));
    private final Climate.Parameter rareUniqueness = BiomeProviderUtils.getUniquenessParameter(BiomeProviders.getIndex(BOPBiomeProvider.RARE_LOCATION));

    /* Terminology:
        Continentalness: Low to generate near coasts, far to generate away from coasts
        Erosion: Low is hilly terrain, high is flat terrain
     */

    private final Climate.Parameter[] temperatures = new Climate.Parameter[]{
            Climate.Parameter.span(-1.0F, -0.45F),
            Climate.Parameter.span(-0.45F, -0.15F),
            Climate.Parameter.span(-0.15F, 0.2F),
            Climate.Parameter.span(0.2F, 0.55F),
            Climate.Parameter.span(0.55F, 1.0F)
    };

    private final Climate.Parameter[] humidities = new Climate.Parameter[]{
            Climate.Parameter.span(-1.0F, -0.35F),
            Climate.Parameter.span(-0.35F, -0.1F),
            Climate.Parameter.span(-0.1F, 0.1F),
            Climate.Parameter.span(0.1F, 0.3F),
            Climate.Parameter.span(0.3F, 1.0F)
    };

    private final Climate.Parameter[] erosions = new Climate.Parameter[]{
            Climate.Parameter.span(-1.0F, -0.78F),
            Climate.Parameter.span(-0.78F, -0.375F),
            Climate.Parameter.span(-0.375F, -0.2225F),
            Climate.Parameter.span(-0.2225F, 0.05F),
            Climate.Parameter.span(0.05F, 0.45F),
            Climate.Parameter.span(0.45F, 0.55F),
            Climate.Parameter.span(0.55F, 1.0F)
    };

    private static final Climate.Parameter COMMON_RARENESS_RANGE = Climate.Parameter.span(-1.0F, 0.35F);
    private static final Climate.Parameter RARE_RARENESS_RANGE = Climate.Parameter.span(0.35F, 1.0F);

    private final Climate.Parameter FROZEN_RANGE = this.temperatures[0];
    private final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(this.temperatures[1], this.temperatures[4]);
    private final Climate.Parameter mushroomFieldsContinentalness = Climate.Parameter.span(-1.2F, -1.05F);
    private final Climate.Parameter deepOceanContinentalness = Climate.Parameter.span(-1.05F, -0.455F);
    private final Climate.Parameter oceanContinentalness = Climate.Parameter.span(-0.455F, -0.19F);
    private final Climate.Parameter coastContinentalness = Climate.Parameter.span(-0.19F, -0.11F);
    private final Climate.Parameter inlandContinentalness = Climate.Parameter.span(-0.11F, 0.55F);
    private final Climate.Parameter nearInlandContinentalness = Climate.Parameter.span(-0.11F, 0.03F);
    private final Climate.Parameter midInlandContinentalness = Climate.Parameter.span(0.03F, 0.3F);
    private final Climate.Parameter farInlandContinentalness = Climate.Parameter.span(0.3F, 1.0F);

    private final ResourceKey<Biome>[][] OCEANS = new ResourceKey[][]{
            {Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN},
            {Biomes.FROZEN_OCEAN,      Biomes.COLD_OCEAN,      Biomes.OCEAN,      Biomes.LUKEWARM_OCEAN,      Biomes.WARM_OCEAN}
    };

    private final ResourceKey<Biome>[][] ISLAND_BIOMES_BOP = new ResourceKey[][]{
            {BOPBiomes.RAINBOW_HILLS,         BOPBiomes.RAINBOW_HILLS,         BOPBiomes.RAINBOW_HILLS, BOPBiomes.RAINBOW_HILLS, BOPBiomes.RAINBOW_HILLS},
            {null,                            null,                            null,                    null,                    null},
            {BOPBiomes.ORIGIN_VALLEY,         BOPBiomes.ORIGIN_VALLEY,         BOPBiomes.ORIGIN_VALLEY, BOPBiomes.ORIGIN_VALLEY, BOPBiomes.ORIGIN_VALLEY},
            {BOPBiomes.TROPICS,               BOPBiomes.TROPICS,               BOPBiomes.TROPICS,       BOPBiomes.TROPICS,       BOPBiomes.TROPICS},
            {BOPBiomes.TROPICS,               BOPBiomes.TROPICS,               BOPBiomes.TROPICS,       BOPBiomes.TROPICS,       BOPBiomes.TROPICS}
    };

    private final ResourceKey<Biome>[][] MIDDLE_BIOMES = new ResourceKey[][]{
            {Biomes.SNOWY_PLAINS,  Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA,  Biomes.TAIGA},
            {Biomes.PLAINS,        Biomes.PLAINS,       Biomes.FOREST,       Biomes.TAIGA,        Biomes.OLD_GROWTH_SPRUCE_TAIGA},
            {Biomes.FLOWER_FOREST, Biomes.PLAINS,       Biomes.FOREST,       Biomes.BIRCH_FOREST, Biomes.DARK_FOREST},
            {Biomes.SAVANNA,       Biomes.SAVANNA,      Biomes.FOREST,       Biomes.JUNGLE,       Biomes.JUNGLE},
            {Biomes.DESERT,        Biomes.DESERT,       Biomes.DESERT,       Biomes.DESERT,       Biomes.DESERT}
    };

    private final ResourceKey<Biome>[][] MIDDLE_BIOMES_VARIANT = new ResourceKey[][]{
            {Biomes.ICE_SPIKES,       null, Biomes.SNOWY_TAIGA, null,                           null},
            {null,                    null, null,               null,                           Biomes.OLD_GROWTH_PINE_TAIGA},
            {Biomes.SUNFLOWER_PLAINS, null, null,               Biomes.OLD_GROWTH_BIRCH_FOREST, null},
            {null,                    null, Biomes.PLAINS,      Biomes.SPARSE_JUNGLE,           Biomes.BAMBOO_JUNGLE},
            {null,                    null, null,               null,                           null}
    };

    private final ResourceKey<Biome>[][] MIDDLE_BIOMES_BOP = new ResourceKey[][]{
            {BOPBiomes.COLD_DESERT,     BOPBiomes.TUNDRA,               BOPBiomes.SNOWY_CONIFEROUS_FOREST, BOPBiomes.MAPLE_WOODS, BOPBiomes.DEAD_FOREST},
            {BOPBiomes.SEASONAL_FOREST, BOPBiomes.CONIFEROUS_FOREST,    BOPBiomes.CONIFEROUS_FOREST,       BOPBiomes.FIELD,       BOPBiomes.FIELD},
            {BOPBiomes.PRAIRIE,         BOPBiomes.SHRUBLAND,            BOPBiomes.SHRUBLAND,               BOPBiomes.GRASSLAND,   BOPBiomes.GRASSLAND},
            {BOPBiomes.SCRUBLAND,       BOPBiomes.MEDITERRANEAN_FOREST, BOPBiomes.WOODLAND,                BOPBiomes.RAINFOREST,  BOPBiomes.RAINFOREST},
            {BOPBiomes.WASTELAND,       BOPBiomes.WASTELAND,            BOPBiomes.DRYLAND,                 BOPBiomes.LUSH_DESERT, BOPBiomes.VOLCANIC_PLAINS}
    };

    private final ResourceKey<Biome>[][] MIDDLE_BIOMES_VARIANT_BOP = new ResourceKey[][]{
            {null,                    null,                         BOPBiomes.SNOWY_FIR_CLEARING,   BOPBiomes.SNOWY_MAPLE_WOODS,  BOPBiomes.OLD_GROWTH_DEAD_FOREST},
            {BOPBiomes.PUMPKIN_PATCH, BOPBiomes.FIR_CLEARING,       null,                           null,                         BOPBiomes.FORESTED_FIELD},
            {BOPBiomes.PASTURE,       BOPBiomes.LAVENDER_FIELD,     BOPBiomes.REDWOOD_FOREST,       BOPBiomes.CLOVER_PATCH,       BOPBiomes.CHERRY_BLOSSOM_GROVE},
            {null,                    null,                         BOPBiomes.OLD_GROWTH_WOODLAND,  null,                         null},
            {null,                    BOPBiomes.WOODED_WASTELAND,   null,                           BOPBiomes.LUSH_SAVANNA,       null}
    };

    private final ResourceKey<Biome>[][] SWAMP_BIOMES_BOP = new ResourceKey[][]{
            // NOTE: Frozen biomes not applicable for swamp biomes
            {null,            null,                          null,              null,                 null},
            {BOPBiomes.BOG,   BOPBiomes.BOG,                 BOPBiomes.WETLAND, BOPBiomes.WETLAND,    BOPBiomes.WETLAND},
            {BOPBiomes.MARSH, BOPBiomes.MARSH,               BOPBiomes.MARSH,   BOPBiomes.MARSH,      BOPBiomes.MARSH},
            {BOPBiomes.BAYOU, BOPBiomes.MEDITERRANEAN_LAKES, BOPBiomes.BAYOU,   BOPBiomes.FLOODPLAIN, BOPBiomes.FLOODPLAIN},
            {null,            null,                          null,              null,                 null}
    };

    private final ResourceKey<Biome>[][] RARE_BIOMES_BOP = new ResourceKey[][]{
            {null, null, null,                    BOPBiomes.MUSKEG,        null},
            {null, null, BOPBiomes.OMINOUS_WOODS, null,                    null},
            {null, null, null,                    BOPBiomes.MYSTIC_GROVE,  null},
            {null, null, null,                    null,                    BOPBiomes.FUNGAL_JUNGLE},
            {null, null, null,                    null,                    null}
    };

    private final ResourceKey<Biome>[][] PLATEAU_BIOMES = new ResourceKey[][]{
            {Biomes.SNOWY_PLAINS,    Biomes.SNOWY_PLAINS,    Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA,     Biomes.SNOWY_TAIGA},
            {Biomes.MEADOW,          Biomes.MEADOW,          Biomes.FOREST,       Biomes.TAIGA,           Biomes.OLD_GROWTH_SPRUCE_TAIGA},
            {Biomes.MEADOW,          Biomes.MEADOW,          Biomes.MEADOW,       Biomes.MEADOW,          Biomes.DARK_FOREST},
            {Biomes.SAVANNA_PLATEAU, Biomes.SAVANNA_PLATEAU, Biomes.FOREST,       Biomes.FOREST,          Biomes.JUNGLE},
            {Biomes.BADLANDS,        Biomes.BADLANDS,        Biomes.BADLANDS,     Biomes.WOODED_BADLANDS, Biomes.WOODED_BADLANDS}
    };

    private final ResourceKey<Biome>[][] PLATEAU_BIOMES_VARIANT = new ResourceKey[][]{
            {Biomes.ICE_SPIKES,      null,                   null,          null,                null},
            {null,                   null,                   Biomes.MEADOW, Biomes.MEADOW,       Biomes.OLD_GROWTH_PINE_TAIGA},
            {null,                   null,                   Biomes.FOREST, Biomes.BIRCH_FOREST, null},
            {null,                   null,                   null,          null,                null},
            {Biomes.ERODED_BADLANDS, Biomes.ERODED_BADLANDS, null,          null,                null}
    };

    private final ResourceKey<Biome>[][] PLATEAU_BIOMES_BOP = new ResourceKey[][]{
            {BOPBiomes.COLD_DESERT,      BOPBiomes.TUNDRA,               BOPBiomes.SNOWY_FIR_CLEARING, BOPBiomes.MAPLE_WOODS, BOPBiomes.DEAD_FOREST},
            {BOPBiomes.SEASONAL_FOREST,  BOPBiomes.CONIFEROUS_FOREST,    BOPBiomes.HIGHLAND,           BOPBiomes.HIGHLAND,    BOPBiomes.FIELD},
            {BOPBiomes.ORCHARD,          BOPBiomes.ROCKY_SHRUBLAND,      BOPBiomes.ROCKY_SHRUBLAND,    BOPBiomes.HIGHLAND,    BOPBiomes.HIGHLAND},
            {BOPBiomes.WOODED_SCRUBLAND, BOPBiomes.MEDITERRANEAN_FOREST, BOPBiomes.WOODLAND,           BOPBiomes.RAINFOREST,  BOPBiomes.RAINFOREST_CLIFFS},
            {BOPBiomes.WASTELAND,        BOPBiomes.WASTELAND,            BOPBiomes.DRYLAND,            BOPBiomes.LUSH_DESERT, BOPBiomes.VOLCANO}
    };

    private final ResourceKey<Biome>[][] PLATEAU_BIOMES_VARIANT_BOP = new ResourceKey[][]{
            {null,                    null,                      null, BOPBiomes.SNOWY_MAPLE_WOODS,  null},
            {BOPBiomes.BOREAL_FOREST, null,                      null, null,                         null},
            {null,                    BOPBiomes.LAVENDER_FOREST, null, BOPBiomes.HIGHLAND_MOOR,      BOPBiomes.BAMBOO_GROVE},
            {null,                    null,                      null, null,                         null},
            {null,                    null,                      null, BOPBiomes.LUSH_SAVANNA,       BOPBiomes.VOLCANIC_PLAINS}
    };

    private final ResourceKey<Biome>[][] EXTREME_HILLS = new ResourceKey[][]{
            {Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
            {Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
            {Biomes.WINDSWEPT_HILLS,          Biomes.WINDSWEPT_HILLS,          Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
            {null,                            null,                            null,                   null,                    null},
            {null,                            null,                            null,                   null,                    null}
    };

    private final ResourceKey<Biome>[][] EXTREME_HILLS_BOP = new ResourceKey[][]{
            {null,                            null,                            null,                   null,           null},
            {null,                            null,                            null,                   null,           null},
            {null,                            null,                            null,                   null,           null},
            {null,                            null,                            null,                   null,           BOPBiomes.RAINFOREST_CLIFFS},
            {null,                            null,                            null,                   null,           BOPBiomes.VOLCANO}
    };

    public List<TBClimate.ParameterPoint> spawnTarget()
    {
        Climate.Parameter climate$parameter = Climate.Parameter.point(0.0F);
        float f = 0.16F;
        return List.of(new TBClimate.ParameterPoint(this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.inlandContinentalness, this.FULL_RANGE), this.FULL_RANGE, climate$parameter, Climate.Parameter.span(-1.0F, -0.16F), this.FULL_RANGE, 0L), new TBClimate.ParameterPoint(this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.inlandContinentalness, this.FULL_RANGE), this.FULL_RANGE, climate$parameter, Climate.Parameter.span(0.16F, 1.0F), this.FULL_RANGE, 0L));
    }

    public void addBiomes(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        if (SharedConstants.debugGenerateSquareTerrainWithoutNoise)
        {
            // TODO:
            // TerrainProvider.overworld(false).addDebugBiomesToVisualizeSplinePoints(mapper);
        }
        else
        {
            this.addOffCoastBiomes(biomeRegistry, mapper);
            this.addInlandBiomes(biomeRegistry, mapper);
            this.addUndergroundBiomes(biomeRegistry, mapper);
        }
    }

    private void addOffCoastBiomes(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        for (int i = 0; i < this.temperatures.length; ++i)
        {
            Climate.Parameter temperature = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j)
            {
                Climate.Parameter humidity = this.humidities[j];
                ResourceKey<Biome> islandBiomeBOP = this.pickIslandBiomeBOP(biomeRegistry, i, j);

                this.addSurfaceBiomeGlobal(mapper, temperature, humidity, this.mushroomFieldsContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, islandBiomeBOP);
            }

            this.addSurfaceBiomeGlobal(mapper, temperature, this.FULL_RANGE, this.deepOceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, this.OCEANS[0][i]);
            this.addSurfaceBiomeGlobal(mapper, temperature, this.FULL_RANGE, this.oceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, this.OCEANS[1][i]);
        }
    }

    private void addInlandBiomes(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        /*
            Weirdness ranges map to specific slices in a repeating triangle wave fashion.
                   PEAKS                           PEAKS
               HIGH     HIGH                   HIGH     HIGH
            MID             MID             MID             MID
                               LOW       LOW
                                  VALLEYS
         */

        // First cycle
        this.addMidSlice(biomeRegistry, mapper, Climate.Parameter.span(-1.0F, -0.93333334F));
        this.addHighSlice(biomeRegistry, mapper, Climate.Parameter.span(-0.93333334F, -0.7666667F));
        this.addPeaks(biomeRegistry, mapper, Climate.Parameter.span(-0.7666667F, -0.56666666F));
        this.addHighSlice(biomeRegistry, mapper, Climate.Parameter.span(-0.56666666F, -0.4F));
        this.addMidSlice(biomeRegistry, mapper, Climate.Parameter.span(-0.4F, -0.26666668F));
        this.addLowSlice(biomeRegistry, mapper, Climate.Parameter.span(-0.26666668F, -0.05F));
        this.addValleys(biomeRegistry, mapper, Climate.Parameter.span(-0.05F, 0.05F));
        this.addLowSlice(biomeRegistry, mapper, Climate.Parameter.span(0.05F, 0.26666668F));
        this.addMidSlice(biomeRegistry, mapper, Climate.Parameter.span(0.26666668F, 0.4F));

        // Second cycle is truncated
        this.addHighSlice(biomeRegistry, mapper, Climate.Parameter.span(0.4F, 0.56666666F));
        this.addPeaks(biomeRegistry, mapper, Climate.Parameter.span(0.56666666F, 0.7666667F));
        this.addHighSlice(biomeRegistry, mapper, Climate.Parameter.span(0.7666667F, 0.93333334F));
        this.addMidSlice(biomeRegistry, mapper, Climate.Parameter.span(0.93333334F, 1.0F));
    }

    private void addPeaks(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
    {
        for (int i = 0; i < this.temperatures.length; ++i)
        {
            Climate.Parameter temperature = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j)
            {
                Climate.Parameter humidity = this.humidities[j];

                ResourceKey<Biome> middleBiomeVanilla                     = this.pickMiddleBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBiomeBOP                         = this.pickMiddleBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> middleOrBadlandsBiomeVanilla           = this.pickMiddleBiomeOrBadlandsIfHotVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBadlandsOrSlopeBiomeVanilla      = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBadlandsOrSlopeBiomeBOP          = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> rareBiomeBOP                           = this.pickRareBiomeBOP(biomeRegistry, i, j, weirdness);

                ResourceKey<Biome> plateauBiome                           = this.pickPlateauBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> plateauBiomeBOP                        = this.pickPlateauBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> extremeHillsBiome                      = this.pickExtremeHillsBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> extremeHillsBiomeBOP                   = this.pickExtremeHillsBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> shatteredBiome                         = this.maybePickShatteredBiome(i, j, weirdness, extremeHillsBiome);
                ResourceKey<Biome> peakBiome                              = this.pickPeakBiome(i, j, weirdness);
                ResourceKey<Biome> peakBiomeBOP                           = this.pickPeakBiomeBOP(biomeRegistry,  i, j, weirdness);

                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[0], weirdness, 0.0F, peakBiome, peakBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[1], weirdness, 0.0F, middleBadlandsOrSlopeBiomeVanilla, middleBadlandsOrSlopeBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], weirdness, 0.0F, peakBiome, peakBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], weirdness, 0.0F, plateauBiome, plateauBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.midInlandContinentalness, this.erosions[3], weirdness, 0.0F, middleOrBadlandsBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.farInlandContinentalness, this.erosions[3], weirdness, 0.0F, plateauBiome, plateauBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], weirdness, 0.0F, shatteredBiome);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], weirdness, 0.0F, extremeHillsBiome, extremeHillsBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
            }
        }

    }

    private void addHighSlice(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
    {
        for (int i = 0; i < this.temperatures.length; ++i)
        {
            Climate.Parameter temperature = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j)
            {
                Climate.Parameter humidity = this.humidities[j];

                ResourceKey<Biome> middleBiomeVanilla                = this.pickMiddleBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBiomeBOP                    = this.pickMiddleBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> middleOrBadlandsBiomeVanilla      = this.pickMiddleBiomeOrBadlandsIfHotVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBadlandsOrSlopeBiomeVanilla = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBadlandsOrSlopeBiomeBOP     = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> rareBiomeBOP                      = this.pickRareBiomeBOP(biomeRegistry, i, j, weirdness);

                ResourceKey<Biome> plateauBiome               = this.pickPlateauBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> plateauBiomeBOP            = this.pickPlateauBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> extremeHillsBiome          = this.pickExtremeHillsBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> extremeHillsBiomeBOP       = this.pickExtremeHillsBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> shatteredBiome             = this.maybePickShatteredBiome(i, j, weirdness, middleBiomeVanilla);
                ResourceKey<Biome> slopeBiomeVanilla          = this.pickSlopeBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> slopeBiomeBOP              = this.pickSlopeBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> peakBiome                  = this.pickPeakBiome(i, j, weirdness);
                ResourceKey<Biome> peakBiomeBOP               = this.pickPeakBiomeBOP(biomeRegistry, i, j, weirdness);

                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[0], weirdness, 0.0F, slopeBiomeVanilla, slopeBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[0], weirdness, 0.0F, peakBiome, peakBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[1], weirdness, 0.0F, middleBadlandsOrSlopeBiomeVanilla, middleBadlandsOrSlopeBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], weirdness, 0.0F, slopeBiomeVanilla, slopeBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], weirdness, 0.0F, plateauBiome, plateauBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.midInlandContinentalness, this.erosions[3], weirdness, 0.0F, middleOrBadlandsBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.farInlandContinentalness, this.erosions[3], weirdness, 0.0F, plateauBiome, plateauBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], weirdness, 0.0F, shatteredBiome);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], weirdness, 0.0F, extremeHillsBiome, extremeHillsBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
            }
        }

    }

    private void addMidSlice(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
    {
        this.addSurfaceBiome(mapper, this.FULL_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[2]), weirdness, 0.0F, Biomes.STONY_SHORE);

        for (int i = 0; i < this.temperatures.length; ++i)
        {
            Climate.Parameter temperature = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j)
            {
                Climate.Parameter humidity = this.humidities[j];

                ResourceKey<Biome> middleBiomeVanilla                  = this.pickMiddleBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBiomeBOP                      = this.pickMiddleBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> middleOrBadlandsBiomeVanilla        = this.pickMiddleBiomeOrBadlandsIfHotVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBadlandsOrSlopeBiomeVanilla   = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBadlandsOrSlopeBiomeBOP       = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBOP(biomeRegistry, i, j, weirdness);

                ResourceKey<Biome> extremeHillsBiome          = this.pickExtremeHillsBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> extremeHillsBiomeBOP       = this.pickExtremeHillsBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> plateauBiomeVanilla        = this.pickPlateauBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> plateauBiomeBOP            = this.pickPlateauBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> beachBiome                 = this.pickBeachBiome(biomeRegistry, i, j);
                ResourceKey<Biome> shatteredBiome             = this.maybePickShatteredBiome(i, j, weirdness, middleBiomeVanilla);
                ResourceKey<Biome> shatteredCoastBiome        = this.pickShatteredCoastBiome(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> slopeBiomeVanilla          = this.pickSlopeBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> slopeBiomeBOP              = this.pickSlopeBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> swampBiomeBOP              = this.pickSwampBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> rareBiomeBOP               = this.pickRareBiomeBOP(biomeRegistry, i, j, weirdness);

                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[0], weirdness, 0.0F, slopeBiomeVanilla, slopeBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.midInlandContinentalness), this.erosions[1], weirdness, 0.0F, middleBadlandsOrSlopeBiomeVanilla, middleBadlandsOrSlopeBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.farInlandContinentalness, this.erosions[1], weirdness, 0.0F, i == 0 ? slopeBiomeVanilla : plateauBiomeVanilla, i == 0 ? slopeBiomeBOP : plateauBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[2], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.midInlandContinentalness, this.erosions[2], weirdness, 0.0F, middleOrBadlandsBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.farInlandContinentalness, this.erosions[2], weirdness, 0.0F, plateauBiomeVanilla, plateauBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[3], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[3], weirdness, 0.0F, middleOrBadlandsBiomeVanilla, middleBiomeBOP, rareBiomeBOP);

                if (weirdness.max() < 0L)
                {
                    this.addSurfaceBiomeGlobal(mapper, temperature, humidity, this.coastContinentalness, this.erosions[4], weirdness, 0.0F, beachBiome);
                    this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                }
                else
                {
                    this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                }

                this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[5], weirdness, 0.0F, shatteredCoastBiome);
                this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[5], weirdness, 0.0F, shatteredBiome);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], weirdness, 0.0F, extremeHillsBiome, extremeHillsBiomeBOP);
                if (weirdness.max() < 0L)
                {
                    this.addSurfaceBiomeGlobal(mapper, temperature, humidity, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, beachBiome);
                }
                else
                {
                    this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                }

                if (i == 0)
                {
                    this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.MUSKEG, rareBiomeBOP));
                }
                else
                {
                    this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, Biomes.SWAMP, swampBiomeBOP);
                }
            }
        }

    }

    private void addLowSlice(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
    {
        this.addSurfaceBiome(mapper, this.FULL_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[2]), weirdness, 0.0F, Biomes.STONY_SHORE);

        for (int i = 0; i < this.temperatures.length; ++i)
        {
            Climate.Parameter temperature = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j)
            {
                Climate.Parameter humidity = this.humidities[j];

                ResourceKey<Biome> middleBiomeVanilla                  = this.pickMiddleBiomeVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBiomeBOP                      = this.pickMiddleBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> middleOrBadlandsBiomeVanilla        = this.pickMiddleBiomeOrBadlandsIfHotVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBadlandsOrSlopeBiomeVanilla   = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBadlandsOrSlopeBiomeBOP       = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBOP(biomeRegistry, i, j, weirdness);

                ResourceKey<Biome> beachBiome                   = this.pickBeachBiome(biomeRegistry, i, j);
                ResourceKey<Biome> shatteredBiome               = this.maybePickShatteredBiome(i, j, weirdness, middleBiomeVanilla);
                ResourceKey<Biome> shatteredCoastBiome          = this.pickShatteredCoastBiome(biomeRegistry, i, j, weirdness);

                ResourceKey<Biome> rareBiomeBOP                 = this.pickRareBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> swampBiomeBOP                = this.pickSwampBiomeBOP(biomeRegistry, i, j, weirdness);

                // Lowest to low erosion
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, middleOrBadlandsBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, middleBadlandsOrSlopeBiomeVanilla, middleBadlandsOrSlopeBiomeBOP, rareBiomeBOP);

                // Reduced to moderate erosion
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[2], this.erosions[3]), weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), weirdness, 0.0F, middleOrBadlandsBiomeVanilla, middleBiomeBOP, rareBiomeBOP);

                // Moderate to increased erosion
                this.addSurfaceBiomeGlobal(mapper, temperature, humidity, this.coastContinentalness, Climate.Parameter.span(this.erosions[3], this.erosions[4]), weirdness, 0.0F, beachBiome);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);

                // High erosion
                this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[5], weirdness, 0.0F, shatteredCoastBiome);
                this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[5], weirdness, 0.0F, shatteredBiome);
                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, rareBiomeBOP);

                // Highest erosion
                this.addSurfaceBiomeGlobal(mapper, temperature, humidity, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, beachBiome);

                if (i == 0)
                {
                    this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, middleBiomeVanilla, middleBiomeBOP, BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.MUSKEG, rareBiomeBOP));
                }
                else
                {
                    this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, Biomes.SWAMP, swampBiomeBOP);
                }
            }
        }

    }

    private void addValleys(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
    {
        this.addSurfaceBiome(mapper, this.FROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, weirdness.max() < 0L ? Biomes.STONY_SHORE : Biomes.FROZEN_RIVER);
        this.addSurfaceBiome(mapper, this.UNFROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, weirdness.max() < 0L ? Biomes.STONY_SHORE : Biomes.RIVER);
        this.addSurfaceBiome(mapper, this.FROZEN_RANGE, this.FULL_RANGE, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome(mapper, this.UNFROZEN_RANGE, this.FULL_RANGE, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, Biomes.RIVER);
        this.addSurfaceBiome(mapper, this.FROZEN_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[5]), weirdness, 0.0F, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome(mapper, this.UNFROZEN_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[5]), weirdness, 0.0F, Biomes.RIVER);

        // Coastal watery valleys
        this.addSurfaceBiome(mapper, this.FROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome(mapper, this.UNFROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, Biomes.RIVER);

        // Inland watery valleys
        this.addSurfaceBiome(mapper, this.FROZEN_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, Biomes.FROZEN_RIVER);

        for (int i = 0; i < this.temperatures.length; ++i)
        {
            Climate.Parameter temperature = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j)
            {
                Climate.Parameter humidity = this.humidities[j];
                ResourceKey<Biome> middleOrBadlandsBiomeVanilla = this.pickMiddleBiomeOrBadlandsIfHotVanilla(i, j, weirdness);
                ResourceKey<Biome> middleBiomeBOP               = this.pickMiddleBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> rareBiomeBOP                 = this.pickRareBiomeBOP(biomeRegistry, i, j, weirdness);
                ResourceKey<Biome> swampBiomeBOP                = this.pickSwampBiomeBOP(biomeRegistry, i, j, weirdness);

                this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, middleOrBadlandsBiomeVanilla, middleBiomeBOP, rareBiomeBOP);

                if (i != 0)
                {
                    this.addParallelSurfaceBiomes(mapper, temperature, humidity, Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, Biomes.SWAMP, swampBiomeBOP);
                }
            }
        }
    }

    private void addUndergroundBiomes(Registry<Biome> biomeRegistry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        this.addParallelUndergroundBiomes(mapper, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(0.8F, 1.0F), this.FULL_RANGE, this.FULL_RANGE, this.DEFAULT_DEPTH_RANGE, 0.0F, Biomes.DRIPSTONE_CAVES, BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.SPIDER_NEST, Biomes.DRIPSTONE_CAVES));
        this.addParallelUndergroundBiomes(mapper, this.FULL_RANGE, Climate.Parameter.span(0.7F, 1.0F), this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, this.DEFAULT_DEPTH_RANGE, 0.0F, Biomes.LUSH_CAVES, BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.GLOWING_GROTTO, Biomes.LUSH_CAVES));
    }

    private ResourceKey<Biome> pickIslandBiomeBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex)
    {
        return BiomeUtil.biomeOrFallback(biomeRegistry, this.ISLAND_BIOMES_BOP[temperatureIndex][humidityIndex], Biomes.MUSHROOM_FIELDS);
    }

    private ResourceKey<Biome> pickMiddleBiomeVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        if (weirdness.max() < 0L)
        {
            return this.MIDDLE_BIOMES[temperatureIndex][humidityIndex];
        }
        else
        {
            ResourceKey<Biome> variantBiome = this.MIDDLE_BIOMES_VARIANT[temperatureIndex][humidityIndex];
            return variantBiome == null ? this.MIDDLE_BIOMES[temperatureIndex][humidityIndex] : variantBiome;
        }
    }

    private ResourceKey<Biome> pickMiddleBiomeBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        ResourceKey<Biome> middleBiome = BiomeUtil.biomeOrFallback(biomeRegistry, this.MIDDLE_BIOMES_BOP[temperatureIndex][humidityIndex], this.MIDDLE_BIOMES[temperatureIndex][humidityIndex]);

        if (weirdness.max() < 0) return middleBiome;
        else
        {
            return BiomeUtil.biomeOrFallback(biomeRegistry, this.MIDDLE_BIOMES_VARIANT_BOP[temperatureIndex][humidityIndex], middleBiome);
        }
    }

    private ResourceKey<Biome> pickRareBiomeBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        ResourceKey<Biome> middleBiome = this.pickMiddleBiomeBOP(biomeRegistry, temperatureIndex, humidityIndex, weirdness);
        return BiomeUtil.biomeOrFallback(biomeRegistry, this.RARE_BIOMES_BOP[temperatureIndex][humidityIndex], middleBiome);
    }

    private ResourceKey<Biome> pickMiddleBiomeOrBadlandsIfHotVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        return temperatureIndex == 4 ? this.pickBadlandsBiome(humidityIndex, weirdness) : this.pickMiddleBiomeVanilla(temperatureIndex, humidityIndex, weirdness);
    }

    private ResourceKey<Biome> pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        return temperatureIndex == 0 ? this.pickSlopeBiomeVanilla(temperatureIndex, humidityIndex, weirdness) : this.pickMiddleBiomeOrBadlandsIfHotVanilla(temperatureIndex, humidityIndex, weirdness);
    }

    private ResourceKey<Biome> pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        return temperatureIndex == 0 ? this.pickSlopeBiomeBOP(biomeRegistry, temperatureIndex, humidityIndex, weirdness) : this.pickMiddleBiomeBOP(biomeRegistry, temperatureIndex, humidityIndex, weirdness);
    }

    private ResourceKey<Biome> pickSwampBiomeBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        return BiomeUtil.biomeOrFallback(biomeRegistry, this.SWAMP_BIOMES_BOP[temperatureIndex][humidityIndex], this.pickMiddleBiomeBOP(biomeRegistry, temperatureIndex, humidityIndex, weirdness), Biomes.SWAMP);
    }

    private ResourceKey<Biome> maybePickShatteredBiome(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness, ResourceKey<Biome> extremeHillsBiome)
    {
        return temperatureIndex > 1 && humidityIndex < 4 && weirdness.max() >= 0L ? Biomes.WINDSWEPT_SAVANNA : extremeHillsBiome;
    }

    private ResourceKey<Biome> pickShatteredCoastBiome(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        ResourceKey<Biome> resourcekey = weirdness.max() >= 0L ? this.pickMiddleBiomeVanilla(temperatureIndex, humidityIndex, weirdness) : this.pickBeachBiome(biomeRegistry, temperatureIndex, humidityIndex);
        return this.maybePickShatteredBiome(temperatureIndex, humidityIndex, weirdness, resourcekey);
    }

    private ResourceKey<Biome> pickBeachBiome(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex)
    {
        if (temperatureIndex == 0)
            return Biomes.SNOWY_BEACH;
        else if (temperatureIndex == 2 && humidityIndex <= 2)
        {
            return BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.DUNE_BEACH, Biomes.BEACH);
        }
        else
        {
            return temperatureIndex == 4 ? Biomes.DESERT : Biomes.BEACH;
        }
    }

    private ResourceKey<Biome> pickBadlandsBiome(int humidityIndex, Climate.Parameter weirdness)
    {
        if (humidityIndex < 2)
        {
            return weirdness.max() < 0L ? Biomes.ERODED_BADLANDS : Biomes.BADLANDS;
        }
        else
        {
            return humidityIndex < 3 ? Biomes.BADLANDS : Biomes.WOODED_BADLANDS;
        }
    }

    private ResourceKey<Biome> pickPlateauBiomeVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        if (weirdness.max() < 0L)
        {
            return this.PLATEAU_BIOMES[temperatureIndex][humidityIndex];
        }
        else
        {
            ResourceKey<Biome> resourcekey = this.PLATEAU_BIOMES_VARIANT[temperatureIndex][humidityIndex];
            return resourcekey == null ? this.PLATEAU_BIOMES[temperatureIndex][humidityIndex] : resourcekey;
        }
    }

    private ResourceKey<Biome> pickPlateauBiomeBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        if (weirdness.max() < 0L) return BiomeUtil.biomeOrFallback(biomeRegistry, this.PLATEAU_BIOMES_BOP[temperatureIndex][humidityIndex], this.PLATEAU_BIOMES[temperatureIndex][humidityIndex]);
        else return BiomeUtil.biomeOrFallback(biomeRegistry, this.PLATEAU_BIOMES_VARIANT_BOP[temperatureIndex][humidityIndex], this.PLATEAU_BIOMES_BOP[temperatureIndex][humidityIndex], this.PLATEAU_BIOMES[temperatureIndex][humidityIndex]);
    }

    private ResourceKey<Biome> pickPeakBiome(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        if (temperatureIndex <= 2)
        {
            return weirdness.max() < 0L ? Biomes.JAGGED_PEAKS : Biomes.FROZEN_PEAKS;
        }
        else
        {
            return temperatureIndex == 3 ? Biomes.STONY_PEAKS : this.pickBadlandsBiome(humidityIndex, weirdness);
        }
    }

    private ResourceKey<Biome> pickPeakBiomeBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        ResourceKey<Biome> peakBiome = this.pickPeakBiome(temperatureIndex, humidityIndex, weirdness);

        if (temperatureIndex == 1) return BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.CRAG, peakBiome);
        else if (temperatureIndex == 2) return BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.JADE_CLIFFS, peakBiome);
        else return peakBiome;
    }

    private ResourceKey<Biome> pickSlopeBiomeVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        if (temperatureIndex >= 3)
        {
            return this.pickPlateauBiomeVanilla(temperatureIndex, humidityIndex, weirdness);
        }
        else
        {
            return humidityIndex <= 1 ? Biomes.SNOWY_SLOPES : Biomes.GROVE;
        }
    }

    private ResourceKey<Biome> pickSlopeBiomeBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        ResourceKey<Biome> plateauBiome = this.pickPlateauBiomeBOP(biomeRegistry, temperatureIndex, humidityIndex, weirdness);

        if (temperatureIndex == 1) return BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.CRAG, plateauBiome);
        else if (temperatureIndex == 2) return BiomeUtil.biomeOrFallback(biomeRegistry, BOPBiomes.JADE_CLIFFS, plateauBiome);
        else return plateauBiome;
    }

    private ResourceKey<Biome> pickExtremeHillsBiomeVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        ResourceKey<Biome> resourcekey = this.EXTREME_HILLS[temperatureIndex][humidityIndex];
        return resourcekey == null ? this.pickMiddleBiomeVanilla(temperatureIndex, humidityIndex, weirdness) : resourcekey;
    }

    private ResourceKey<Biome> pickExtremeHillsBiomeBOP(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
    {
        return BiomeUtil.biomeOrFallback(biomeRegistry, this.EXTREME_HILLS_BOP[temperatureIndex][humidityIndex], this.pickExtremeHillsBiomeVanilla(temperatureIndex, humidityIndex, weirdness));
    }

    private void addParallelSurfaceBiomes(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset, ResourceKey<Biome> vanillaBiome, ResourceKey<Biome> bopBiome)
    {
        addParallelSurfaceBiomes(mapper, temperature, humidity, continentalness, erosion, weirdness, offset, vanillaBiome, bopBiome, bopBiome);
    }

    private void addParallelSurfaceBiomes(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset, ResourceKey<Biome> vanillaBiome, ResourceKey<Biome> bopBiome, ResourceKey<Biome> rareBiome)
    {
        addSurfaceBiome(mapper, temperature, humidity, continentalness, erosion, weirdness, this.vanillaUniqueness, offset, vanillaBiome);
        addSurfaceBiome(mapper, temperature, humidity, continentalness, erosion, weirdness, this.bopUniqueness, offset, bopBiome);
        addSurfaceBiome(mapper, temperature, humidity, continentalness, erosion, weirdness, this.rareUniqueness, offset, rareBiome);
    }

    private void addSurfaceBiome(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset, ResourceKey<Biome> biome)
    {
        addSurfaceBiome(mapper, temperature, humidity, continentalness, erosion, weirdness, this.vanillaUniqueness, offset, biome);
    }

    private void addSurfaceBiomeGlobal(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset, ResourceKey<Biome> biome)
    {
        addSurfaceBiome(mapper, temperature, humidity, continentalness, erosion, weirdness, this.FULL_RANGE, offset, biome);
    }

    private void addSurfaceBiome(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, Climate.Parameter uniqueness, float offset, ResourceKey<Biome> biome)
    {
        mapper.accept(Pair.of(TBClimate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(0.0F), weirdness, uniqueness, offset), biome));
        mapper.accept(Pair.of(TBClimate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(1.0F), weirdness, uniqueness, offset), biome));
    }

    private void addParallelUndergroundBiomes(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, Climate.Parameter depth, float offset, ResourceKey<Biome> vanillaBiome, ResourceKey<Biome> bopBiome)
    {
        mapper.accept(Pair.of(TBClimate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, this.vanillaUniqueness, offset), vanillaBiome));
        mapper.accept(Pair.of(TBClimate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, this.bopUniqueness, offset), bopBiome));
        mapper.accept(Pair.of(TBClimate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, this.rareUniqueness, offset), bopBiome));
    }
}