package web.bot.helper.core;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import web.bot.helper.core.updates.UpdateDispatcher;

import java.lang.reflect.Method;

public class BotStarter extends TelegramLongPollingBot {
  private final String botUsername;
  private final String botToken;
  public Mensajeria mensajero;
  private static BotStarter instance;

  public BotStarter(){
    Dotenv dotenv = Dotenv.load();
    this.botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
    this.botUsername = dotenv.get("TELEGRAM_USER_NAME");
  }
  public static BotStarter getInstance() {
    if (instance == null) {
      instance = new BotStarter();
    }
    return instance;
  }
  public static void Send(SendMessage msg) throws TelegramApiException {
    getInstance().execute(msg);
  }
  public static void Send(SendPhoto photo) throws  TelegramApiException{
    getInstance().execute(photo);
  }

  @Override
  public void onUpdateReceived(Update update) {
   UpdateDispatcher.UpdateDispatcherSelector(update);
  }
  @Override
  public String getBotUsername() {
    return botUsername;
  }
  @Override
  public String getBotToken(){
    return botToken;
  }

}
