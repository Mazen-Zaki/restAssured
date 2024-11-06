package utilities;

import com.github.javafaker.Faker;
import models.BookingDates;
import models.BookingDetails;
import models.partialBookingDetails;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class BookingDataGenerator
{
    private static final Faker faker = new Faker();

    public static BookingDetails generateBookingDetails()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        BookingDates dates = BookingDates.builder()
                .checkin(sdf.format(faker.date().birthday()))
                .checkout(sdf.format(faker.date().future(10, TimeUnit.DAYS)))
                .build();

        return BookingDetails.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .totalPrice(faker.number().numberBetween(50, 500))
                .depositPaid(faker.bool().bool())
                .bookingDates(dates)
                .additionalNeeds("Breakfast")
                .build();
    }

    public static partialBookingDetails partialGenerateBookingDetails()
    {
        return partialBookingDetails.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }
}
