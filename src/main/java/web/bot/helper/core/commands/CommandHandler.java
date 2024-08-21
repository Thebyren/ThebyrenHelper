package web.bot.helper.core.commands;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.telegram.telegrambots.meta.api.objects.Message;

public class CommandHandler {
  public static void selector(Message comm){
    String commmand = comm.getText().toLowerCase().substring(1);
    String jsonString;
  }
}
