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

    private static final Deal threeAsFor130 = new Deal(A, 3, 130);
    private static final Deal fiveAsFor200 = new Deal(A, 5, 200);
    private static final Deal twoBFor45 = new Deal(B, 2, 45);

    private static final List<Deal> deals = Arrays.asList(threeAsFor130, fiveAsFor200, twoBFor45);

    private static final Map<String, List<Deal>> dealsBySku = deals.stream()
            .collect(Collectors.groupingBy(d -> d.item.sku));

    public static Integer checkout(String skus) {
        return Optional.ofNullable(skus)
                .flatMap(itemSkus -> convertToValidItems(itemSkus).map(Checkout::getPrice))
                .orElse(INVALID);
    }

    private static int getPrice(List<Item> items) {

        Map<Item, Long> itemCounts = items.stream().collect(Collectors.groupingBy(i -> i, Collectors.counting()));

        return itemCounts.keySet()
                .stream()
                .mapToInt(item -> {
                    int numberOfItems = itemCounts.get(item).intValue();
                    List<Deal> deals = dealsBySku.getOrDefault(item.sku, Collections.emptyList());

                    int price = 0;
                    while (numberOfItems > 0) {
                        int currentNumberOfItems = numberOfItems;

                        Map<Double, Deal> dealsByValue = deals.stream()
                                .filter(deal -> deal.quantityToQualifyForDeal <= currentNumberOfItems)
                                .collect(Collectors.toMap(d -> d.dealPriceInWholePounts * 1.0 / d.quantityToQualifyForDeal, d -> d));

                        OptionalDouble maybeBestDealValue = dealsByValue.keySet()
                                .stream()
                                .mapToDouble(d -> d)
                                .min();

                        if (maybeBestDealValue.isPresent()) {
                            Deal deal = dealsByValue.get(maybeBestDealValue.getAsDouble());
                            price = price + deal.dealPriceInWholePounts;
                            numberOfItems = numberOfItems - deal.quantityToQualifyForDeal;
                        } else {
                            price = price + item.priceInWholePounds * numberOfItems;
                            numberOfItems = 0;
                        }

                    }
                    return price;
                })
                .sum();

    }

    private static boolean dealAppliesToItems(Deal deal, Map<Item, Long> itemsBySku) {
        return itemsBySku.containsKey(deal.item) && itemsBySku.get(deal.item) >= deal.quantityToQualifyForDeal;
    }

    private static class DealApplicationReport {
        private final Map<Item, Long> itemsAfterDeal;
        private final int dealReduction;

        private DealApplicationReport(Map<Item, Long> itemsAfterDeal, int dealReduction) {
            this.itemsAfterDeal = itemsAfterDeal;
            this.dealReduction = dealReduction;
        }
    }

    private static int getPriceUsingDeal(Item item, int numberOfItems, Deal dealToApply) {
        int numberOfTimesDealIsMet = numberOfItems / dealToApply.quantityToQualifyForDeal;
        int numberOfItemsNotInDeal = numberOfItems % dealToApply.quantityToQualifyForDeal;
        return numberOfTimesDealIsMet * dealToApply.dealPriceInWholePounts + numberOfItemsNotInDeal * item.priceInWholePounds;
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
