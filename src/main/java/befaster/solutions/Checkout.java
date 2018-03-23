package befaster.solutions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Checkout {

    private static final String DELIMITER = ",";

    private static final List<Item> items = Arrays.asList(
            new Item("A", 50),
            new Item("B", 30),
            new Item("C", 20),
            new Item("D", 15)
    );

    private static final Map<String, Item> itemsBySku = items
            .stream()
            .collect(Collectors.toMap(i -> i.sku, i -> i));

    public static Integer checkout(String skus) {
        return Arrays.stream(skus.split(DELIMITER))
                .mapToInt(sku -> itemsBySku.get(sku.trim()).priceInWholePounds)
                .sum();
    }

    private static class Item {
        private final String sku;
        private final int priceInWholePounds;

        private Item(String sku, int priceInWholePounds) {
            this.sku = sku;
            this.priceInWholePounds = priceInWholePounds;
        }
    }
}
