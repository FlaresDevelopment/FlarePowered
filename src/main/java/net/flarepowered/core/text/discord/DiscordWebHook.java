package net.flarepowered.core.text.discord;

import com.google.gson.*;
import net.flarepowered.utils.Utility;

import java.net.MalformedURLException;
import java.net.URL;

public class DiscordWebHook {

    private final URL discordWebHook;

    public DiscordWebHook(final String url) throws MalformedURLException {
        this.discordWebHook = new URL(url);
    }

    public void sendMessage(String content) {
        JsonObject jsonObject = JsonParser.parseString("{\"content\": null, \"embeds\": []}").getAsJsonObject();
        jsonObject.addProperty("content", content);
        Utility.post(discordWebHook, jsonObject.toString());
    }

    public void sendEmbed(Embed embed) {
        JsonObject jsonObject = JsonParser.parseString("{\"content\": null, \"embeds\": []}").getAsJsonObject();
        jsonObject.getAsJsonArray("embeds").add(new Gson().toJsonTree(embed));
        Utility.post(discordWebHook, jsonObject.toString());
    }

}
