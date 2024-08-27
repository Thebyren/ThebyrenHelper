package web.bot.helper.tasks;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import web.bot.helper.core.BotStarter;
import web.bot.helper.core.Mensajeria;
import web.bot.helper.core.commands.CommandHandler;
import web.bot.helper.core.commands.CommandHandler.command;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Stenography implements command {
  @Override
  public void execute(Update update) {
    String msg = "¡Bienvenido a la seccion de estenografía!   Aquí podrás encriptar y desencriptar mensajes dentro de imágenes. ¡Comencemos!";
    long chatId = Mensajeria.getChatIdMsg(update);
    try {
      BotStarter.Send(Mensajeria.createMsg(chatId, msg));
      CommandHandler.userContexts.put(chatId, new StenographyContext());
      InlineKeyboardMarkup menu = Mensajeria.generateKeyboard(new String[]{"encriptar","desencriptar"});
      BotStarter.Send(Mensajeria.createMsgMenu(chatId, "¿Quieres encriptar o desencriptar un mensaje?",menu));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class StenographyContext implements CommandHandler.CommandContext<StenographyContext.StenographyState> {
    private InputFile photo;
    public InputFile getPhoto() {
      return this.photo;
    }
    public void setPhoto(InputFile photo) {
      this.photo = photo;
    }

    public enum StenographyState {
      INITIAL,
      WAITING_FOR_PHOTO_ENCODE,
      WAITING_FOR_PHOTO_DECODE,
      WAITING_FOR_TEXT,
      PROCESSING,
      COMPLETED,
      CANCEL,
      DEFAULT
    }
    private StenographyState state = StenographyState.INITIAL;

    @Override
    public void setState(StenographyState state) {

    }

    @Override
    public StenographyState getState() {
      return state;
    }
    @Override
    public void processUpdate(Update update) throws Exception {
      Message msg = update.getMessage();
      long chatId;

      chatId = Mensajeria.getChatId(update);
      System.out.println(STR."estado actual: \{state.toString()}");
      switch (state){
        case INITIAL:
          try {
            if (update.hasCallbackQuery()) {
              if (Objects.equals(update.getCallbackQuery().getData(), "encriptar")) {
                BotStarter.Send(Mensajeria.createMsg(chatId, "Ok, manda la foto en la que desas ocultar tu mensaje"));
                state = StenographyState.WAITING_FOR_PHOTO_ENCODE;
              }
              if (Objects.equals(update.getCallbackQuery().getData(), "desencriptar")) {
                BotStarter.Send(Mensajeria.createMsg(chatId, "Ok, manda la foto en la que desas ocultar tu mensaje"));
                state = StenographyState.WAITING_FOR_PHOTO_DECODE;
              }
            }
          }catch (Exception e){
            System.out.println("ocurrio un error al manejar el callbackquery "+ Arrays.toString(e.getStackTrace()));
          }
        case WAITING_FOR_PHOTO_ENCODE:
          String response;
          try{
          if(msg!=null && msg.hasPhoto()){
            response = "foto recibida procesando...";
            List<PhotoSize> photos = msg.getPhoto();
            if(photos!=null && !photos.isEmpty()) {
              PhotoSize largestPhoto = photos.getLast();
              String fileId = largestPhoto.getFileId();
            setPhoto( new InputFile(fileId));
            if(getPhoto()!=null){
              System.out.println(STR."foto almacenada con id:\{fileId}");
            }else{
              System.err.println("foto es nula");
            }
            }else{
              System.err.println("error, la lista esta vacia.");
            }
            state = StenographyState.WAITING_FOR_TEXT;
          }else {
            response="no hemos recibido ninguna foto, envia una.";
          }
          }catch (Exception e){
            response="No hemos recibido ninguna foto, hubo un error";
            System.out.println(STR."ERROR al procesar la foto\{Arrays.toString(e.getStackTrace())}");
          }
          BotStarter.Send(Mensajeria.createMsg(chatId,response));
          break;
        case WAITING_FOR_PHOTO_DECODE:
          System.out.println("hola decodificador");
          break;
        case WAITING_FOR_TEXT:
          System.out.println(STR."hola\{getPhoto()}");
          break;
        case PROCESSING:
          break;
        case COMPLETED:
          break;
        case CANCEL:
          break;
        case DEFAULT:
          try {
            BotStarter.Send(Mensajeria.createMsg(chatId, "no contienes foto y texto"));
          } catch (TelegramApiException e) {
            throw new RuntimeException(e);
          }
      }

      /*
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
        photo = stenographyAlgorith(photo, response);

        try {
          BotStarter.Send(Mensajeria.createMsg(chatId, response, photo));
        } catch (TelegramApiException e) {
          throw new RuntimeException(e);
        }
        CommandHandler.userContexts.remove(chatId);


      }

      */

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
    URL fileUrl = new URL(STR."https://api.telegram.org/file/bot\{BotStarter.getInstance().getBotToken()}/\{filePath}");

    try (InputStream inputStream = fileUrl.openStream()) {
      return ImageIO.read(inputStream);
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

  private static InputFile stenographyAlgorith(
          InputFile Image, String msg) throws Exception {
    String name = Image.getMediaName();
    BufferedImage bImage = getBufferedImageFromInputFile(Image);
    StringBuilder binaryMsg = new StringBuilder();
    for (byte b : msg.getBytes()) {
      binaryMsg.append(String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0'));
    }

    int index = 0;
    outerLoop:
    for (int y = 0; y < bImage.getHeight(); y++) {
      for (int x = 0; x < bImage.getWidth(); x++) {
        int rgb = bImage.getRGB(x, y);
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        r = setLSB(r, binaryMsg.charAt(index));
        g = setLSB(g, binaryMsg.charAt(index + 1));
        b = setLSB(b, binaryMsg.charAt(index + 2));
        index += 3;

        rgb = (r << 16) | (g << 8) | b;
        bImage.setRGB(x, y, rgb);
        if (index >= binaryMsg.length()) {
          break outerLoop;
        }
      }

    }
    try {
      return bufferedImageToInputFile(bImage, name);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static int setLSB(int pixel, char bit) {
    return (bit == '1') ? (pixel | 1) : (pixel & ~1);
  }

  private static char getLSB(int pixel) {
    return (pixel & 1) == 0 ? '0' : '1';
  }

  private static String recoverMessageFromImage(InputFile image) throws Exception {
    BufferedImage bImage = getBufferedImageFromInputFile(image);
    StringBuilder binaryMsg = new StringBuilder();

    for (int y = 0; y < bImage.getHeight(); y++) {
      for (int x = 0; x < bImage.getWidth(); x++) {
        int rgb = bImage.getRGB(x, y);
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        // Recuperar los bits menos significativos
        binaryMsg.append(getLSB(r));
        binaryMsg.append(getLSB(g));
        binaryMsg.append(getLSB(b));
      }
    }

    // Convertir la cadena binaria en un mensaje legible
    return binaryToString(binaryMsg.toString());
  }

  private static String binaryToString(String binary) {
    StringBuilder message = new StringBuilder();
    for (int i = 0; i + 8 <= binary.length(); i += 8) {
      String byteStr = binary.substring(i, i + 8);
      char character = (char) Integer.parseInt(byteStr, 2);
      message.append(character);
    }
    return message.toString();
  }
}
