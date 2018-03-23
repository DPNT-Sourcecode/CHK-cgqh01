package befaster.solutions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelloTest {

    @Test
    public void say_hello_to_someone() {
        String bellaGreeting = Hello.hello("Bella");
        assertEquals("Hello, Bella!", bellaGreeting);
    }

}