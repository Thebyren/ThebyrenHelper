package web.bot.helper.core.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import web.bot.helper.core.BotStarter;
import web.bot.helper.core.Mensajeria;
import web.bot.helper.tasks.Stenography;
import web.bot.helper.tasks.helpCommand;
import web.bot.helper.tasks.startCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandHandler {
  static Map<String, command> commandMap = new HashMap<>();
  public static Map<Long, CommandContext> userContexts = new HashMap<>();

  static {
    commandMap.put("/start", new startCommand());
    commandMap.put("/stenography", new Stenography());
    commandMap.put("/help", new helpCommand());
  }


  public static void selector(Update update) throws Exception {
    long idMsg = Mensajeria.getChatIdMsg(update);
    CommandContext context = userContexts.get(idMsg);
    String commandString = update.getMessage().getText();
    if (context != null) {
      // Manejo del comando /cancelar
      if (Objects.equals(commandString, "/cancelar")) {
        userContexts.remove(idMsg);
        BotStarter.Send(Mensajeria.createMsg(idMsg, "Operación cancelada. Puedes comenzar de nuevo."));
      } else {
        // Continuar procesando dentro del contexto actual
        context.processUpdate(update);
      }
    } else {
      // Si no hay un contexto activo, procesar el comando como de costumbre
      command command = commandMap.get(commandString);

      if (command != null) {
        // Ejecutar el comando
        command.execute(update);
      } else if (Objects.equals(commandString, "/cancelar")) {
        BotStarter.Send(Mensajeria.createMsg(idMsg, "No tienes un proceso que cancelar en este momento."));
      } else {
        // Comando no reconocido
        BotStarter.Send(Mensajeria.createMsg(idMsg, "Comando no reconocido, para más ayuda revisa /help"));
      }
    }
  }

  public interface CommandContext<S extends Enum<S>>{
    void setState(S state);
    S getState();
    void processUpdate(Update update) throws Exception;
    void cancel(Update update);
  }

  public interface command {
    void execute(Update update);
  }
}
