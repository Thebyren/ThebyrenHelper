package web.bot.helper;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import web.bot.helper.core.BotStarter;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botRegister = new TelegramBotsApi(DefaultBotSession.class);
        BotStarter bot = new BotStarter();
        botRegister.registerBot(bot);
    }
}
