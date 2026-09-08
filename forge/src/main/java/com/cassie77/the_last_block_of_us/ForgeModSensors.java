package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.infected.InfectedAttackablesSensor;
import com.cassie77.the_last_block_of_us.platform.Services;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeModSensors {

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, Constants.MOD_ID);

    public static final RegistryObject<SensorType<InfectedAttackablesSensor>> INFECTED_ENTITY_SENSOR =
            SENSOR_TYPES.register("infected_entity_sensor", () -> {
                SensorType<InfectedAttackablesSensor> sensor = Services.REGISTRY.createSensorType(InfectedAttackablesSensor::new);
                ModSensors.INFECTED_ENTITY_SENSOR = sensor;
                return sensor;
            });

    private ForgeModSensors() {
    }
}
