package com.cassie77;

import com.cassie77.entity.bloater.BloaterAttackablesSensor;
import com.cassie77.entity.clicker.ClickerAttackablesSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

import static com.cassie77.TheLastOfUsMod.MOD_ID;

public class ModSensors <U extends Sensor<?>>{

    public static final SensorType<ClickerAttackablesSensor> CLICKER_ENTITY_SENSOR = register("clicker_entity_sensor", ClickerAttackablesSensor::new);
    public static final SensorType<BloaterAttackablesSensor> BLOATER_ENTITY_SENSOR = register("bloater_entity_sensor",BloaterAttackablesSensor::new);

    private final Supplier<U> factory;

    public ModSensors(Supplier<U> factory) {
        this.factory = factory;
    }

    public U create() {
        return this.factory.get();
    }

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return register(keyOf(id), factory);
    }

    private static RegistryKey<SensorType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.SENSOR_TYPE, Identifier.of(MOD_ID, id));
    }

    private static <U extends Sensor<?>> SensorType<U> register(RegistryKey<SensorType<?>> key, Supplier<U> factory) {
        return Registry.register(Registries.SENSOR_TYPE, key, new SensorType<>(factory));
    }

    public static void initialize() {
        // Initialization logic if needed
        TheLastOfUsMod.LOGGER.info("Registering " + TheLastOfUsMod.MOD_ID + " Sensors");
    }

}
