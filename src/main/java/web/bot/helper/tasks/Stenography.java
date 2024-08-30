package web.bot.helper.tasks;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.*;
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
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

public class Stenography implements command {

  @Override
  public void execute(Update update) {
    String msg = "¡Bienvenido a la seccion de estenografía!   Aquí podrás encriptar y desencriptar mensajes dentro de imágenes [png, ]. ¡Comencemos!";
    long chatId = Mensajeria.getChatIdMsg(update);
    try {
      BotStarter.Send(Mensajeria.createMsg(chatId, msg));
      CommandHandler.userContexts.put(chatId, new StenographyContext());
      InlineKeyboardMarkup menu = Mensajeria.generateKeyboard(new String[]{"ocultar", "revelar"});
      BotStarter.Send(Mensajeria.createMsgMenu(chatId, "¿Quieres ocultar o revelar un mensaje?", menu));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class StenographyContext implements CommandHandler.CommandContext<StenographyContext.StenographyState> {
    private InputFile photo;

    public InputFile getPhoto() {
      return this.photo;
    }

    public void setPhoto(Document photo) {
      this.photo = new InputFile(photo.getFileId());

    }

    public void cancel(Update update) {
      CommandHandler.userContexts.remove(Mensajeria.getChatId(update));
    }

    public enum StenographyState {
      INITIAL,
      WAITING_FOR_PHOTO_ENCODE,
      PROCESSING_ENCODE,
      PROCESSING_DECODE,
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
      switch (state) {
        case INITIAL:
          try {
            if (update.hasCallbackQuery()) {
              if (Objects.equals(update.getCallbackQuery().getData(), "ocultar")) {
                BotStarter.Send(Mensajeria.createMsg(chatId, "Ok, manda la foto en la que desas ocultar tu mensaje"));
                state = StenographyState.WAITING_FOR_PHOTO_ENCODE;
              }
              if (Objects.equals(update.getCallbackQuery().getData(), "revelar")) {
                BotStarter.Send(Mensajeria.createMsg(chatId, "Ok, manda la foto en la que ocultaste tu mensaje"));
                state = StenographyState.PROCESSING_DECODE;
              }

              BotStarter.Send(Mensajeria.createMsg(chatId,"no olvides que tienes que desactivar la compresion para que el proceso funcione correctamente"));
            }
          } catch (Exception e) {
            System.out.printf("ocurrio un error al manejar el callbackquery %s%n", Arrays.toString(e.getStackTrace()));
          }
        case WAITING_FOR_PHOTO_ENCODE:
          String response = null;
          try {
            if (msg != null && msg.hasDocument()) {
              response = "foto recibida, ahora envia el texto que deseas encriptar.";
              setPhoto(msg.getDocument());
              state = StenographyState.PROCESSING_ENCODE;
            }
          } catch (Exception e) {
            response = "No hemos recibido ninguna foto, hubo un error";
            System.out.printf("ERROR al procesar la foto%s%n", Arrays.toString(e.getStackTrace()));
          }
          if (!update.hasCallbackQuery()) {
            BotStarter.Send(Mensajeria.createMsg(chatId, response));
          }
          break;
        case PROCESSING_ENCODE:
          if (msg.hasText()) {
            InputFile newPhoto = stenographyAlgorithm(getPhoto(), msg.getText());
            BotStarter.Send(Mensajeria.createMsg(chatId, newPhoto,"tu texto ya ha sido escondido en la foto"));
            CommandHandler.userContexts.remove(chatId);
          } else {
            BotStarter.Send(Mensajeria.createMsg(chatId, "no has enviando el texto, por favor envia el texto esconder en la foto."));
          }
          break;
        case PROCESSING_DECODE:
          if (msg.hasDocument()) {
            try {
              setPhoto(msg.getDocument());
            } catch (Exception e) {
              System.out.printf("error al obtener la imagen del mensaje:%s%n", Arrays.toString(e.getStackTrace()));
            }
            try {
              String secret;
              BufferedImage bImage = null;
              try {
                InputFile photoP = getPhoto();
                try {
                  bImage = getBufferedImageFromInputFile(photoP);
                } catch (Exception e) {
                  System.err.println("error al obtener la imagen del inputfile");
                }
                secret = recoverMessageFromImage(bImage);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
              try {
                secret = "Secreto encontrado:%s".formatted(secret);
                BotStarter.Send(Mensajeria.createMsg(Mensajeria.getChatId(update), secret));
                CommandHandler.userContexts.remove(chatId);
              } catch (TelegramApiException e) {
                System.out.printf("problema al enviar el mensaje encriptado:%s%n", Arrays.toString(e.getStackTrace()));
              }
            } catch (Exception e) {
              System.err.printf("error an intentar recuperar una imagen%s%n", Arrays.toString(e.getStackTrace()));
            }
          } else {
            BotStarter.Send(Mensajeria.createMsg(chatId, "no has enviado la foto, por favor enviala."));
          }
          break;
      }
    }
  }

  private static BufferedImage getBufferedImageFromInputFile(InputFile inputFile) {
    try {
      // Obtiene el file_id del InputFile
      String fileId = inputFile.getAttachName();
      GetFile getFile = new GetFile();
      getFile.setFileId(fileId);

      // Descarga el archivo de Telegram
      org.telegram.telegrambots.meta.api.objects.File file = BotStarter.getInstance().execute(getFile);
      String filePath = file.getFilePath();
      URL fileUrl = new URI("https://api.telegram.org/file/bot%s/%s".formatted(BotStarter.getInstance().getBotToken(), filePath)).toURL();
      System.out.println(fileUrl);
      try (InputStream inputStream = fileUrl.openStream()) {
        return ImageIO.read(inputStream);
      }
    } catch (Exception e) {
      System.out.printf("error al obtener la imagen de inputfile:%s%n", Arrays.toString(e.getStackTrace()));
      return null;
    }
  }


  public static InputFile bufferedImageToInputFile(BufferedImage bufferedImage, String fileName) throws IOException {
    // Crea el archivo temporal para guardar la imagen
    File tempFile = File.createTempFile(fileName, "");

    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      ImageIO.write(bufferedImage, "png", fos);
    }
    if (!tempFile.exists() || !tempFile.canRead()) {
      throw new IOException("El archivo temporal no se pudo crear o leer correctamente.");
    }
    return new InputFile(tempFile);
  }

  private static InputFile stenographyAlgorithm(InputFile image, String originalMsg) {
    String msg = "_START_%s_END_".formatted(originalMsg);
    String name = image.getAttachName();
    BufferedImage bImage = getBufferedImageFromInputFile(image);
    StringBuilder binaryMsg = new StringBuilder();

    for (byte b : msg.getBytes()) {
      binaryMsg.append(String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0'));
    }

    int index = 0;
    outerLoop:
    for (int y = 0; y < Objects.requireNonNull(bImage).getHeight(); y++) {
      for (int x = 0; x < bImage.getWidth(); x++) {
        if (index + 2 >= binaryMsg.length()) {
          break outerLoop;
        }
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

  private static String recoverMessageFromImage(BufferedImage bImage) {
    StringBuilder binaryMsg = new StringBuilder();
    if (bImage == null) {
      return " ";
    } else {
      try {
        outerLoop:
        for (int y = 0; y < Objects.requireNonNull(bImage).getHeight(); y++) {
          for (int x = 0; x < bImage.getWidth(); x++) {
            int rgb = bImage.getRGB(x, y);
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;

            // Recuperar los bits menos significativos
            binaryMsg.append(getLSB(r));
            binaryMsg.append(getLSB(g));
            binaryMsg.append(getLSB(b));

            // Verificar si ya se tiene suficiente para formar el mensaje de "_END_"
            if (binaryMsg.length() >= 40) { // "_END_" en binario es de 40 bits
              String recoveredMessage = binaryToString(binaryMsg.toString());
              if (recoveredMessage.contains("_END_")) {
                break outerLoop;
              }
            }

            if (binaryMsg.length() > 9000) { // Límite en bits para evitar crecimiento descontrolado
              break outerLoop;
            }
          }
        }

        String recoveredMessage = binaryToString(binaryMsg.toString());
        if (recoveredMessage.contains("_START_") && recoveredMessage.contains("_END_")) {
          int startIndex = recoveredMessage.indexOf("_START_") + "_START_".length();
          int endIndex = recoveredMessage.indexOf("_END_");
          return recoveredMessage.substring(startIndex, endIndex);
        } else {
          return "No se encontró un mensaje oculto válido.";
        }
      } catch (Exception e) {
        System.err.printf("Error en el algoritmo de retroceso esteganográfico: %s%n", e.getMessage());
        throw new RuntimeException(e);
      }
    }
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
