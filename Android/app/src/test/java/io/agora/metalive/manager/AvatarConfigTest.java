package io.agora.metalive.manager;

import org.junit.Test;

public class AvatarConfigTest {
    @Test
    public void testAvatarConfigParser() {
        String configString = "{" +
                "\"55\":[" +
                "{\"id\":\"55001\",\"name\":\"眉毛1\",\"icon\":\"https:\\/\\/store.gtx.fun\\/avatar\\/cjie\\/icon\\/55001.png\",\"version\":0,\"tag\":0,\"status\":0,\"isUsing\":0,\"zOrder\":0}," +
                "{\"id\":\"55002\",\"name\":\"眉毛2\",\"icon\":\"https:\\/\\/store.gtx.fun\\/avatar\\/cjie\\/icon\\/55002.png\",\"version\":0,\"tag\":0,\"status\":0,\"isUsing\":1,\"zOrder\":0}" +
                "]," +
                "\"70\":[" +
                "{\"id\":\"70001\",\"name\":\"发型1\",\"icon\":\"https:\\/\\/store.gtx.fun\\/avatar\\/cjie\\/icon\\/70001.png\",\"version\":0,\"tag\":0,\"status\":0,\"isUsing\":0,\"zOrder\":0}," +
                "{\"id\":\"70002\",\"name\":\"发型2\",\"icon\":\"https:\\/\\/store.gtx.fun\\/avatar\\/cjie\\/icon\\/70002.png\",\"version\":0,\"tag\":0,\"status\":0,\"isUsing\":0,\"zOrder\":0}" +
                "]}";

        AvatarConfigManager.getInstance().parseDressConfig(configString);
        System.out.println(AvatarConfigManager.getInstance().dump());
    }
}
