package befaster.solutions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Checkout {

    private static final String DELIMITER = ",";
    private static final int INVALID = -1;

    private static final Item A = new Item("A", 50);
    private static final Item B = new Item("B", 30);
    private static final Item C = new Item("C", 20);
    private static final Item D = new Item("D", 15);

    private static final List<Item> items = Arrays.asList(A, B, C, D);

    private static final Map<String, Item> itemsBySku = items
            .stream()
            .collect(Collectors.toMap(i -> i.sku, i -> i));

    private static final Deal threeAsFor130 = new Deal(A, 3, 130);

    private static final List<Deal> deals = Arrays.asList(threeAsFor130);

    private static final Map<String, List<Deal>> dealsBySku = deals.stream()
            .collect(Collectors.groupingBy(d -> d.item.sku));

    public static Integer checkout(String skus) {
        return Optional.ofNullable(skus)
                .flatMap(itemSkus -> convertToValidItems(itemSkus).map(Checkout::getPrice))
                .orElse(INVALID);
    }

    private static int getPrice(List<Item> items) {
        Map<String, List<Item>> itemsBySku = items.stream().collect(Collectors.groupingBy(i -> i.sku));
        itemsBySku.entrySet()
                .stream()
                .map((sku, itemsForSku) -> )
        return items.stream().mapToInt(i -> i.priceInWholePounds).sum();
    }

    private static Optional<List<Item>> convertToValidItems(String skus) {
        return Optional.ofNullable(skus)
                .flatMap(delimitedSkus -> {

                    List<String> parsedSkus = Arrays.stream(delimitedSkus.split(DELIMITER))
                            .map(String::trim)
                            .collect(Collectors.toList());

                    boolean allSkusAreValid = parsedSkus.stream().allMatch(itemsBySku::containsKey);

                    if (allSkusAreValid) {
                        List<Item> items = parsedSkus.stream().map(itemsBySku::get).collect(Collectors.toList());
                        return Optional.of(items);
                    }
                    return Optional.empty();
                });

    }

    private static class Deal {
        private final Item item;
        private final int quantityToQualifyForDeal;
        private final int dealPriceInWholePounts;

        private Deal(Item item, int quantityToQualifyForDeal, int dealPriceInWholePounts) {
            this.item = item;
            this.quantityToQualifyForDeal = quantityToQualifyForDeal;
            this.dealPriceInWholePounts = dealPriceInWholePounts;
        }
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
