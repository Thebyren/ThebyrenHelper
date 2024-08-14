package web.bot.helper.core;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class OutputServices {
    private final AbsSender bot;

    public OutputServices(AbsSender bot){
        this.bot = bot;
    }

    public void sendText(long user, String content){
        SendMessage sm = SendMessage.builder()
                .chatId(String.valueOf(user))
                .text(content).build();
        try{
            bot.execute(sm);
        }catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }
}
