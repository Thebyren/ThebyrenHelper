package web.bot.helper.tasks;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import web.bot.helper.core.BotStarter;
import web.bot.helper.core.Mensajeria;
import web.bot.helper.core.commands.CommandHandler;
import web.bot.helper.core.commands.CommandHandler.command;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.io.File;

public class Stenography implements command {
  @Override
  public void execute(Update update) {
    String msg = "hola, manda tu foto y el texto que quieras encriptar en la foto para que inicie el proceso";
    long chatId = Mensajeria.getChatIdMsg(update);
    try {
      BotStarter.Send(Mensajeria.createMsg(chatId, msg));
      CommandHandler.userContexts.put(chatId, new StenographyContext());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class StenographyContext implements CommandHandler.CommandContext {
    @Override
    public void processUpdate(Update update) throws Exception {
      Message msg = update.getMessage();
      long chatId = Mensajeria.getChatIdMsg(update);
      InputFile photo;
      if (msg.hasPhoto()) {
        String response = "Foto recibida. procesando...";
        List<PhotoSize> photos = msg.getPhoto();
        PhotoSize largestPhoto = photos.get(photos.size() - 1);
        photo = new InputFile(largestPhoto.getFileId());
        try {
          BotStarter.Send(Mensajeria.createMsg(chatId, response));
        } catch (TelegramApiException e) {
          throw new RuntimeException(e);
        }

        response = msg.getCaption();
        stenographyAlgorith(getBufferedImageFromInputFile(photo), response);

        try {
          BotStarter.Send(Mensajeria.createMsg(chatId, response, photo));
        } catch (TelegramApiException e) {
          throw new RuntimeException(e);
        }
        CommandHandler.userContexts.remove(chatId);
      } else {
        try {
          BotStarter.Send(Mensajeria.createMsg(chatId, "no contienes foto y texto"));
        } catch (TelegramApiException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private static BufferedImage getBufferedImageFromInputFile(InputFile inputFile) throws Exception {
    // Obtiene el file_id del InputFile
    String fileId = inputFile.getMediaName();
    GetFile getFile = new GetFile();
    getFile.setFileId(fileId);

    // Descarga el archivo de Telegram
    org.telegram.telegrambots.meta.api.objects.File file = BotStarter.getInstance().execute(getFile);
    String filePath = file.getFilePath();
    URL fileUrl = new URL("https://api.telegram.org/file/bot" + BotStarter.getInstance().getBotToken() + "/" + filePath);

    try (InputStream inputStream = fileUrl.openStream()) {
      BufferedImage bufferedImage = ImageIO.read(inputStream);
      return bufferedImage;
    }
  }
  public static InputFile bufferedImageToInputFile(BufferedImage bufferedImage, String fileName) throws IOException {
    // Crea un archivo temporal para guardar la imagen
    File tempFile = File.createTempFile(fileName, ".png");
    try (FileOutputStream fos = new FileOutputStream(String.valueOf(tempFile))) {
      // Guarda la imagen en el archivo temporal
      ImageIO.write(bufferedImage, "png", fos);
    }
    // Crea un InputFile con el archivo temporal
    return new InputFile(String.valueOf(tempFile));
  }
  private static InputFile stenographyAlgorith(BufferedImage bImage, String msg){
    InputFile file = null;
    String binaryMsg = msg.getBytes().toString();

    int index = 0;

    for(int y = 0; y<bImage.getHeight(); y++){
      for(int x =0; x< bImage.getWidth(); x++){
        int rgb = bImage.getRGB(x,y);
        int r = (rgb>>16)& 0xff;
        int g = (rgb>>8)& 0xff;
        int b = rgb & 0xff;

        r = setLSB(r, binaryMsg.charAt(index));
        g = setLSB(g, binaryMsg.charAt(index+1));
        b = setLSB(b, binaryMsg.charAt(index+2));
        index+=3;

        rgb = (r<<16)| (g<<8)|b;
        bImage.setRGB(x,y,rgb);
        if(index>= binaryMsg.length()){
          break;
        }
      }

    }

    return file;
  }
  private static int setLSB(int pixel, char bit) {
    if (bit == '1') {
      return pixel | 1;
    } else {
      return pixel & ~1;
    }
  }
}
