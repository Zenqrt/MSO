package dev.zenqrt.mso.lobby.messenger.responses;

import com.google.common.io.ByteArrayDataInput;
import net.minestom.server.entity.PlayerSkin;

import java.util.UUID;

public record ScoreResponse(UUID uuid, String username, PlayerSkin skin, int score) {

    public static ScoreResponse read(ByteArrayDataInput input) {
        String uuid = input.readUTF();
        String username = input.readUTF();
        String textureValue = input.readUTF();
        String signature = input.readUTF();
        int score = input.readInt();

        return new ScoreResponse(UUID.fromString(uuid), username, new PlayerSkin(textureValue, signature), score);
    }

}
