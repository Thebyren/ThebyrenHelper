package web.bot.helper.core.updates;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import web.bot.helper.core.commands.CommandHandler;

import java.util.Arrays;

public class MessageHandler {
  public static void selector(Update update){
    Message msg = update.getMessage();
    try{
      if(msg.isCommand()){
        CommandHandler.selector(msg);
      }
    }catch (Exception e){
      System.err.println("error al identificar el tipo de mensaje: "+ Arrays.toString(e.getStackTrace()));
    }
  }
  private  class text_message extends Message{

  }
}
