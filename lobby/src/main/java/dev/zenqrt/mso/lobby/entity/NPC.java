package dev.zenqrt.mso.lobby.entity;

import net.minestom.server.entity.*;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.network.packet.server.play.*;
import net.minestom.server.utils.PacketUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class NPC extends Entity {

    private final String internalUsername;
    private String username;
    private PlayerSkin skin;
    private float scale;

    public NPC(String internalUsername, String username, PlayerSkin skin) {
        super(EntityType.PLAYER);

        this.internalUsername = internalUsername;
        this.username = username;
        this.skin = skin;
    }

    public NPC(String username, PlayerSkin skin) {
        this(randomHex(), username, skin);
    }

    private static String randomHex() {
        Random random = new Random();
        int number = random.nextInt(0x10) + 0x10;
        return Integer.toHexString(number);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void updateNewViewer(@NotNull Player player) {
        PlayerInfoUpdatePacket.Entry entry = new PlayerInfoUpdatePacket.Entry(
                getUuid(),
                internalUsername,
                List.of(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature())),
                false,
                0,
                GameMode.ADVENTURE,
                null,
                null
        );
        PlayerInfoUpdatePacket packet = new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry);
        player.sendPacket(packet);
        super.updateNewViewer(player);

        player.sendPacket(new EntityMetaDataPacket(getEntityId(), Map.of(17, Metadata.Byte((byte) 127))));

        EntityAttributesPacket propertiesPacket = new EntityAttributesPacket(this.getEntityId(), List.of(
                new AttributeInstance(Attribute.GENERIC_SCALE, scale, Collections.emptyList(), _ -> {})
        ));
        player.sendPacket(propertiesPacket);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);
        player.sendPacket(new PlayerInfoRemovePacket(getUuid()));
    }

    public void hideNameTag() {
        Entity tagHider = new Entity(EntityType.SLIME);
        tagHider.editEntityMeta(EntityMeta.class, meta -> meta.setInvisible(true));
        tagHider.setInstance(instance, getPosition());

        instance.loadChunk(getPosition()).thenRun(() -> addPassenger(tagHider));
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getInternalUsername() {
        return internalUsername;
    }

    public void setSkin(PlayerSkin skin) {
        this.skin = skin;

        if (this.instance != null) {
            DestroyEntitiesPacket destroyEntitiesPacket = new DestroyEntitiesPacket(this.getEntityId());
            PlayerInfoRemovePacket removePlayerPacket = this.getRemovePlayerToList();
            PlayerInfoUpdatePacket addPlayerPacket = this.getAddPlayerToList();
            PacketUtils.broadcastPlayPacket(removePlayerPacket);
            this.sendPacketToViewers(destroyEntitiesPacket);
            PacketUtils.broadcastPlayPacket(addPlayerPacket);

            if (currentChunk.isLoaded()) {
                this.teleport(this.getPosition());
            }
        }
    }

    @NotNull
    private PlayerInfoUpdatePacket getAddPlayerToList() {
        return new PlayerInfoUpdatePacket(EnumSet.of(PlayerInfoUpdatePacket.Action.ADD_PLAYER, PlayerInfoUpdatePacket.Action.UPDATE_LISTED), List.of(this.infoEntry()));
    }

    @NotNull
    private PlayerInfoRemovePacket getRemovePlayerToList() {
        return new PlayerInfoRemovePacket(this.getUuid());
    }

    private PlayerInfoUpdatePacket.Entry infoEntry() {
        PlayerSkin skin = this.skin;
        List<PlayerInfoUpdatePacket.Property> prop = skin != null ? List.of(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature())) : List.of();
        return new PlayerInfoUpdatePacket.Entry(this.getUuid(), username, prop, true, 0, GameMode.ADVENTURE, null, null);
    }

    public PlayerSkin getSkin() {
        return skin;
    }

    public void setScale(float scale) {
        this.scale = scale;

        EntityAttributesPacket propertiesPacket = new EntityAttributesPacket(this.getEntityId(), List.of(
                new AttributeInstance(Attribute.GENERIC_SCALE, scale, Collections.emptyList(), _ -> {})
        ));
        this.sendPacketToViewers(propertiesPacket);
    }

    public float getScale() {
        return scale;
    }
}
