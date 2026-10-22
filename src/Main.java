import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// javac *.java
// time java -cp . Main ../input/input3.txt ../output/output33.txt
// diff ../output/output3.txt ../output/output33.txt

/** Entry point. The two command-line arguments are the input and output paths. */
public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            java.lang.System.err.println("Usage: java Main <input file> <output file>");
            return;
        }

        RealEstateSystem realEstateSystem = new RealEstateSystem();
        String[] tokens = new String[10];

        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]), 32768);
             BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]), 32768)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int tokenCount = tokenize(line, tokens);
                if (tokenCount > 0) {
                    String command = tokens[0];
                    switch (command) {
                        case "Add":
                            // Format: Add <ID> <Price> <Size>
                            String addId = tokens[1];
                            int addPrice = Integer.parseInt(tokens[2]);
                            int addSize = Integer.parseInt(tokens[3]);

                            if(realEstateSystem.addHouse(addId,addPrice,addSize)) writer.write("House " + addId + " Added Successfully\n");
                            else writer.write("ERROR\n");
                            break;

                        case "Remove":
                            // Format: Remove <ID>
                            String removeId = tokens[1];
                            if(realEstateSystem.removeHouse(removeId)) writer.write("House " + removeId + " Removed Successfully\n");
                            else writer.write("ERROR\n");
                            break;

                        case "Update":
                            // Format: Update <ID> <NewPrice>
                            String updateId = tokens[1];
                            int newPrice = Integer.parseInt(tokens[2]);
                            if(realEstateSystem.updatePrice(updateId,newPrice)) writer.write("House " + updateId + " Updated Successfully\n");
                            else writer.write("ERROR\n");
                            break;

                        case "Search":
                            // Format: Search <SearchID> <MaxPrice> <MinSize> <Count>
                            String searchId = tokens[1];
                            int maxPrice = Integer.parseInt(tokens[2]);
                            int minSize = Integer.parseInt(tokens[3]);
                            int count = Integer.parseInt(tokens[4]);

                            SearchSession searchSession = realEstateSystem.search(searchId,minSize,maxPrice,count);

                            if(searchSession != null){
                                int houseListSize = searchSession.getHouseList().size();
                                for(int i = 0; i<houseListSize;i++){
                                    String id = searchSession.getHouseList().get(i).getId();
                                    int price = searchSession.getHouseList().get(i).getPrice();
                                    int size = searchSession.getHouseList().get(i).getSize();
                                    String priority = searchSession.getPriorityList().get(i);
                                    writer.write(id + " " + price + " " + size + " " + priority + "\n");
                                }
                            }
                            else writer.write("ERROR\n");
                            break;

                        case "Buy":
                            // Format: Buy <SearchID> <Index>
                            String buySearchId = tokens[1];
                            int index = Integer.parseInt(tokens[2]);
                            House soldHouse = realEstateSystem.buy(buySearchId,index);
                            if(soldHouse != null) writer.write("House " + soldHouse.getId() + " Sold\n");
                            else writer.write("ERROR\n");
                            break;

                        case "Close":
                            // Format: Close <SearchID>
                            String closeSearchId = tokens[1];
                            if(realEstateSystem.closeSearch(closeSearchId)) writer.write("Search " + closeSearchId + " Closed\n");
                            else writer.write("ERROR\n");
                            break;

                        case "Count":
                            // Format: Count (parametresiz)
                            int cnt = realEstateSystem.count();
                            writer.write("Count : " + cnt + "\n");
                            break;

                        case "TotalPrice":
                            // Format: TotalPrice <MaxPrice> <MinSize>
                            int tpMaxPrice = Integer.parseInt(tokens[1]);
                            int tpMinSize = Integer.parseInt(tokens[2]);
                            long totalPrice = realEstateSystem.totalPrice(tpMaxPrice,tpMinSize);
                            writer.write("TotalPrice is " + totalPrice + "\n");
                            break;
                    }
                }
            }
        } catch (IOException exception) {
            java.lang.System.err.println("File operation failed: " + exception.getMessage());
        }
    }

    private static int tokenize(String line, String[] tokens) {
        int len = line.length();
        int tokenIdx = 0;
        int i = 0;

        while (i < len) {
            while (i < len && Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            if (i >= len) break;

            int start = i;
            while (i < len && !Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            tokens[tokenIdx++] = line.substring(start, i);
        }
        return tokenIdx;
    }
}
