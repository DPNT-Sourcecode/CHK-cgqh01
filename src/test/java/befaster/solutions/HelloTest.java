package befaster.solutions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelloTest {
   private static final String GENERIC_HELLO_WORLD = "Hello, World!";

    @Test
    public void say_hello_to_someone() {
        String bellaGreeting = Hello.hello("Bella");
        assertEquals(GENERIC_HELLO_WORLD, bellaGreeting);
    }

}