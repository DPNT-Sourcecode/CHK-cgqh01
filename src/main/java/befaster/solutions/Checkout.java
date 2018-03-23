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
    private static final Item F = new Item("F", 10);

    private static final List<Item> items = Arrays.asList(A, B, C, D, E, F);

    private static final Map<String, Item> itemsBySku = items
            .stream()
            .collect(Collectors.toMap(i -> i.sku, i -> i));

    private static final Deal threeAsFor130 = new Deal(A, 3, A, 130, 3);
    private static final Deal fiveAsFor200 = new Deal(A, 5, A, 200, 5);
    private static final Deal twoBFor45 = new Deal(B, 2, B, 45, 2);
    private static final Deal twoEGetsAFreeB = new Deal(E, 2, B, 0, 1);
    private static final Deal twoFGetOneFree = new Deal(F, 2, F, 0, 1);

    private static final List<Deal> deals = Arrays.asList(threeAsFor130, fiveAsFor200, twoBFor45, twoEGetsAFreeB, twoFGetOneFree);

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
                    Map<Item, Long> itemCounts = new HashMap<>(originalItemCounts);

                    int price = 0;
                    while (itemCounts.get(item) > 0) {
                        int currentNumberOfItems = itemCounts.get(item).intValue();

                        Map<Double, Deal> dealsByValue = deals.stream()
                                .filter(deal -> dealAppliesTo(deal, item, currentNumberOfItems, itemCounts))
                                .collect(Collectors.toMap(d -> d.dealPriceInWholePounts * 1.0 / d.criterionItemQuantity, d -> d));

                        OptionalDouble maybeBestDealValue = dealsByValue.keySet()
                                .stream()
                                .mapToDouble(d -> d)
                                .min();

                        if (maybeBestDealValue.isPresent()) {
                            Deal deal = dealsByValue.get(maybeBestDealValue.getAsDouble());
                            price = price + deal.dealPriceInWholePounts;
                            long criterionItemsLeft = itemCounts.get(deal.criterionItem) - deal.criterionItemQuantity;
                            itemCounts.put(deal.criterionItem, criterionItemsLeft);
                            if (deal.criterionItem != item) {
                                itemCounts.put(item, itemCounts.get(item) - 1);
                            }
                        } else {
                            price = price + item.priceInWholePounds * currentNumberOfItems;
                            itemCounts.put(item, 0L);
                        }

                    }
                    return price;
                })
                .sum();

    }

    private static boolean dealAppliesTo(Deal deal, Item item, int itemCount, Map<Item, Long> originalItemCounts) {
        return deal.applicableItemForDeal.equals(item) &&
                itemCount >= (item.equals(deal.criterionItem) ? deal.criterionItemQuantity : 1) &&
                originalItemCounts.containsKey(deal.criterionItem) &&
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
        private final Item applicableItemForDeal;
        private final int applicableItemQuantity;

        private Deal(Item criterionItem, int criterionItemQuantity, Item applicableItemForDeal, int dealPriceInWholePounts, int applicableItemQuantity) {
            this.criterionItem = criterionItem;
            this.criterionItemQuantity = criterionItemQuantity;
            this.dealPriceInWholePounts = dealPriceInWholePounts;
            this.applicableItemForDeal = applicableItemForDeal;
            this.applicableItemQuantity = applicableItemQuantity;
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
