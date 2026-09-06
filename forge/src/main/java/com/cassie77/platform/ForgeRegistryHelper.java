package com.cassie77.platform;

import com.cassie77.platform.services.IRegistryHelper;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.function.Supplier;

/**
 * Forge implementation of IRegistryHelper.
 * On Forge, SensorType's constructor is made accessible via the
 * Access Transformer declared in META-INF/accesstransformer.cfg.
 */
public class ForgeRegistryHelper implements IRegistryHelper {

    @Override
    public <U extends Sensor<?>> SensorType<U> createSensorType(Supplier<U> factory) {
        return new SensorType<>(factory);
    }
}
