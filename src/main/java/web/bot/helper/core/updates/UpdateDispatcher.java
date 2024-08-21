package web.bot.helper.core.updates;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class UpdateDispatcher {

  public static void UpdateDispatcherSelector(Update update) {
    try{
      if(update.hasMessage()){

      } else if (update.hasCallbackQuery()) {

      } else if (update.hasInlineQuery()) {

      }
    } catch (TelegramApiException e){
      System.out.println(e);
    }
  }
}
