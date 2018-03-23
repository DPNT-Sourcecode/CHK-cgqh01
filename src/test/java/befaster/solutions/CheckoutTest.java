package befaster.solutions;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CheckoutTest {

    private static final int INVALID = -1;

    enum Item {
        A, B, C, D
    }

    @Test
    public void test_can_buy_a_single_A() {
        int price = Checkout.checkout(serialize(Item.A));
        assertEquals(50, price);
    }

    @Test
    public void test_can_buy_a_single_C() {
        int price = Checkout.checkout(serialize(Item.C));
        assertEquals(20, price);
    }

    @Test
    public void test_can_buy_a_single_B() {
        int price = Checkout.checkout(serialize(Item.B));
        assertEquals(30, price);
    }

    @Test
    public void test_can_buy_a_single_D() {
        int price = Checkout.checkout(serialize(Item.D));
        assertEquals(15, price);
    }

    @Test
    public void test_can_buy_multiple_A() {
        int price = Checkout.checkout(serialize(Item.A, Item.A));
        assertEquals(100, price);
    }

    @Test
    public void trims_whitespace_around_sku() {
        int price = Checkout.checkout(" A , A ");
        assertEquals(100, price);
    }

    @Test
    public void returns_negative_one_for_null_input() {
        int price = Checkout.checkout(null);
        assertEquals(INVALID, price);
    }

    private String serialize(Item... items) {
        return Arrays.stream(items)
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

}
