package web.bot.helper.core;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;

public class Mensajeria {

  public static SendMessage createMsg(long user, String text){
    return SendMessage.builder()
            .chatId(user)
            .text(text)
            .build();
  }

  public static SendPhoto createMsg(long user,String text, InputFile photo){
    return SendPhoto.builder()
            .chatId(user)
            .photo(photo)
            .caption(text)
            .build();
  }
  public static SendMessage createMsgMenu(long user, String text, InlineKeyboardMarkup menu){
    return SendMessage.builder()
            .chatId(user)
            .text(text)
            .replyMarkup(menu)
            .build();
  }
  public static long getChatId(Update update){
    if (update.hasCallbackQuery()){
      return getChatIdClb(update);
    } else{
      return getChatIdMsg(update);
    }
  }
  public static long getChatIdMsg(Update update){
    return update.getMessage().getChatId();
  }
  public static long getChatIdClb(Update update){
    return update.getCallbackQuery().getFrom().getId();
  }
  public static InlineKeyboardMarkup generateKeyboard(String[] options) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> rows = new ArrayList<>();

    // Crear una fila por cada opción
    for (String option : options) {
      List<InlineKeyboardButton> row = new ArrayList<>();
      InlineKeyboardButton button = new InlineKeyboardButton();
      button.setText(option);
      button.setCallbackData(option);
      row.add(button);
      rows.add(row);
    }

    markup.setKeyboard(rows);
    return markup;
  }
}
