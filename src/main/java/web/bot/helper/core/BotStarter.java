package web.bot.helper.core;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotStarter extends TelegramLongPollingBot {
  private final String botUsername;
  private final String botToken;
  public  OutputServices outputServices;

  public BotStarter(){
    Dotenv dotenv = Dotenv.load();
    this.botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
    this.botUsername = dotenv.get("TELEGRAM_USER_NAME");
    outputServices = new OutputServices(this);
  }

  @Override
  public void onUpdateReceived(Update update) {
    long userId = getUserId(update);
    if(
            update.hasMessage())
    {
      if (update.getMessage().hasText()) {
        Message msg = update.getMessage();

        if (msg.getText().charAt(0) == '/') {
          outputServices.sendText(userId, "parece que esto es un comando");
        }

        if (msg.getText().toLowerCase().contains("hola")) {
          outputServices.sendText(userId, "hola como estas");
        }
      }
      if (update.getMessage().hasPhoto()){
        outputServices.sendText(userId, "parece que esto es una imagen xd");
      }
    }
  }

  @Override
  public String getBotUsername() {
    return botUsername;
  }
  @Override
  public String getBotToken(){
    return botToken;
  }
  public long getUserId(Update update) {
    if (update.hasMessage() && update.getMessage().getFrom() != null) {
      return update.getMessage().getFrom().getId();
    }
    else if (update.hasCallbackQuery() && update.getCallbackQuery().getFrom() != null) {
      return update.getCallbackQuery().getFrom().getId();
    } else if (update.hasInlineQuery()) {
      return update.getInlineQuery().getFrom().getId();
    } else if (update.hasPollAnswer() && update.getPollAnswer().getUser() != null) {
      return update.getPollAnswer().getUser().getId();
    }else{
      return 0;
    }

  }
}
