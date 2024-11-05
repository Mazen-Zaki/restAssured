package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.AuthToken;
import models.BookingDetails;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import utilities.BookingDataGenerator;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static utilities.Configurations.*;



public class ApiTests
{

    @Test(priority = 1)
    public void authCreateToken(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        String token;

        AuthToken requestBody = AuthToken.builder()
                .username(username)
                .password(password)
                .build();

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(createTokenEndpoint);

        Assert.assertEquals(response.getStatusCode(), 200, "Bad request" + response.asString());

        token = response.jsonPath().getString("token");

        context.setAttribute("token", token);

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
                .extract()
                .response();

        Assert.assertEquals(response.getStatusCode(), 200, "Booking creation failed for " + bookingDetails.getFirstName() + " " + bookingDetails.getLastName() + "!");

//        System.out.println("Response: " + response.asString());

        Assert.assertEquals(response.jsonPath().getString("booking.firstname"), bookingDetails.getFirstName(), "firstName");
        Assert.assertEquals(response.jsonPath().getString("booking.lastname"), bookingDetails.getLastName(), "lastName");
        Assert.assertEquals(response.jsonPath().getInt("booking.totalprice"), bookingDetails.getTotalPrice(), "total Price");
        Assert.assertEquals(response.jsonPath().getBoolean("booking.depositpaid"), bookingDetails.isDepositPaid(), "deposit Paid");
        Assert.assertEquals(response.jsonPath().getString("booking.bookingdates.checkin"), bookingDetails.getBookingDates().getCheckin(), "checkin");
        Assert.assertEquals(response.jsonPath().getString("booking.bookingdates.checkout"), bookingDetails.getBookingDates().getCheckout(),"checkout");
        Assert.assertEquals(response.jsonPath().getString("booking.additionalneeds"), bookingDetails.getAdditionalNeeds(), "additional Needs");


        context.setAttribute("bookingid", response.jsonPath().getInt("bookingid"));

        context.setAttribute("firstname", bookingDetails.getFirstName());

        context.setAttribute("lastname", bookingDetails.getLastName());

        context.setAttribute("checkin", bookingDetails.getBookingDates().getCheckin());

        context.setAttribute("checkout", bookingDetails.getBookingDates().getCheckout());

        System.out.println("(createBooking method) bookingid : " + response.jsonPath().getInt("bookingid"));
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
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Bad request: " + response.asString());

        //System.out.println("Response: " + response.asString());
    }

    @Test(dependsOnMethods = "createBooking",priority = 3)
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
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Bad request: " + response.asString());

        System.out.println("Response: " + response.asString());
    }

    @Test(dependsOnMethods = "createBooking",priority = 3)
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
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Bad request: " + response.asString());

        System.out.println("Response: " + response.asString());
    }


    @Test(dependsOnMethods = "createBooking",priority = 4)
    public void getBooking(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;
        int bookingid = (int) context.getAttribute("bookingid");

        System.out.println("bookingid : " + bookingid);


        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/booking/{bookid}", bookingid)
                .then()
                .assertThat()
//                .body(matchesJsonSchemaInClasspath("getBookingIds-schema.json"))
                .extract().response();

        System.out.println("response : " + response.asString());
    }






}
