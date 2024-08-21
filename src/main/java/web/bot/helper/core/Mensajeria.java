package web.bot.helper.core;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

public class Mensajeria {
  private final AbsSender mensajero;

  public Mensajeria(AbsSender mensajero) {
    this.mensajero = mensajero;
  }

  private void ExecuteMessage(SendMessage smg){
    try{
    mensajero.execute(smg);
    }catch (TelegramApiException e){
      System.out.println("error al ejecutar orden de mensaje: "+ Arrays.toString(e.getStackTrace()));
      System.err.println("detalles de la causa: "+e.getCause());
    }
  }

  public static SendMessage sendText(long user, String text){
    SendMessage smg = SendMessage.builder()
            .chatId(user)
            .text(text)
            .build();
    return smg;
  }
}
