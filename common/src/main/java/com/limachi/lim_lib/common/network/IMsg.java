package com.limachi.lim_lib.common.network;

//import com.limachi.lim_lib.common.mod_creation.ModBase;

import com.limachi.lim_lib.ModInstances;
import dev.architectury.networking.NetworkManager;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface IMsg<T extends IMsg<T>> extends CustomPacketPayload {
    void run(NetworkManager.PacketContext ctx);

    default Boolean upstream() { return null; }

    @Override
    default Type<T> type() { return ModInstances.getModByOwnedClass(getClass()).registries.getMessageType(this); }

    default Type<T> getType(ResourceLocation id) {
        if (this instanceof IS2CMsg && this instanceof IC2SMsg) {
            if (upstream() instanceof Boolean upstream)
                id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + (upstream ? "_c2s" : "_s2c"));
            else
                throw new RuntimeException("IS2CMsg + IC2SMsg record or class should return a valid boolean when calling upstream()");
        }
        return new Type<>(id);
    }
}