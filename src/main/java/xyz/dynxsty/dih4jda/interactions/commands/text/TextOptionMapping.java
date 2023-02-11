package xyz.dynxsty.dih4jda.interactions.commands.text;

public class TextOptionMapping {
    private final TextOptionData optionData;
    private final Object value;

    public TextOptionMapping(TextOptionData optionData, Object value) {
        this.optionData = optionData;
        this.value = value;
    }

    public TextOptionData getOptionData() {
        return optionData;
    }

    public String getAsString() {
        return (String) value;
    }

    public String getMultiString() {
        return (String) value;
    }
}
