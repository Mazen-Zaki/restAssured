package tests;

import base.BaseSuite;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.AuthToken;
import models.BookingDetails;
import models.BookingResponse;
import models.partialBookingDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import utilities.BookingDataGenerator;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static utilities.Configurations.*;



public class ApiTests extends BaseSuite
{

    private static final Logger logger = LogManager.getLogger(ApiTests.class);

    @Test(priority = 1)
    public void authCreateToken(ITestContext context)
    {
        logger.info("Auth - CreateToken - POST");
        logger.info("API : https://restful-booker.herokuapp.com/auth");

        AuthToken requestBody = AuthToken.builder()
                .username(username)
                .password(password)
                .build();

        logger.info("Request Body : " + requestBody.toString());

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(createTokenEndpoint)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("getAuthToken-schema.json"))
                .statusCode(200)
                .extract()
                .response();

        logger.info("Response ({}) : {}",response.statusCode() , response.asString());

        context.setAttribute("token", response.jsonPath().getString("token"));
        logger.info("token has been saved in the context");
    }

    @Test(invocationCount = 1, priority = 2)
    public void createBooking(ITestContext context)
    {
        logger.info("Booking - CreateBooking - POST");
        logger.info("API : https://restful-booker.herokuapp.com/booking");

        BookingDetails bookingDetails = BookingDataGenerator.generateBookingDetails();

        logger.info("Request body : " + bookingDetails.toString());

        BookingResponse actualRes = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(bookingDetails)
                .when()
                .post(createBookingEndpoint)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("createBooking-schema.json"))
                .statusCode(200)
                .extract()
                .as(BookingResponse.class);

        logger.info("Response : {}" , actualRes.toString());

        Assert.assertTrue(actualRes.getBookingDetails().equals(bookingDetails), "mismatch");

        context.setAttribute("res",actualRes );
        logger.info("actualRes has been saved in the context");
    }


    @Test(priority = 3)
    public void getBookingIds()
    {
        logger.info("Booking - GetBookingIds - All IDs - GET");
        logger.info("API : https://restful-booker.herokuapp.com/booking");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(getBookingEndpoint)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("getBookingIds-schema.json"))
                .statusCode(200)
                .extract().response();

        logger.info("Response : {}" , response.asString());
    }

    @Test(dependsOnMethods = "createBooking",priority = 4)
    public void getBookingIdsNamesFilter(ITestContext context)
    {
        logger.info("Booking - GetBookingIds - Filter by name - GET");
        logger.info("API : https://restful-booker.herokuapp.com/booking");

        BookingResponse expectedRes = (BookingResponse) context.getAttribute("res");

        String firstname = expectedRes.getBookingDetails().getFirstName();
        String lastname = expectedRes.getBookingDetails().getLastName();

        logger.info("?firstname={}&lastname={}",firstname,lastname);

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(getBookingEndpoint + "?firstname={firstname}&lastname={lastname}",firstname,lastname)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("getBookingIds-schema.json"))
                .statusCode(200)
                .extract().response();

        logger.info("Response : {}" , response.asString());

    }

    @Test(dependsOnMethods = "createBooking",priority = 5)
    public void getBookingIdsDatesFilter(ITestContext context)
    {
        logger.info("Booking - GetBookingIds - Filter by dates - GET");
        logger.info("API : https://restful-booker.herokuapp.com/booking");

        BookingResponse expectedRes = (BookingResponse) context.getAttribute("res");

        String checkin = expectedRes.getBookingDetails().getBookingDates().getCheckin();
        String checkout = expectedRes.getBookingDetails().getBookingDates().getCheckout();

        logger.info("?checkin={}&checkout={}",checkin,checkout);

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(getBookingEndpoint + "?checkin={checkin}&checkout={checkout}",checkin,checkout)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("getBookingIds-schema.json"))
                .statusCode(200)
                .extract().response();

        logger.info("Response : {}" , response.asString());
    }

    @Test(dependsOnMethods = {"createBooking", "authCreateToken"}, priority = 6)
    public void updateBooking(ITestContext context)
    {
        logger.info("Booking - UpdateBooking - PUT");
        logger.info("API : https://restful-booker.herokuapp.com/booking/:id");

        BookingResponse bookingResponse = (BookingResponse) context.getAttribute("res");
        String token = (String) context.getAttribute("token");

        bookingResponse.setBookingDetails(BookingDataGenerator.generateBookingDetails());

        BookingDetails actualRes = RestAssured
                .given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(bookingResponse.getBookingDetails())
                .when()
                .put("/booking/{bookid}", bookingResponse.getBookingId())
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("updateBooking-schema.json"))
                .statusCode(200)
                .extract()
                .as(BookingDetails.class);

        logger.info("Response : {}" , actualRes.toString());

        Assert.assertTrue(actualRes.equals(bookingResponse.getBookingDetails()));

        context.setAttribute("res", bookingResponse);
        logger.info("actualRes has been saved in the context");
    }


    @Test(dependsOnMethods = {"createBooking", "authCreateToken"},priority = 7)
    public void getBooking(ITestContext context)
    {
        logger.info("Booking - GetBooking - GET");
        logger.info("API : https://restful-booker.herokuapp.com/booking/:id");

        BookingResponse expectedRes = (BookingResponse) context.getAttribute("res");

        BookingDetails actualRes = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/booking/{bookid}", expectedRes.getBookingId())
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("getBooking-schema.json"))
                .statusCode(200)
                .extract()
                .as(BookingDetails.class);

        Assert.assertTrue(expectedRes.getBookingDetails().equals(actualRes));

        logger.info("Response : {}" , actualRes.toString());

    }

    @Test(dependsOnMethods = {"createBooking", "authCreateToken"}, priority = 8)
    public void partialUpdateBooking(ITestContext context)
    {
        logger.info("Booking - PartialUpdateBooking - PUT");
        logger.info("API : https://restful-booker.herokuapp.com/booking/:id");

        BookingResponse bookingResponse = (BookingResponse) context.getAttribute("res");
        String token = (String) context.getAttribute("token");

        partialBookingDetails bookingDetails = BookingDataGenerator.partialGenerateBookingDetails();

        bookingResponse.getBookingDetails().setFirstName(bookingDetails.getFirstName());
        bookingResponse.getBookingDetails().setLastName(bookingDetails.getLastName());

        BookingDetails actualRes = RestAssured
                .given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(bookingDetails)
                .when()
                .patch("/booking/{bookid}", bookingResponse.getBookingId())
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("updateBooking-schema.json"))
                .statusCode(200)
                .extract()
                .as(BookingDetails.class);

        logger.info("Response : {}" , actualRes.toString());

        Assert.assertTrue(actualRes.equals(bookingResponse.getBookingDetails()));

        context.setAttribute("res", bookingResponse);
        logger.info("actualRes has been saved in the context");
    }

    @Test(dependsOnMethods = {"createBooking", "authCreateToken"},priority = 8)
    public void deleteBooking(ITestContext context)
    {
        logger.info("Booking - PartialUpdateBooking - PUT");
        logger.info("API : https://restful-booker.herokuapp.com/booking/:id");

        BookingResponse bookingResponse = (BookingResponse) context.getAttribute("res");
        String token = (String) context.getAttribute("token");

        Response response = RestAssured
                .given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("/booking/{bookid}", bookingResponse.getBookingId())
                .then()
                .assertThat()
                .body(equalTo("Created"))
                .statusCode(201)
                .extract().response();

        logger.info("Response ({}) : {}",response.statusCode() , response.asString());
    }
}
