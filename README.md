## What is FeatherChat?
FeatherChat is a chat channel plugin designed to be highly customizable by all users. Players are allowed to create their own chat channels, invite other players, and chat with them in private.

* **FeatherChat requires Spigot to run.** FeatherChat is neither tested nor supported on other platforms, but Paper support is planned for the future.
* **FeatherChat only supports Minecraft 1.19.2.** FeatherChat utilizes the newest chat features from 1.19.2, and as such, that is the only version currently supported. There will be support for future versions of Minecraft, but there will be no official support for any versions prior to 1.19.2.
* **FeatherChat requires Java 17 or higher.** Java 17 is the minimum Java version required to build and run FeatherChat on Spigot 1.19.2.


## Building
To clone and build FeatherChat, you need Git and Java 17 or higher installed.
#### Compiling from source
```sh
git clone https://github.com/Rabbitual/FeatherChat.git
cd FeatherChat
./gradlew build
```
All platform jars will be output to the "jars" folder.

## Commands
* `/featherchat`
  - `displayname <displayname>` - Sets your own display name.
  - `channel`
    - `create <name>` - Creates a new channel with the specified name.
    - `chat <channel> <message>` - Sends a message to the specified channel.
    - `invite <channel> <player>` - Invites a player to a specified channel.
    - `join <channel>` - Accept a pending invite to the specified channel.
    - `deny <channel>` - Deny a pending invite to the specified channel.
    - `info <channel>` - Display info about the specified channel.
    - `displayname <channel> <displayname>` - Changes the display name of the specified channel.
  - `debug`
    - `chat <message>` - Sends a message to the debug channel.
    - `format <format>` - Sets the message format of the debug channel.
    - `displayname <displayname>` - Sets the display name of the debug channel.
  - `version` - Displays version info about this plugin.


## Contributing
If you like the project and are a developer yourself, consider supporting the project by making a PR for any bug fixes, improvements, or new features that you would like to see added.
