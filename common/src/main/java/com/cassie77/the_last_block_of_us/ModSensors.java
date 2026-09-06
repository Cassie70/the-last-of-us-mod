package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.bloater.BloaterAttackablesSensor;
import com.cassie77.the_last_block_of_us.entity.clicker.ClickerAttackablesSensor;
import com.cassie77.the_last_block_of_us.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.function.Supplier;

public class ModSensors {

    public static final SensorType<ClickerAttackablesSensor> CLICKER_ENTITY_SENSOR = register("clicker_entity_sensor",
            ClickerAttackablesSensor::new);
    public static final SensorType<BloaterAttackablesSensor> BLOATER_ENTITY_SENSOR = register("bloater_entity_sensor",
            BloaterAttackablesSensor::new);

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return register(keyOf(id), factory);
    }

    private static ResourceKey<SensorType<?>> keyOf(String id) {
        return ResourceKey.create(Registries.SENSOR_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, id));
    }

    private static <U extends Sensor<?>> SensorType<U> register(ResourceKey<SensorType<?>> key, Supplier<U> factory) {
        return Registry.register(BuiltInRegistries.SENSOR_TYPE, key, Services.REGISTRY.createSensorType(factory));
    }

    public static void initialize() {
        Constants.LOG.info("Registering {} Sensors", Constants.MOD_ID);
    }
}
