package models;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class BookingDates
{
    @JsonSetter("checkin")
    private String checkin;

    @JsonSetter("checkout")
    private String checkout;

    public boolean detailedEquals(BookingDates other)
    {
        if (other == null) return false;

        boolean isEqual = true;

        if (!this.checkin.equals(other.checkin)) {
            System.out.println("Mismatch in checkin: Expected " + this.checkin + ", but got " + other.checkin);
            isEqual = false;
        }
        if (!this.checkout.equals(other.checkout)) {
            System.out.println("Mismatch in checkout: Expected " + this.checkout + ", but got " + other.checkout);
            isEqual = false;
        }

        return isEqual;
    }

}
