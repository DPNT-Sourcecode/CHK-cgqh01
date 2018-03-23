package befaster.solutions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CheckoutTest {

    @Test
    public void test_can_buy_a_single_A() {
        int price = Checkout.checkout("A");
        assertEquals(50, price);
    }

}