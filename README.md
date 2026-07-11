# ServerPresets

A Minecraft Fabric mod that pins preset servers to the top of your multiplayer server list. These servers are locked and cannot be edited, deleted, or moved by players.

## Features

- Load preset servers from a configuration file
- Preset servers are always pinned to the top of the server list
- Preset servers cannot be edited, deleted, or moved
- Automatic deduplication: duplicate servers with the same IP are removed
- Players can freely manage their own servers (but cannot move them above preset servers)
- Configure server resource pack policy (Prompt/Enabled/Disabled)

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.26.1.2
2. Download the latest release from [Releases](https://github.com/Null-K/ServerPresets/releases)
3. Place the mod jar file in your `.minecraft/mods` folder
4. Launch the game

## Usage

1. Launch the game with the mod installed
2. The mod will automatically create `config/serverpresets.json`
3. Edit the configuration file to add your preset servers
4. Restart the game to see the preset servers at the top of your multiplayer list

## Configuration

Configuration file location: `config/serverpresets.json`

### Example Configuration

```json
{
  "presetServers": [
    {
      "name": "Example Server 1",
      "ip": "mc.example.com",
      "resourcePackPolicy": "PROMPT"
    },
    {
      "name": "Example Server 2",
      "ip": "play.example.net:25565",
      "resourcePackPolicy": "ENABLED"
    }
  ]
}
```

### Configuration Fields

- `name`: Server display name
- `ip`: Server address (can include port)
- `resourcePackPolicy`: Server resource pack behavior
  - `PROMPT`: Ask the player (default)
  - `ENABLED`: Always accept resource packs
  - `DISABLED`: Always decline resource packs

## Technical Details

This mod implements its features through Mixins that intercept:

- **ServerListMixin**: Server list loading, saving, deletion, replacement, and swapping operations
- **JoinMultiplayerScreenMixin**: Edit/delete button states and callback functions
- **ServerSelectionListEntryMixin**: Move button clicks and position validation

## Building from Source

### Requirements

- Java 25
- Gradle 9.5+

### Build Steps

```bash
./gradlew clean build
```

The built jar file will be located in `build/libs/ServerPresets-1.0.0.jar`

## Compatibility

- **Minecraft**: 26.1.2
- **Fabric Loader**: 0.19.3+
- **Java**: 25+

## License

MIT
