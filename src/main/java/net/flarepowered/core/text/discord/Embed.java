package net.flarepowered.core.text.discord;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class Embed {
    private String title;
    private String description;
    private String url;
    private int color;
    private List<EmbedField> fields;
    private Author author;
    private Footer footer;
    private String timestamp;
    private Image image;
    private Thumbnail thumbnail;

    public Embed() {}

    public Embed(String title, String description, String url, int color, List<EmbedField> fields, Author author, Footer footer, String timestamp, Image image, Thumbnail thumbnail) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.color = color;
        this.fields = fields;
        this.author = author;
        this.footer = footer;
        this.timestamp = timestamp;
        this.image = image;
        this.thumbnail = thumbnail;
    }

    public String toPayLoad() {
        return new Gson().toJson(this);
    }

    public Embed setTitle(String title) {
        this.title = title;
        return this;
    }

    public Embed setDescription(String description) {
        this.description = description;
        return this;
    }

    public Embed setUrl(String url) {
        this.url = url;
        return this;
    }

    public Embed setColor(String color) {
        this.color = Integer.parseInt(color.replaceFirst("#", ""), 16) ;
        return this;
    }

    public Embed setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Embed addEmbedField(String name, String value, boolean inline) {
        if(fields == null) fields = new ArrayList<>();
        fields.add(new EmbedField(name, value, inline));
        return this;
    }

    public Embed addAuthor(String name, String url, String icon_url) {
        author = new Author(name, url, icon_url);
        return this;
    }

    public Embed addFooter(String text, String icon_url) {
        footer = new Footer(text, icon_url);
        return this;
    }

    public Embed addImage(String url) {
        image = new Image(url);
        return this;
    }

    public Embed addThumbnail(String url) {
        thumbnail = new Thumbnail(url);
        return this;
    }

    public class EmbedField {
        private final String name;
        private final String value;
        private final boolean inline;

        public EmbedField(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

    }

    public class Author {
        private final String name;
        private final String url;
        private final String icon_url;

        public Author(String name, String url, String icon_url) {
            this.name = name;
            this.url = url;
            this.icon_url = icon_url;
        }
    }

    public class Footer {
        private final String text;
        private final String icon_url;

        public Footer(String text, String icon_url) {
            this.text = text;
            this.icon_url = icon_url;
        }

    }

    public class Image {
        private final String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public class Thumbnail {
        private final String url;

        public Thumbnail(String url) {
            this.url = url;
        }
    }
}