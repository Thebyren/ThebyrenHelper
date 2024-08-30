package web.bot.helper.core.updates;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import web.bot.helper.core.BotStarter;
import web.bot.helper.core.Mensajeria;
import web.bot.helper.core.commands.CommandHandler;
import web.bot.helper.core.commands.CommandHandler.CommandContext;

import java.util.Arrays;

public class MessageHandler {
  public static void selector(Update update) {
    long chatId = Mensajeria.getChatId(update);
    try {
      if (CommandHandler.userContexts.containsKey(chatId)) {
        try {
          var context = CommandHandler.userContexts.get(chatId);
          if(!cancelCommand(update, context)){
            context.processUpdate(update);
          }
        } catch (Exception e) {
          System.out.println("error al ejecutar contexto" + Arrays.toString(e.getStackTrace()));
        }
      } else {
        if (update.getMessage().isCommand()) {
          CommandHandler.selector(update);
        }
      }
    } catch (TelegramApiException e) {
      System.err.println("error al identificar el tipo de mensaje: " + Arrays.toString(e.getStackTrace()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean cancelCommand(Update update, CommandContext context) throws TelegramApiException {
    if (update.hasMessage()) {
      if (update.getMessage().isCommand()) {
        if(update.getMessage().getText().startsWith("/cancelar")){
        context.cancel(update);
          BotStarter.Send(Mensajeria.createMsg(Mensajeria.getChatId(update), "se cancelo el proceso actual."));
          return true;
        }
      }
    }
    return false;
  }
}
