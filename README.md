# DIH4JDA

A very easy-to-use interaction handler for JDA!

## Usage

Just create a new `DIH4JDA` instance like this:

```java
DIH4JDA dih4JDA = new DIH4JDABuilder()
        .setJDA(jda)
        .setCommandsPackage("package")
        .setCommandType(SlashCommandType.GUILD)
        .build();
```

It will automatically register all Slash Commands that extend `SlashCommand`.  
A single Slash Command would look something like this:

```java
class PingCommand extends SlashCommand implements ISlashCommand {

    public PingCommand() {
        setCommandData(new CommandData("ping", "Pong!"));
    }

    @Override
    public void handleSlash(SlashCommandEvent event) {
        event.reply("Pong!").queue();
    }
```

All commands that run code on execution should implement the `ISlashCommand` class.

## ToDo

- [ ] Support for Global Slash Commands
- [ ] Support for other Interaction Types, such as Buttons and Selection Menus
- [ ] Proper Support for Command privileges





