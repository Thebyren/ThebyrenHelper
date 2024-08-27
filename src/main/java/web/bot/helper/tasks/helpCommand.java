package web.bot.helper.tasks;

import org.telegram.telegrambots.meta.api.objects.Update;
import web.bot.helper.core.BotStarter;
import web.bot.helper.core.Mensajeria;
import web.bot.helper.core.commands.CommandHandler.command;

public class helpCommand implements command {

  @Override
  public void execute(Update update) {
    String msg = "hola, parece que necesitas ayuda, dejame ayudarte.";
    try {
      BotStarter.Send(Mensajeria.createMsg(Mensajeria.getChatIdMsg(update), msg));
    } catch (Exception e) {
      System.out.println("error ocasionado en execute start command");
      throw new RuntimeException(e);
    }
  }
}
