/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package biomesoplenty.fabric.mixin;

import biomesoplenty.api.block.BOPFluids;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class MixinBloodFluid
{
    @Inject(method = "computeFogColor", at = @At(value = "RETURN"), cancellable = true)
    private static void modifyFogColors(Camera camera, float f, ClientLevel level, int i, float g, CallbackInfoReturnable<Vector4f> cir)
    {
        BlockPos blockPos = camera.getBlockPosition();
        FluidState fluidState = level.getFluidState(blockPos);
        if(camera.getPosition().y > blockPos.getY() + fluidState.getHeight(level, blockPos))
        {
            return;
        }

        Fluid fluid = fluidState.getType();

        if(BOPFluids.BLOOD.isSame(fluid))
        {
            cir.setReturnValue(new Vector4f(0.407F, 0.121F, 0.137F, 1.0F));
        }
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void setupFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f vector4f, float f, boolean bl, float g, CallbackInfoReturnable<FogParameters> cir)
    {
        Level level = Minecraft.getInstance().level;
        BlockPos blockPos = camera.getBlockPosition();
        FluidState fluidState = level.getFluidState(blockPos);
        if(camera.getPosition().y >= blockPos.getY() + fluidState.getHeight(level, blockPos))
        {
            return;
        }

        Fluid fluid = fluidState.getType();

        if(BOPFluids.BLOOD.isSame(fluid))
        {
            cir.setReturnValue(new FogParameters(0.125F, 5.0F, FogShape.CYLINDER, vector4f.x, vector4f.y, vector4f.z, vector4f.w));
        }
    }
}
