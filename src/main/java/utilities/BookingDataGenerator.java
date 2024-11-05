package utilities;

import com.github.javafaker.Faker;
import models.createBookingDetails;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class BookingDataGenerator
{
    private static final Faker faker = new Faker();

    public static createBookingDetails generateBookingDetails()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String firstname = faker.name().firstName();
        String lastname = faker.name().lastName();
        int totalprice = faker.number().numberBetween(50, 500);
        boolean depositpaid = faker.bool().bool();
        String checkin = sdf.format(faker.date().birthday());
        String checkout = sdf.format(faker.date().future(10, TimeUnit.DAYS));
        String additionalneeds = "Breakfast";

        createBookingDetails bookingDetails = new createBookingDetails();
        bookingDetails.setFirstName(firstname);
        bookingDetails.setLastName(lastname);
        bookingDetails.setTotalPrice(totalprice);
        bookingDetails.setDepositPaid(depositpaid);
        bookingDetails.setAdditionalNeeds(additionalneeds);

        createBookingDetails.BookingDates bookingDates = new createBookingDetails.BookingDates();
        bookingDates.setCheckin(checkin);
        bookingDates.setCheckout(checkout);
        bookingDetails.setBookingDates(bookingDates);

        return bookingDetails;
    }
}
