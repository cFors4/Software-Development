import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        pebbleGameTest.class,
})

public class junitTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(pebbleGameTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
            failure.getException().printStackTrace();
        }


        System.out.println("Test successful? " + result.wasSuccessful());
    }
    // This class remains empty, it is used only as a holder for the above annotations
}


