# ServerPresets

A Minecraft Fabric mod that pins preset servers to the top of your multiplayer server list. These servers are locked and cannot be edited, deleted, or moved by players.

## Features

- Load preset servers from a configuration file
- Preset servers are always pinned to the top of the server list
- Preset servers cannot be edited, deleted, or moved
- Automatic deduplication: duplicate servers with the same IP are removed
- Players can freely manage their own servers (but cannot move them above preset servers)
- Configure server resource pack policy (Prompt/Enabled/Disabled)
- Load preset servers from a remote URL (HTTP/HTTPS), with automatic merging of local and remote lists

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
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

## Remote Server List

Preset servers can also be loaded from a remote URL, which is useful for managing the server list across multiple clients from a single source.

### Enabling Remote Loading

Add the following fields to `config/serverpresets.json`:

```json
{
  "presetServers": [
    {
      "name": "Local Server",
      "ip": "localhost",
      "resourcePackPolicy": "PROMPT"
    }
  ],
  "remoteUrl": "https://example.com/servers.json",
  "enableRemote": true
}
```

- `remoteUrl`: The URL of the remote server list (HTTP or HTTPS). Leave empty to disable.
- `enableRemote`: Set to `true` to enable remote loading, `false` to disable (default).

### Remote JSON Format

The remote URL must return JSON in the following format:

```json
{
  "servers": [
    {
      "name": "Remote Server 1",
      "ip": "play.example.net",
      "resourcePackPolicy": "ENABLED"
    },
    {
      "name": "Remote Server 2",
      "ip": "mc.example.com",
      "resourcePackPolicy": "PROMPT"
    }
  ]
}
```

### Behavior

- The mod loads the local config first, then merges the remote list on top of it (local + remote).
- The remote list is fetched once on each game startup.
- If remote loading fails (network error, invalid JSON, etc.), the mod falls back to the local list and startup is not affected.
- Request timeouts: 10s connect, 15s read. HTTP redirects are followed automatically.

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

- **Fabric Loader**: 0.19.3+
- **Java**: 25+

## License

MIT
