# SigmaLandsExtension

A Bukkit/Spigot plugin that extends the Lands plugin to manage zone permissions and access control.

## Features

- 🔍 **Automatic Area Detection**: Detects when players enter Lands areas using the Lands API
- 🔐 **Permission-Based Access Control**: Restricts access based on required permissions
- ⚙️ **Configurable Zones**: Easy configuration through YAML files
- 🐛 **Debug Logging**: Comprehensive logging for troubleshooting
- 🚫 **Movement Blocking**: Prevents unauthorized entry to restricted areas

## Dependencies

- **Lands Plugin**: Required for area detection and management
- **Spigot/Paper**: Bukkit API implementation
- **Java 17+**: Required for compilation

## Installation

1. Ensure you have the Lands plugin installed and running
2. Download the latest release from the releases page
3. Place the JAR file in your server's `plugins` folder
4. Restart your server
5. Configure the `config.yml` file with your desired zones and permissions

## Configuration

The plugin uses a hierarchical configuration system:

```yaml
restricted-areas:
  LandName:
    AreaName:
      - permission.required
      - another.permission
```

### Example Configuration

```yaml
restricted-areas:
  MembersZone:
    zone1:
      - youtube.premium1
    zone2:
      - youtube.premium1
      - youtube.premium2
    zone3:
      - youtube.premium1
      - youtube.premium2
      - youtube.premium3
```

## How It Works

1. **Area Detection**: The plugin listens for player movement events
2. **Lands Integration**: Uses the Lands API to detect which area the player is entering
3. **Permission Check**: Retrieves required permissions for that area from the configuration
4. **Access Control**: Blocks movement if the player lacks any required permissions
5. **User Feedback**: Sends a message to the player if access is denied

## Building from Source

1. Clone this repository
2. Run `mvn clean package` to build the plugin
3. The compiled JAR will be in `target/` directory

## API Usage

The plugin provides access to the Lands API through the main plugin class:

```java
SigmaLandsExtension plugin = SigmaLandsExtension.getInstance();
LandsIntegration landsAPI = plugin.getLandsAPI();
ZoneManager zoneManager = plugin.getZoneManager();
```

## Permissions

The plugin doesn't define any specific permissions - it uses your existing permission system. Players need the permissions defined in the configuration to access restricted areas.

## Troubleshooting

- **Plugin not loading**: Ensure Lands plugin is installed and enabled
- **Areas not detected**: Check that the Lands areas are properly claimed
- **Permissions not working**: Verify the permission names in your config match your permission plugin
- **Debug information**: Enable debug logging in the config to see detailed information

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, please open an issue on the GitHub repository or contact the development team.
