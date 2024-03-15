# Discord Commit Companion Bot

The **Discord Commit Companion Bot** is a Discord bot designed to provide real-time notifications of commits made to GitHub repositories. It allows users to configure specific GitHub repositories they wish to monitor and receive notifications directly in Discord channels.

## Features

- **Real-time Commit Notifications**: Receive instant notifications in Discord channels whenever commits are made to configured GitHub repositories.
- **Customizable Settings**: Configure which GitHub repositories to monitor and where to send notifications within Discord.
- **Interactive Commands**: Use interactive commands to manage bot settings, add/remove repositories, and more.

## Installation

To run the Discord Commit Companion Bot, follow these steps:

1. Clone this repository to your local machine.
2. Ensure you have Java 17 or later installed.
3. Set up a Discord bot application and obtain a bot token.
4. Set up a webhook for your GitHub repository pointing to `<bot_base_url>/webhook`.
5. Configure your bot settings in the `application.properties` file.
6. Build the project using Gradle: `./gradlew build`.
7. Run the bot application: `java -jar discord-commit-companion-bot.jar`.

## Usage

1. Invite the bot to your Discord server using the bot invitation link generated when setting up the bot application.
2. Use the `!help` command to view available commands and their usage instructions.
3. Use the `!setCommitCompanionChannel` command to specify the channel where the bot will send commit notifications.
4. Use the `!watchRepository [Repository URL]` command to add a GitHub repository to the bot's watch list.
5. Receive real-time notifications in the configured Discord channel whenever commits are made to the monitored repositories.

## Contributing

Contributions to the Discord Commit Companion Bot are welcome! If you'd like to contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature-name`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to the branch (`git push origin feature/your-feature-name`).
6. Create a new Pull Request.
