package com.cassie77.the_last_block_of_us.platform.services;

import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.function.Supplier;

/**
 * Platform-specific registry helper.
 * Needed because SensorType's constructor is package-private in vanilla
 * Minecraft,
 * and only accessible via Fabric API's access widener on Fabric.
 * Each platform provides its own implementation via ServiceLoader.
 */
public interface IRegistryHelper {

    /**
     * Creates a new SensorType wrapping the given factory supplier.
     * This must be implemented per-platform since the SensorType constructor
     * is not publicly accessible in vanilla (requires Fabric API or AT on Forge).
     *
     * @param factory Supplier that creates the sensor instance
     * @param <U>     The sensor type
     * @return A new SensorType wrapping the factory
     */
    <U extends Sensor<?>> SensorType<U> createSensorType(Supplier<U> factory);
}