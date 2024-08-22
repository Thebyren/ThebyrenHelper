package web.bot.helper.tasks;

import org.telegram.telegrambots.meta.api.objects.Update;
import web.bot.helper.core.BotStarter;
import web.bot.helper.core.Mensajeria;
import web.bot.helper.core.commands.CommandHandler;

public class startCommand implements CommandHandler.command {
  @Override
  public void execute(Update update){
    String msg = "hola como estas, este es el proposito del bot";
    try{
      BotStarter.Send(Mensajeria.createMsg(Mensajeria.getChatIdMsg(update),msg));
    } catch (Exception e) {
      System.out.println("error ocasionado en execute start command");
      throw new RuntimeException(e);
    }
  }
}