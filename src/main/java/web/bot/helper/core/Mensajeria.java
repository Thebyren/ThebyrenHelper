package web.bot.helper.core;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.awt.*;

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
  public static long getChatIdMsg(Update update){
    return update.getMessage().getChatId();
  }
}
