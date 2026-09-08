package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.infected.InfectedAttackablesSensor;
import com.cassie77.the_last_block_of_us.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

public final class FabricModSensors {

    private FabricModSensors() {
    }

    public static void initialize() {
        ResourceKey<SensorType<?>> key = ResourceKey.create(Registries.SENSOR_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "infected_entity_sensor"));
        ModSensors.INFECTED_ENTITY_SENSOR = register(key, InfectedAttackablesSensor::new);
    }

    private static <U extends Sensor<?>> SensorType<U> register(ResourceKey<SensorType<?>> key,
                                                                java.util.function.Supplier<U> factory) {
        return Registry.register(BuiltInRegistries.SENSOR_TYPE, key,
                Services.REGISTRY.createSensorType(factory));
    }
}
