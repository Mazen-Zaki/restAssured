package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.AuthToken;
import models.BookingDetails;
import models.partialBookingDetails;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import utilities.BookingDataGenerator;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static utilities.Configurations.*;



public class ApiTests
{

    @Test(priority = 1)
    public void authCreateToken(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;

        AuthToken requestBody = AuthToken.builder()
                .username(username)
                .password(password)
                .build();

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

//        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code. Response body: " + response.asString());

        context.setAttribute("token", response.jsonPath().getString("token"));
    }

    @Test(invocationCount = 1, priority = 2)
    public void createBooking(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;

        BookingDetails bookingDetails = BookingDataGenerator.generateBookingDetails();

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(bookingDetails)
                .when()
                .post(createBookingEndpoint)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("createBooking-schema.json"))
                .statusCode(200)
                .body("booking.firstname", equalTo(bookingDetails.getFirstName()))
                .body("booking.lastname", equalTo(bookingDetails.getLastName()))
                .body("booking.totalprice", equalTo(bookingDetails.getTotalPrice()))
                .body("booking.depositpaid", equalTo(bookingDetails.isDepositPaid()))
                .body("booking.bookingdates.checkin", equalTo(bookingDetails.getBookingDates().getCheckin()))
                .body("booking.bookingdates.checkout", equalTo(bookingDetails.getBookingDates().getCheckout()))
                .body("booking.additionalneeds", equalTo(bookingDetails.getAdditionalNeeds()))
                .extract()
                .response();

//        Assert.assertEquals(response.getStatusCode(), 200, "Booking creation failed for " + bookingDetails.getFirstName() + " " + bookingDetails.getLastName() + "!");
//        Assert.assertEquals(response.jsonPath().getString("booking.firstname"), bookingDetails.getFirstName(), "firstName");
//        Assert.assertEquals(response.jsonPath().getString("booking.lastname"), bookingDetails.getLastName(), "lastName");
//        Assert.assertEquals(response.jsonPath().getInt("booking.totalprice"), bookingDetails.getTotalPrice(), "total Price");
//        Assert.assertEquals(response.jsonPath().getBoolean("booking.depositpaid"), bookingDetails.isDepositPaid(), "deposit Paid");
//        Assert.assertEquals(response.jsonPath().getString("booking.bookingdates.checkin"), bookingDetails.getBookingDates().getCheckin(), "checkin");
//        Assert.assertEquals(response.jsonPath().getString("booking.bookingdates.checkout"), bookingDetails.getBookingDates().getCheckout(),"checkout");
//        Assert.assertEquals(response.jsonPath().getString("booking.additionalneeds"), bookingDetails.getAdditionalNeeds(), "additional Needs");

        context.setAttribute("bookingid", response.jsonPath().getInt("bookingid"));
        context.setAttribute("firstname", bookingDetails.getFirstName());
        context.setAttribute("lastname", bookingDetails.getLastName());
        context.setAttribute("totalprice", bookingDetails.getTotalPrice());
        context.setAttribute("depositpaid", bookingDetails.isDepositPaid());
        context.setAttribute("checkin", bookingDetails.getBookingDates().getCheckin());
        context.setAttribute("checkout", bookingDetails.getBookingDates().getCheckout());
        context.setAttribute("additionalneeds", bookingDetails.getAdditionalNeeds());

        System.out.println("(createBooking method) bookingid : " + response.jsonPath().getInt("bookingid"));
        System.out.println("Response: " + response.asString());
    }


    @Test(priority = 3)
    public void getBookingIds(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        
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

//        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code. Response body: " + response.asString());

        System.out.println("Response: " + response.asString());
    }

    @Test(dependsOnMethods = "createBooking",priority = 4)
    public void getBookingIdsNamesFilter(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        String firstname = (String) context.getAttribute("firstname");
        String lastname = (String) context.getAttribute("lastname");


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

//        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code. Response body: " + response.asString());

        System.out.println("Response: " + response.asString());
    }

    @Test(dependsOnMethods = "createBooking",priority = 5)
    public void getBookingIdsDatesFilter(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        String checkin = (String) context.getAttribute("checkin");
        String checkout = (String) context.getAttribute("checkout");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(getBookingEndpoint + "?firstname={checkin}&lastname={checkout}",checkin,checkout)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("getBookingIds-schema.json"))
                .statusCode(200)
                .extract().response();

//        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code. Response body: " + response.asString());

        System.out.println("Response: " + response.asString());
    }

    @Test(dependsOnMethods = {"createBooking", "authCreateToken"}, priority = 6)
    public void updateBooking(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        int bookingid = (int) context.getAttribute("bookingid");
        String token = (String) context.getAttribute("token");

        BookingDetails bookingDetails = BookingDataGenerator.generateBookingDetails();

        Response response = RestAssured
                .given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(bookingDetails)
                .when()
                .put("/booking/{bookid}", bookingid)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("updateBooking-schema.json"))
                .statusCode(200)
                .extract().response();

