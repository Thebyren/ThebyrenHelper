package web.bot.helper.core;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public class MessageListener {
    public static Message listener(Update update){
      Message msg = new Message();
      if (update.hasMessage()) {
        handleMessage(update.getMessage());
      } else if (update.hasCallbackQuery()) {
        handleCallbackQuery(update.getCallbackQuery());
      } else if (update.hasInlineQuery()) {
        handleInlineQuery(update.getInlineQuery());
      }
      return msg;
    }
  private static void handleMessage(Message message) {
    // Lógica para manejar mensajes
    long chatId = message.getChatId();
    System.out.println("Mensaje recibido: " + message.getText());
  }

  private static void handleCallbackQuery(CallbackQuery callbackQuery) {
    // Lógica para manejar consultas de callback
    System.out.println("Consulta de callback recibida: " + callbackQuery.getData());
  }

  private static void handleInlineQuery(InlineQuery inlineQuery) {
    // Lógica para manejar consultas en línea
    System.out.println("Consulta en línea recibida: " + inlineQuery.getQuery());
  }


}
