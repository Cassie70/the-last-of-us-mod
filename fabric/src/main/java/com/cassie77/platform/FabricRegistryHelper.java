package com.cassie77.platform;

import com.cassie77.platform.services.IRegistryHelper;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.function.Supplier;

/**
 * Fabric implementation of IRegistryHelper.
 * On Fabric, SensorType's constructor is made public via Fabric API's access widener,
 * so we can call it directly here without issues.
 */
public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public <U extends Sensor<?>> SensorType<U> createSensorType(Supplier<U> factory) {
        return new SensorType<>(factory);
    }
}
