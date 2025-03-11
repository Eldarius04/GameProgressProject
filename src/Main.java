import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] args) {
        // Путь к архиву и папке для распаковки
        String zipFilePath = "C:\\Users\\eldar\\Desktop\\Games\\savegames\\saves.zip";
        String outputFolderPath = "C:\\Users\\eldar\\Desktop\\Games\\savegames\\unzipped";

        // Распаковываем архив
        openZip(zipFilePath, outputFolderPath);

        // Путь к файлу для десериализации
        String saveFilePath = outputFolderPath + File.separator + "save2.dat";

        // Десериализуем файл и выводим состояние игры
        GameProgress progress = openProgress(saveFilePath);
        if (progress != null) {
            System.out.println("Состояние игры: " + progress);
        }
    }

    public static void openZip(String zipFilePath, String outputFolderPath) {
        // Проверяем, существует ли архив
        if (!Files.exists(Path.of(zipFilePath))) {
            System.out.println("Архив не найден: " + zipFilePath);
            return;
        }

        // Создаем папку для распаковки, если она не существует
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // Получаем имя файла из архива
                String fileName = entry.getName();
                File outputFile = new File(outputFolderPath + File.separator + fileName);

                // Создаем файл и записываем в него данные
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
                System.out.println("Файл распакован: " + outputFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при распаковке архива: " + e.getMessage());
        }
    }

    public static GameProgress openProgress(String filePath) {
        // Проверяем, существует ли файл
        if (!Files.exists(Path.of(filePath))) {
            System.out.println("Файл не найден: " + filePath);
            return null;
        }

        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // Десериализуем объект
            GameProgress progress = (GameProgress) ois.readObject();
            System.out.println("Файл успешно десериализован: " + filePath);
            return progress;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при десериализации файла: " + e.getMessage());
            return null;
        }
    }
}