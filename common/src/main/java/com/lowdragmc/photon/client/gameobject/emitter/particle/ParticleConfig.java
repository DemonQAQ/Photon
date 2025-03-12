package com.lowdragmc.photon.client.gameobject.emitter.particle;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.syncdata.IPersistedSerializable;
import com.lowdragmc.photon.client.gameobject.emitter.PhotonParticleRenderType;
import com.lowdragmc.photon.client.gameobject.emitter.data.*;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.*;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.Color;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.Gradient;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.RandomColor;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.RandomGradient;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.curve.Curve;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.curve.CurveConfig;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.curve.RandomCurve;
import com.lowdragmc.photon.core.mixins.accessor.BlendModeAccessor;
import com.lowdragmc.photon.core.mixins.accessor.ShaderInstanceAccessor;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/6/11
 * @implNote ParticleConfig
 */
public class ParticleConfig implements IPersistedSerializable {
    @Setter
    @Getter
    @Configurable(name = "发射器持续时间", tips = "photon.emitter.config.duration")
    @NumberRange(range = {1, Integer.MAX_VALUE})
    protected int duration = 100;
    @Setter
    @Getter
    @Configurable(name = "循环发射", tips = "photon.emitter.config.looping")
    protected boolean looping = true;
    @Setter
    @Getter
    @Configurable(name = "发射器发射延迟", tips = "photon.emitter.config.startDelay")
    @NumberFunctionConfig(types = {Constant.class, RandomConstant.class, Curve.class, RandomCurve.class}, isDecimals = false, min = 0, curveConfig = @CurveConfig(bound = {0, 100}, xAxis = "duration", yAxis = "delay"))
    protected NumberFunction startDelay = NumberFunction.constant(0);
    @Setter
    @Getter
    @Configurable(name = "粒子生命时长", tips = "photon.emitter.config.startLifetime")
    @NumberFunctionConfig(types = {Constant.class, RandomConstant.class, Curve.class, RandomCurve.class}, isDecimals = false, min = 0, defaultValue = 100, curveConfig = @CurveConfig(bound = {0, 200}, xAxis = "duration", yAxis = "life time"))
    protected NumberFunction startLifetime = NumberFunction.constant(100);
    @Setter
    @Getter
    @Configurable(name = "粒子初始速度", tips = "photon.emitter.config.startSpeed")
    @NumberFunctionConfig(types = {Constant.class, RandomConstant.class, Curve.class, RandomCurve.class}, defaultValue = 1f, curveConfig = @CurveConfig(bound = {-2, 2}, xAxis = "duration", yAxis = "speed"))
    protected NumberFunction startSpeed = NumberFunction.constant(1);
    @Setter
    @Getter
    @Configurable(name = "粒子初始尺寸", tips = "photon.emitter.config.startSize")
    @NumberFunction3Config(common = @NumberFunctionConfig(types = {Constant.class, RandomConstant.class, Curve.class, RandomCurve.class}, min = 0, defaultValue = 0.1f, curveConfig = @CurveConfig(bound = {0, 1}, xAxis = "duration", yAxis = "size")))
    protected NumberFunction3 startSize = new NumberFunction3(0.1, 0.1, 0.1);
    @Setter
    @Getter
    @Configurable(name = "粒子初始旋转", tips = "photon.emitter.config.startRotation")
    @NumberFunction3Config(affectX = false, affectY = false, common = @NumberFunctionConfig(types = {Constant.class, RandomConstant.class, Curve.class, RandomCurve.class}, wheelDur = 10, curveConfig = @CurveConfig(bound = {0, 360}, xAxis = "duration", yAxis = "rotation")))
    protected NumberFunction3 startRotation = new NumberFunction3(0, 0, 0);
    @Setter
    @Getter
    @Configurable(name = "粒子初始颜色", tips = "photon.emitter.config.startColor")
    @NumberFunctionConfig(types = {Color.class, RandomColor.class, Gradient.class, RandomGradient.class}, defaultValue = -1)
    protected NumberFunction startColor = NumberFunction.color(-1);
    @Setter
    @Getter
    @Configurable(name = "粒子坐标系", tips = "photon.emitter.config.simulationSpace")
    protected Space simulationSpace = Space.Local;
    @Setter
    @Getter
    @Configurable(name = "粒子最大数量", tips = "photon.emitter.config.maxParticles")
    @NumberRange(range = {0, 100000}, wheel = 100)
    protected int maxParticles = 2000;
    @Setter
    @Getter
    @Configurable(name = "并行更新", tips = {"photon.emitter.config.parallelUpdate.0", "photon.emitter.config" +
            ".parallelUpdate.1"})
    protected boolean parallelUpdate = false;
    @Setter
    @Getter
    @Configurable(name = "并行渲染", tips = {"photon.emitter.config.parallelRendering.0",
            "photon.emitter.config.parallelRendering.1"})
    protected boolean parallelRendering = false;
    @Configurable(name = "发射速度", subConfigurable = true, tips = "photon.emitter.config.emission")
    public final EmissionSetting emission = new EmissionSetting();
    @Configurable(name = "发射形状", subConfigurable = true, tips = "photon.emitter.config.shape")
    public final ShapeSetting shape = new ShapeSetting();
    @Configurable(name = "粒子材质", subConfigurable = true, tips = "photon.emitter.config.material")
    public final MaterialSetting material = new MaterialSetting();
    @Configurable(name = "粒子渲染", subConfigurable = true, tips = "photon.emitter.config.renderer")
    public final RendererSetting.Particle renderer = new RendererSetting.Particle();
    @Configurable(name = "粒子物理", subConfigurable = true, tips = "photon.emitter.config.physics")
    public final PhysicsSetting physics = new PhysicsSetting();
    @Configurable(name = "粒子光照", subConfigurable = true, tips = "photon.emitter.config.lights")
    public final LightOverLifetimeSetting lights = new LightOverLifetimeSetting();
    @Configurable(name = "粒子速度", subConfigurable = true, tips = "photon.emitter.config.velocityOverLifetime")
    public final VelocityOverLifetimeSetting velocityOverLifetime = new VelocityOverLifetimeSetting();
    @Configurable(name = "粒子速度与发射速度关系", subConfigurable = true, tips = "photon.emitter.config.inheritVelocity")
    public final InheritVelocitySetting inheritVelocity = new InheritVelocitySetting();
    @Configurable(name = "粒子速度与生命周期关系", subConfigurable = true, tips = "photon.emitter.config.lifetimeByEmitterSpeed")
    public final LifetimeByEmitterSpeedSetting lifetimeByEmitterSpeed = new LifetimeByEmitterSpeedSetting();
    @Configurable(name = "粒子受力", subConfigurable = true, tips = "photon.emitter.config.forceOverLifetime")
    public final ForceOverLifetimeSetting forceOverLifetime = new ForceOverLifetimeSetting();
    @Configurable(name = "粒子颜色与生命周期关系", subConfigurable = true, tips = "photon.emitter.config.colorOverLifetime")
    public final ColorOverLifetimeSetting colorOverLifetime = new ColorOverLifetimeSetting();
    @Configurable(name = "粒子颜色与速度关系", subConfigurable = true, tips = "photon.emitter.config.colorBySpeed")
    public final ColorBySpeedSetting colorBySpeed = new ColorBySpeedSetting();
    @Configurable(name = "粒子尺寸与生命周期关系", subConfigurable = true, tips = "photon.emitter.config.sizeOverLifetime")
    public final SizeOverLifetimeSetting sizeOverLifetime = new SizeOverLifetimeSetting();
    @Configurable(name = "粒子尺寸与速度关系", subConfigurable = true, tips = "photon.emitter.config.sizeBySpeed")
    public final SizeBySpeedSetting sizeBySpeed = new SizeBySpeedSetting();
    @Configurable(name = "粒子旋转与生命周期关系", subConfigurable = true, tips = "photon.emitter.config.rotationOverLifetime")
    public final RotationOverLifetimeSetting rotationOverLifetime = new RotationOverLifetimeSetting();
    @Configurable(name = "粒子旋转与速度关系", subConfigurable = true, tips = "photon.emitter.config.rotationBySpeed")
    public final RotationBySpeedSetting rotationBySpeed = new RotationBySpeedSetting();
    @Configurable(name = "噪声效果", subConfigurable = true, tips = "photon.emitter.config.noise")
    public final NoiseSetting noise = new NoiseSetting();
    @Configurable(name = "UV动画", subConfigurable = true, tips = "photon.emitter.config.uvAnimation")
    public final UVAnimationSetting uvAnimation = new UVAnimationSetting();
    @Configurable(name = "拖尾", subConfigurable = true, tips = "photon.emitter.config.trails")
    public final TrailsSetting trails = new TrailsSetting();
    @Configurable(name = "子发射器", subConfigurable = true, tips = "photon.emitter.config.sub_emitters")
    public final SubEmittersSetting subEmitters = new SubEmittersSetting();

