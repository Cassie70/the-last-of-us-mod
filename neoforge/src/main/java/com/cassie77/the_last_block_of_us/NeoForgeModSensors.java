package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.infected.InfectedAttackablesSensor;
import com.cassie77.the_last_block_of_us.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class NeoForgeModSensors {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE,
            Constants.MOD_ID);
    public static final DeferredHolder<SensorType<?>, SensorType<InfectedAttackablesSensor>> INFECTED_ENTITY_SENSOR = SENSOR_TYPES
            .register(
                    "infected_entity_sensor", () -> {
                        SensorType<InfectedAttackablesSensor> sensor = Services.REGISTRY
                                .createSensorType(InfectedAttackablesSensor::new);
                        ModSensors.INFECTED_ENTITY_SENSOR = sensor;
                        return sensor;
                    });

    private NeoForgeModSensors() {
    }
}