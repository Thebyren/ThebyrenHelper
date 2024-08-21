package web.bot.helper.core.updates;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

public class UpdateDispatcher {

  public static void UpdateDispatcherSelector(Update update) {
    try {
      if (update.hasMessage())
      {
        MessageHandler.selector(update);
      }
      else if (update.hasCallbackQuery())
      {
        CallbackHandler.selector(update);
      }
      else if (update.hasInlineQuery())
      {
        InlineHandler.selector(update);
      }
    }catch(Exception e){
      System.out.println("error al recibir un update: "+ Arrays.toString(e.getStackTrace()));
    }
  }
}
