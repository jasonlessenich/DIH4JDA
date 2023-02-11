package xyz.dynxsty.dih4jda.interactions.commands.text;

import java.util.Arrays;

public class TextOptionMapping {
    private final TextOptionData optionData;
    private final String[] args;
    private final int index;

    public TextOptionMapping(TextOptionData optionData, String[] args, int index) {
        this.optionData = optionData;
        this.args = args;
        this.index = index;
    }

    public TextOptionData getOptionData() {
        return optionData;
    }

    public TextOptionType getType() {
        return getOptionData().getType();
    }

    public String getAsString() {
        return args[index];
    }

    public String getAsMultiString() {
        return String.join(" ", Arrays.copyOfRange(args, index, args.length));
    }
}
