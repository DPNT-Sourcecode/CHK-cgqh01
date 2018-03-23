package befaster.solutions;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CheckoutTest {

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

    private String serialize(Item... items) {
        return Arrays.stream(items)
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

}