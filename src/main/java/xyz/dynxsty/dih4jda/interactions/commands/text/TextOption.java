package xyz.dynxsty.dih4jda.interactions.commands.text;

public class TextOption {
    public TextOption(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public TextOption(String name, String description, boolean required) {
        this.name = name;
        this.description = description;
        this.required = required;
    }

    private final String name;
    private final String description;

    private String defaultValue;
    private String[] allowedValues = new String[]{};
    private boolean required;

    public TextOption setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public TextOption setAllowedValues(String... allowedValues) {
        this.allowedValues = allowedValues;
        return this;
    }

    public TextOption setRequired(boolean required) {
        this.required = required;
        return this;
    }
}
