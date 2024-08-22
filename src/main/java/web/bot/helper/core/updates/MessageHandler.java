package web.bot.helper.core.updates;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import web.bot.helper.core.Mensajeria;
import web.bot.helper.core.commands.CommandHandler;

import java.util.Arrays;

public class MessageHandler {
  public static void selector(Update update) {
    long chatId = Mensajeria.getChatIdMsg(update);
    try {
      if (CommandHandler.userContexts.containsKey(chatId)) {
        CommandHandler.userContexts.get(chatId).processUpdate(update);
      } else {
        if (update.getMessage().isCommand()) {
          CommandHandler.selector(update);
        }
      }
    } catch (TelegramApiException e) {
      System.err.println("error al identificar el tipo de mensaje: " + Arrays.toString(e.getStackTrace()));
    }
  }
}
