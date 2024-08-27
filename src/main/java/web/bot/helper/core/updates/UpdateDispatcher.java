package web.bot.helper.core.updates;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

public class UpdateDispatcher {

  public static void UpdateDispatcherSelector(Update update) {
    try {
      if (update.hasMessage() | update.hasCallbackQuery())
      {
        MessageHandler.selector(update);
      }
    }catch(Exception e){
      System.out.println(STR."error al recibir un update: \{Arrays.toString(e.getStackTrace())}");
    }
  }
}