    // runtime
    public final PhotonParticleRenderType particleRenderType = new RenderType();

    public enum Space {
        Local,
        World
    }

    public ParticleConfig() {
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        IPersistedSerializable.super.deserializeNBT(tag);
        // compatible with old version
        if (!tag.contains("simulationSpace")) {
            var number = NumberFunction.deserializeWrapper(tag.getCompound("startSize"));
            startSize = new NumberFunction3(number, NumberFunction.copy(number), NumberFunction.copy(number));
            startRotation = new NumberFunction3(startRotation.z, startRotation.y, startRotation.x);
            simulationSpace = Space.World;
        }
    }

    private class RenderType extends PhotonParticleRenderType {
        private BlendMode lastBlend = null;

        @Override
        public void prepareStatus() {
            if (renderer.isBloomEffect()) {
                beginBloom();
            }
            material.pre();
            material.getMaterial().begin(false);
            if (RenderSystem.getShader() instanceof ShaderInstanceAccessor shader) {
                lastBlend = BlendModeAccessor.getLastApplied();
                BlendModeAccessor.setLastApplied(shader.getBlend());
            }
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        }

        @Override
        public void begin(@Nonnull BufferBuilder bufferBuilder) {
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void releaseStatus() {
            material.getMaterial().end(false);
            material.post();
            if (lastBlend != null) {
                lastBlend.apply();
                lastBlend = null;
            }
            if (renderer.isBloomEffect()) {
                endBloom();
            }
        }

        @Override
        public boolean isParallel() {
            return isParallelRendering();
        }
    }

}
