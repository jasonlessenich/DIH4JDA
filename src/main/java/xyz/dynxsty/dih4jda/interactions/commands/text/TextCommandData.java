package xyz.dynxsty.dih4jda.interactions.commands.text;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class TextCommandData {
    @Getter(AccessLevel.PUBLIC)
    private final String name;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private String description = "";

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private String category = "";

    @Getter(AccessLevel.PUBLIC)
    private String[] aliases = new String[]{};

    @Getter(AccessLevel.PUBLIC)
    private TextOptionData[] options = new TextOptionData[]{};

    public TextCommandData(String name) {
        this.name = name;
    }

    public TextCommandData(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public TextCommandData(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public TextCommandData addOptions(TextOptionData... options) {
        this.options = options;
        return this;
    }

    public TextCommandData addAliases(String... aliases) {
        this.aliases = aliases;
        return this;
    }
}