//        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code. Response body: " + response.asString());

        context.setAttribute("firstname", bookingDetails.getFirstName());
        context.setAttribute("lastname", bookingDetails.getLastName());
        context.setAttribute("totalprice", bookingDetails.getTotalPrice());
        context.setAttribute("depositpaid", bookingDetails.isDepositPaid());
        context.setAttribute("checkin", bookingDetails.getBookingDates().getCheckin());
        context.setAttribute("checkout", bookingDetails.getBookingDates().getCheckout());
        context.setAttribute("additionalneeds", bookingDetails.getAdditionalNeeds());

        System.out.println("response : " + response.asString());
    }


    @Test(dependsOnMethods = {"createBooking", "authCreateToken", "updateBooking"},priority = 7)
    public void getBooking(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        int bookingid = (int) context.getAttribute("bookingid");
        String firstname = (String) context.getAttribute("firstname");
        String lastname = (String) context.getAttribute("lastname");
        int totalprice = (int) context.getAttribute("totalprice");
        boolean depositpaid = (boolean) context.getAttribute("depositpaid");
        String checkin = (String) context.getAttribute("checkin");
        String checkout = (String) context.getAttribute("checkout");
        String additionalneeds = (String) context.getAttribute("additionalneeds");

        System.out.println("bookingid : " + bookingid);


        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/booking/{bookid}", bookingid)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("getBooking-schema.json"))
                .statusCode(200)
                .body("firstname", equalTo(firstname))
                .body("lastname", equalTo(lastname))
                .body("totalprice", equalTo(totalprice))
                .body("depositpaid", equalTo(depositpaid))
                .body("bookingdates.checkin", equalTo(checkin))
                .body("bookingdates.checkout", equalTo(checkout))
                .body("additionalneeds", equalTo(additionalneeds))
                .extract().response();

//        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code. Response body: " + response.asString());
//        Assert.assertEquals(response.jsonPath().getString("firstname"), firstname, "firstName");
//        Assert.assertEquals(response.jsonPath().getString("lastname"), lastname, "lastName");
//        Assert.assertEquals(response.jsonPath().getInt("totalprice"), totalprice, "total Price");
//        Assert.assertEquals(response.jsonPath().getBoolean("depositpaid"), depositpaid, "deposit Paid");
//        Assert.assertEquals(response.jsonPath().getString("bookingdates.checkin"), checkin, "checkin");
//        Assert.assertEquals(response.jsonPath().getString("bookingdates.checkout"), checkout,"checkout");
//        Assert.assertEquals(response.jsonPath().getString("additionalneeds"), additionalneeds, "additional Needs");

        System.out.println("response : " + response.asString());
    }

    @Test(dependsOnMethods = {"createBooking", "authCreateToken"}, priority = 8)
    public void partialUpdateBooking(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        int bookingid = (int) context.getAttribute("bookingid");
        String token = (String) context.getAttribute("token");

        partialBookingDetails bookingDetails = BookingDataGenerator.partialGenerateBookingDetails();


        Response response = RestAssured
                .given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(bookingDetails)
                .when()
                .patch("/booking/{bookid}", bookingid)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("updateBooking-schema.json"))
                .statusCode(200)
                .body("firstname",equalTo(bookingDetails.getFirstName()))
                .body("lastname",equalTo(bookingDetails.getLastName()))
                .extract().response();

//        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code. Response body: " + response.asString());
//        Assert.assertEquals(response.jsonPath().getString("firstname"), bookingDetails.getFirstName(), "firstName");
//        Assert.assertEquals(response.jsonPath().getString("lastname"), bookingDetails.getLastName(), "lastName");

        System.out.println("response : " + response.asString());
        System.out.println("status code : " + response.getStatusCode());
    }

    @Test(dependsOnMethods = {"createBooking", "authCreateToken"},priority = 8)
    public void deleteBooking(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        int bookingid = (int) context.getAttribute("bookingid");
        String token = (String) context.getAttribute("token");

        System.out.println("bookingid : " + bookingid);

        Response response = RestAssured
                .given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("/booking/{bookid}", bookingid)
                .then()
                .assertThat()
                .body(equalTo("Created"))
                .statusCode(201)
                .extract().response();

//        Assert.assertEquals(response.getStatusCode(), 201, "Unexpected status code. Response body: " + response.asString());

        System.out.println("response : " + response.asString());
    }
}
