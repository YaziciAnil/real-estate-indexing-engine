import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileComparator {

    public static void main(String[] args) {
        // Karşılaştırmak istediğin iki dosyanın yolunu buraya yaz veya argüman olarak al
        String file1 = args.length >= 2 ? args[0] : "output1.txt";
        String file2 = args.length >= 2 ? args[1] : "output11.txt";

        compareFiles(file1, file2);
    }

    public static void compareFiles(String path1, String path2) {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(path1));
             BufferedReader reader2 = new BufferedReader(new FileReader(path2))) {

            String line1;
            String line2;
            int lineNumber = 1;
            int diffCount = 0;

            System.out.println("Dosyalar karsilastiriliyor:");
            System.out.println("1. Dosya: " + path1);
            System.out.println("2. Dosya: " + path2);
            System.out.println("--------------------------------------------------");

            while (true) {
                line1 = reader1.readLine();
                line2 = reader2.readLine();

                // İki dosyanın da sonuna gelindiyse döngüden çık
                if (line1 == null && line2 == null) {
                    break;
                }

                // Dosyalardan biri diğerinden önce bittiyse
                if (line1 == null) {
                    System.out.printf("Satir %d: 1. dosya bitti ama 2. dosyada su satir var:\n  [2]: %s\n", lineNumber, line2);
                    diffCount++;
                } else if (line2 == null) {
                    System.out.printf("Satir %d: 2. dosya bitti ama 1. dosyada su satir var:\n  [1]: %s\n", lineNumber, line1);
                    diffCount++;
                }
                // İki satır da dolu ama içerikleri farklıysa (trim() ile baştaki/sondaki gereksiz boşlukları görmezden gelebilirsin)
                else if (!line1.equals(line2)) {
                    System.out.printf("Fark Satir %d:\n  [1]: %s\n  [2]: %s\n", lineNumber, line1, line2);
                    diffCount++;
                }

                lineNumber++;
            }

            System.out.println("--------------------------------------------------");
            if (diffCount == 0) {
                System.out.println("TEBRIKLER: Iki dosya birebir AYNI!");
            } else {
                System.out.printf("TOPLAM FARK: %d satirda uyumsuzluk bulundu.\n", diffCount);
            }

        } catch (IOException e) {
            System.err.println("Dosya okuma hatasi: " + e.getMessage());
        }
    }
}
