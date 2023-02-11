package xyz.dynxsty.dih4jda.interactions.commands.text;

public class TextOptionData {
    public TextOptionData(TextOptionType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public TextOptionData(TextOptionType type, String name, String description, boolean required) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
    }

    private final TextOptionType type;
    private final String name;
    private final String description;
    private boolean required = false;

    public TextOptionType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }
}
