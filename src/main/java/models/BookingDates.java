package models;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingDates
{
    @JsonSetter("checkin")
    private String checkin;

    @JsonSetter("checkout")
    private String checkout;
}
