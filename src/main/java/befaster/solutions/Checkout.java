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

        Map<String, List<Item>> itemsBySku = items.stream().collect(Collectors.groupingBy(i -> i.sku));
        List<Deal> applicableDeals = deals.stream().filter(deal -> dealAppliesToItems(deal, itemsBySku)).collect(Collectors.toList());

        Map<Item, Long> numberOfItemsPerSku = items.stream().collect(Collectors.groupingBy(i -> i, Collectors.counting()));

        return numberOfItemsPerSku.keySet()
                .stream()
                .mapToInt(item -> {
                    int numberOfItems = numberOfItemsPerSku.get(item).intValue();
                    if (dealsBySku.containsKey(item.sku)) {

                        deals.stream().sorted((o1, o2) -> {
                            double o1PricePerUnit = o1.dealPriceInWholePounts * 1.0 / o1.dealPriceInWholePounts;
                            double o2PricePerUnit = o2.dealPriceInWholePounts * 1.0 / o2.dealPriceInWholePounts;
//                            return o1PricePerUnit - o2PricePerUnit;
                            return 0;
                        } );

                        List<Deal> deals = dealsBySku.get(item.sku);
                        return deals.stream()
                                .mapToInt(deal -> getPriceUsingDeal(item, numberOfItems, deal))
                                .min().orElse(0);

                    } else {
                        return numberOfItems * item.priceInWholePounds;
                    }

                })
                .sum();

    }

    private static boolean dealAppliesToItems(Deal deal, Map<String, List<Item>> itemsBySku) {
        String sku = deal.item.sku;
        return itemsBySku.containsKey(sku) && itemsBySku.get(sku).size() >= deal.quantityToQualifyForDeal;
    }

    private static class DealApplicationReport {
        private final Map<String, List<Item>> itemsAfterDeal;
        private final int dealRecuction;

        private DealApplicationReport(Map<String, List<Item>> itemsAfterDeal, int dealRecuction) {
            this.itemsAfterDeal = itemsAfterDeal;
            this.dealRecuction = dealRecuction;
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
    }
}
