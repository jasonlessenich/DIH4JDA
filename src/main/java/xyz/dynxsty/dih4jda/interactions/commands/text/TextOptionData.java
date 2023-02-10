package xyz.dynxsty.dih4jda.interactions.commands.text;

public class TextOptionData {
    public TextOptionData(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public TextOptionData(String name, String description, boolean required) {
        this.name = name;
        this.description = description;
        this.required = required;
    }

    private final String name;
    private final String description;

    private String defaultValue;
    private String[] allowedValues = new String[]{};
    private boolean required;

    public TextOptionData setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public TextOptionData setAllowedValues(String... allowedValues) {
        this.allowedValues = allowedValues;
        return this;
    }

    public TextOptionData setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String[] getAllowedValues() {
        return allowedValues;
    }

    public boolean isRequired() {
        return required;
    }
}
