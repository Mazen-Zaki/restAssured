package models;


import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse
{
    @JsonSetter("bookingid")
    private int bookingId;

    @JsonSetter("booking")
    private BookingDetails bookingDetails;


    @Override
    public String toString() {
        return "BookingResponse{" +
                "bookingId=" + bookingId +
                ", bookingDetails=" + bookingDetails +
                '}';
    }
}
