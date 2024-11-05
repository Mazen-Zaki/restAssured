package tests;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.createBookingDetails;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import utilities.BookingDataGenerator;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static utilities.Configurations.*;



public class ApiTests
{

    String token = null;



    @Test(priority = 1)
    public void authCreateToken(ITestContext context)
    {
        RestAssured.baseURI = bookingBaseUrl;

        String requestBody = String.format("{\n" +
                "    \"username\" : \"%s\",\n" +
                "    \"password\" : \"%s\"\n" +
                "}", username, password);

        JsonObject js;


        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(createTokenEndpoint);

        Assert.assertEquals(response.getStatusCode(), 200, "Bad request" + response.asString());

        token = response.jsonPath().getString("token");

        context.setAttribute("token", token);


        System.out.println("token: " + token);
        System.out.println("Response: " + response.asString());
    }

    @Test(dependsOnMethods = "authCreateToken", priority = 2)
    public void getBookingIds(ITestContext context)
    {

        String token = (String) context.getAttribute("token");

        RestAssured.baseURI = bookingBaseUrl;

        
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(getBookingIdsEndpoint)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("getBookingIds-schema.json"))
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Bad request: " + response.asString());

        System.out.println("Response: " + response.asString());
    }


    @Test(invocationCount = 1)
    public void createBooking()
    {
        RestAssured.baseURI = bookingBaseUrl;

//        // SET JSON KEYS
//        createBookingDetails bookingDetails = new createBookingDetails();
//        bookingDetails.setFirstName(firstname);
//        bookingDetails.setLastName(lastname);
//        bookingDetails.setTotalPrice(totalprice);
//        bookingDetails.setDepositPaid(depositpaid);
//        bookingDetails.setAdditionalNeeds(additionalneeds);
//
//        createBookingDetails.BookingDates bookingDates = new createBookingDetails.BookingDates();
//        bookingDates.setCheckin(checkin);
//        bookingDates.setCheckout(checkout);
//        bookingDetails.setBookingDates(bookingDates);

        createBookingDetails bookingDetails = BookingDataGenerator.generateBookingDetails();

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

        System.out.println("Response: " + response.asString());

        Assert.assertEquals(response.jsonPath().getString("booking.firstname"), bookingDetails.getFirstName(), "firstName");
        Assert.assertEquals(response.jsonPath().getString("booking.lastname"), bookingDetails.getLastName(), "lastName");
        Assert.assertEquals(response.jsonPath().getInt("booking.totalprice"), bookingDetails.getTotalPrice(), "total Price");
//        Assert.assertEquals(response.jsonPath().getBoolean("booking.depositpaid"), bookingDetails.getDepositPaid());
        Assert.assertEquals(response.jsonPath().getString("booking.bookingdates.checkin"), bookingDetails.getBookingDates().getCheckin(), "checkin");
        Assert.assertEquals(response.jsonPath().getString("booking.bookingdates.checkout"), bookingDetails.getBookingDates().getCheckout(),"checkout");
        Assert.assertEquals(response.jsonPath().getString("booking.additionalneeds"), bookingDetails.getAdditionalNeeds(), "additional Needs");
    }




}
