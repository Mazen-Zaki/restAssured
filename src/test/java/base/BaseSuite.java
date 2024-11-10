package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static utilities.Configurations.bookingBaseUrl;

public class BaseSuite
{
    private static final Logger logger = LogManager.getLogger(BaseSuite.class);

    @BeforeClass(alwaysRun = true)
    public void setup()
    {
        logger.info("Startup the tests");

        RestAssured.baseURI = bookingBaseUrl;
        logger.info("Booking base URL : https://restful-booker.herokuapp.com");
    }
}
