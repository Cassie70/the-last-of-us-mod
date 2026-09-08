package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.CustomAreaEffectCloudEntity;
import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.cassie77.the_last_block_of_us.entity.clicker.ClickerEntity;
import com.cassie77.the_last_block_of_us.item.bottle.BottleEntity;
import com.cassie77.the_last_block_of_us.item.micotoxinsac.MycotoxinSacEntity;
import com.cassie77.the_last_block_of_us.item.molotov.MolotovEntity;
import com.cassie77.the_last_block_of_us.item.nailbomb.NailBombEntity;
import com.cassie77.the_last_block_of_us.item.smokebomb.SmokeBombEntity;
import net.minecraft.world.entity.EntityType;

public class ModEntities {

    public static EntityType<MolotovEntity> MOLOTOV_ENTITY;
    public static EntityType<BottleEntity> BOTTLE_ENTITY;
    public static EntityType<NailBombEntity> NAIL_BOMB_ENTITY;
    public static EntityType<SmokeBombEntity> SMOKE_BOMB_ENTITY;
    public static EntityType<MycotoxinSacEntity> MYCOTOXIN_SAC_ENTITY;
    public static EntityType<ClickerEntity> CLICKER;
    public static EntityType<BloaterEntity> BLOATER;
    public static EntityType<CustomAreaEffectCloudEntity> CUSTOM_AREA_EFFECT_CLOUD_ENTITY;
}
