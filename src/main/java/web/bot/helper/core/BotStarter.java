package web.bot.helper.core;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.objects.Update;
import web.bot.helper.core.updates.UpdateDispatcher;

public class BotStarter extends TelegramLongPollingBot {
  private final String botUsername;
  private final String botToken;
  public Mensajeria mensajero;

  public BotStarter(){
    Dotenv dotenv = Dotenv.load();
    this.botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
    this.botUsername = dotenv.get("TELEGRAM_USER_NAME");
    mensajero = new Mensajeria(this);
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
