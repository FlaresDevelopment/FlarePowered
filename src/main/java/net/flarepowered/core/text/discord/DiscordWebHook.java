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

    public void sendMessage(String content, String username, String avatarUrl) {
        JsonObject jsonObject = JsonParser.parseString("{\"content\": null, \"embeds\": []}").getAsJsonObject();
        if(username != null) jsonObject.addProperty("username", username);
        if(avatarUrl != null) jsonObject.addProperty("avatar_url", avatarUrl);
        jsonObject.addProperty("content", content);
        Utility.post(discordWebHook, jsonObject.toString());
    }

    public void sendEmbed(Embed embed, String username, String avatarUrl) {
        JsonObject jsonObject = JsonParser.parseString("{\"content\": null, \"embeds\": []}").getAsJsonObject();
        if(username != null) jsonObject.addProperty("username", username);
        if(avatarUrl != null) jsonObject.addProperty("avatar_url", avatarUrl);
        jsonObject.getAsJsonArray("embeds").add(new Gson().toJsonTree(embed));
        Utility.post(discordWebHook, jsonObject.toString());
    }

    public String getJsonMessage(String content, String username, String avatarUrl) {
        JsonObject jsonObject = JsonParser.parseString("{\"content\": null, \"embeds\": []}").getAsJsonObject();
        if(username != null) jsonObject.addProperty("username", username);
        if(avatarUrl != null) jsonObject.addProperty("avatar_url", avatarUrl);
        jsonObject.addProperty("content", content);
        return jsonObject.toString();
    }

    public String getJsonEmbed(Embed embed, String username, String avatarUrl) {
        JsonObject jsonObject = JsonParser.parseString("{\"content\": null, \"embeds\": []}").getAsJsonObject();
        if(username != null) jsonObject.addProperty("username", username);
        if(avatarUrl != null) jsonObject.addProperty("avatar_url", avatarUrl);
        jsonObject.getAsJsonArray("embeds").add(new Gson().toJsonTree(embed));
        return jsonObject.toString();
    }

    public void sendJson(String json) {
        Utility.post(discordWebHook, json);
    }

}
