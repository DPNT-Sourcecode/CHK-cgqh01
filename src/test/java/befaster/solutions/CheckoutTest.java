package befaster.solutions;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CheckoutTest {

    private static final int INVALID = -1;

    enum Item {
        A, B, C, D, E, F, Z
    }

    private static final int aPrice = 50;
    private static final int bPrice = 30;
    private static final int cPrice = 20;
    private static final int dPrice = 15;
    private static final int ePrice = 40;
    private static final int fPrice = 10;

    @Test
    public void test_can_buy_a_single_A() {
        int price = Checkout.checkout(serialize(Item.A));
        assertEquals(aPrice, price);
    }

    @Test
    public void test_can_buy_a_single_C() {
        int price = Checkout.checkout(serialize(Item.C));
        assertEquals(cPrice, price);
    }

    @Test
    public void test_can_buy_a_single_B() {
        int price = Checkout.checkout(serialize(Item.B));
        assertEquals(bPrice, price);
    }

    @Test
    public void test_can_buy_a_single_D() {
        int price = Checkout.checkout(serialize(Item.D));
        assertEquals(dPrice, price);
    }

    @Test
    public void test_can_buy_multiple_A() {
        int price = Checkout.checkout(serialize(Item.A, Item.A));
        assertEquals(aPrice * 2, price);
    }

    @Test
    public void empty_basket_doesnt_cost_anything() {
        int price = Checkout.checkout(serialize());
        assertEquals(0, price);
    }

    @Test
    public void returns_negative_one_for_null_input() {
        int price = Checkout.checkout(null);
        assertEquals(INVALID, price);
    }

    @Test
    public void returns_negative_one_for_invalid_item() {
        int price = Checkout.checkout(serialize(Item.Z));
        assertEquals(INVALID, price);
    }

    @Test
    public void returns_negative_one_if_any_item_is_invalid() {
        int price = Checkout.checkout(serialize(Item.A, Item.Z));
        assertEquals(INVALID, price);
    }

    @Test
    public void three_A_gets_special_price() {
        int price = Checkout.checkout(serialize(Item.A, Item.A, Item.A));
        assertEquals(130, price);
    }

    @Test
    public void five_A_gets_special_price() {
        int price = Checkout.checkout(serialize(Item.A, Item.A, Item.A, Item.A, Item.A));
        assertEquals(200, price);
    }

    @Test
    public void deal_for_A_gets_applied_multiple_times() {
        int price = Checkout.checkout(serialize(
                Item.A, Item.A, Item.A, Item.A, Item.A,
                Item.A, Item.A, Item.A, Item.A, Item.A,
                Item.A, Item.A, Item.A, Item.A, Item.A
        ));
        assertEquals(200 * 3, price);
    }

    @Test
    public void can_apply_to_deals_to_basket() {
        int price = Checkout.checkout(serialize(
                Item.A, Item.A, Item.A, Item.A, Item.A, // 5 for 200
                Item.A, Item.A, Item.A // 3 for 130
        ));
        assertEquals(200 + 130, price);
    }

    @Test
    public void deal_for_A_gets_applied_but_also_undealified_a() {
        int price = Checkout.checkout(serialize(Item.A, Item.A, Item.A, Item.A));
        assertEquals(130 + 50, price);
    }

    @Test
    public void two_B_gets_special_price() {
        int price = Checkout.checkout(serialize(Item.B, Item.B));
        assertEquals(45, price);
    }

    @Test
    public void can_buy_one_E() {
        int price = Checkout.checkout(serialize(Item.E));
        assertEquals(40, price);
    }

    @Test
    public void two_E_gets_a_free_B() {
        int price = Checkout.checkout(serialize(Item.E, Item.E, Item.B));
        assertEquals(80, price);
    }

    @Test
    public void two_E_but_there_is_no_b_to_get_free() {
        int price = Checkout.checkout(serialize(Item.E, Item.E));
        assertEquals(ePrice * 2, price);
    }

    @Test
    public void two_E_and_two_B_only_get_one_B_free() {
        int price = Checkout.checkout(serialize(Item.E, Item.E, Item.B, Item.B));
        assertEquals(ePrice * 2 + bPrice, price);
    }

    @Test
    public void four_E_gets_two_B_free() {
        int price = Checkout.checkout(serialize(Item.E, Item.E, Item.E, Item.E, Item.B, Item.B));
        assertEquals(ePrice * 4, price);
    }


    @Test
    public void can_buy_one_F() {
        int price = Checkout.checkout(serialize(Item.F));
        assertEquals(fPrice, price);
    }

    @Test
    public void can_buy_three_Fs_with_third_one_free() {
        int price = Checkout.checkout(serialize(Item.F, Item.F, Item.F));
        assertEquals(fPrice * 2, price);
    }

    @Test
    public void can_buy_two_fs() {
        int price = Checkout.checkout(serialize(Item.F, Item.F));
        assertEquals(fPrice * 2, price);
    }

    @Test
    public void userAcceptanceTests() {
        assertEquals(280, Checkout.checkout("CCADDEEBBA").intValue());
        assertEquals(280, Checkout.checkout("ABCDEABCDE").intValue());
        assertEquals(455, Checkout.checkout("AAAAAEEBAAABB").intValue());
    }

    private String serialize(Item... items) {
        return Arrays.stream(items)
                .map(Enum::name)
                .collect(Collectors.joining(""));
    }

}
