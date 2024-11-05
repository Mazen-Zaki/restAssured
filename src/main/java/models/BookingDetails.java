package models;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingDetails
{
    @JsonSetter("firstname")
    private String firstName;

    @JsonSetter("lastname")
    private String lastName;

    @JsonSetter("totalprice")
    private int totalPrice;

    @JsonSetter("depositpaid")
    private boolean depositPaid;

    @JsonSetter("bookingdates")
    private BookingDates bookingDates;

    @JsonSetter("additionalneeds")
    private String additionalNeeds;
}
