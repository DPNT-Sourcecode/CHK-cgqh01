package befaster.solutions;

import java.util.*;
import java.util.stream.Collectors;

public class Checkout {

    private static final String DELIMITER = "";
    private static final int INVALID = -1;

    private static final Item A = new Item("A", 50);
    private static final Item B = new Item("B", 30);
    private static final Item C = new Item("C", 20);
    private static final Item D = new Item("D", 15);
    private static final Item E = new Item("E", 40);

    private static final List<Item> items = Arrays.asList(A, B, C, D, E);

    private static final Map<String, Item> itemsBySku = items
            .stream()
            .collect(Collectors.toMap(i -> i.sku, i -> i));

    private static final Deal threeAsFor130 = new Deal(A, 3, 130, A);
    private static final Deal fiveAsFor200 = new Deal(A, 5, 200, A);
    private static final Deal twoBFor45 = new Deal(B, 2, 45, B);
    private static final Deal twoEGetsAFreeB = new Deal(E, 2, 0, B);

    private static final List<Deal> deals = Arrays.asList(threeAsFor130, fiveAsFor200, twoBFor45, twoEGetsAFreeB);

    private static final Map<String, List<Deal>> dealsBySku = deals.stream()
            .collect(Collectors.groupingBy(d -> d.criterionItem.sku));

    public static Integer checkout(String skus) {
        return Optional.ofNullable(skus)
                .flatMap(itemSkus -> convertToValidItems(itemSkus).map(Checkout::getPrice))
                .orElse(INVALID);
    }

    private static int getPrice(List<Item> items) {

        Map<Item, Long> originalItemCounts = items.stream().collect(Collectors.groupingBy(i -> i, Collectors.counting()));
        return originalItemCounts.keySet()
                .stream()
                .mapToInt(item -> {
                    int numberOfItems = originalItemCounts.get(item).intValue();
                    List<Deal> deals = dealsBySku.getOrDefault(item.sku, Collections.emptyList());

                    int price = 0;
                    while (numberOfItems > 0) {
                        int currentNumberOfItems = numberOfItems;

                        Map<Double, Deal> dealsByValue = deals.stream()
                                .filter(deal -> dealAppliesTo(deal, item, currentNumberOfItems, originalItemCounts))
                                .collect(Collectors.toMap(d -> d.dealPriceInWholePounts * 1.0 / d.criterionItemQuantity, d -> d));

                        OptionalDouble maybeBestDealValue = dealsByValue.keySet()
                                .stream()
                                .mapToDouble(d -> d)
                                .min();

                        if (maybeBestDealValue.isPresent()) {
                            Deal deal = dealsByValue.get(maybeBestDealValue.getAsDouble());
                            price = price + deal.dealPriceInWholePounts;
                            numberOfItems = numberOfItems - deal.criterionItemQuantity;
                        } else {
                            price = price + item.priceInWholePounds * numberOfItems;
                            numberOfItems = 0;
                        }

                    }
                    return price;
                })
                .sum();

    }

    private static boolean dealAppliesTo(Deal deal, Item item, int itemCount, Map<Item, Long> originalItemCounts) {
        return deal.itemForDealPrice.equals(item) &&
                itemCount >= (item.equals(deal.criterionItem) ? deal.criterionItemQuantity : 1) &&
                originalItemCounts.get(deal.criterionItem) >= deal.criterionItemQuantity;
    }

    private static Optional<List<Item>> convertToValidItems(String skus) {
        return Optional.ofNullable(skus)
                .flatMap(delimitedSkus -> {

                    List<String> parsedSkus = Arrays.stream(delimitedSkus.split(DELIMITER))
                            .filter(i -> i.length() != 0)
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
        private final Item criterionItem;
        private final int criterionItemQuantity;
        private final int dealPriceInWholePounts;
        private final Item itemForDealPrice;

        private Deal(Item criterionItem, int criterionItemQuantity, int dealPriceInWholePounts, Item itemForDealPrice) {
            this.criterionItem = criterionItem;
            this.criterionItemQuantity = criterionItemQuantity;
            this.dealPriceInWholePounts = dealPriceInWholePounts;
            this.itemForDealPrice = itemForDealPrice;
        }
    }

    private static class Item {
        private final String sku;
        private final int priceInWholePounds;

        private Item(String sku, int priceInWholePounds) {
            this.sku = sku;
            this.priceInWholePounds = priceInWholePounds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (priceInWholePounds != item.priceInWholePounds) return false;
            return sku.equals(item.sku);
        }

        @Override
        public int hashCode() {
            int result = sku.hashCode();
            result = 31 * result + priceInWholePounds;
            return result;
        }
    }
}
