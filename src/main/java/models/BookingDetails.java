package models;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class BookingDetails
{
    private static final Logger logger = LogManager.getLogger(BookingDetails.class);

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

    @Override
    public String toString() {
        return "BookingDetails{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", totalPrice=" + totalPrice +
                ", depositPaid=" + depositPaid +
                ", bookingDates=" + bookingDates +
                ", additionalNeeds='" + additionalNeeds + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BookingDetails other = (BookingDetails) obj;

        if (!this.firstName.equals(other.firstName))
        {
            logger.info("Mismatch in firstName: Expected " + this.firstName + ", but got " + other.firstName);
            return false;
        }
        if (!this.lastName.equals(other.lastName))
        {
            logger.info("Mismatch in lastName: Expected " + this.lastName + ", but got " + other.lastName);
            return false;
        }
        if (this.totalPrice != other.totalPrice)
        {
            logger.info("Mismatch in totalPrice: Expected " + this.totalPrice + ", but got " + other.totalPrice);
            return false;
        }
        if (this.depositPaid != other.depositPaid)
        {
            logger.info("Mismatch in depositPaid: Expected " + this.depositPaid + ", but got " + other.depositPaid);
            return false;
        }
        if (!this.bookingDates.detailedEquals(other.bookingDates))
        {
            logger.info("Mismatch in bookingDates");
            return false;
        }
        if (!this.additionalNeeds.equals(other.additionalNeeds))
        {
            logger.info("Mismatch in additionalNeeds: Expected " + this.additionalNeeds + ", but got " + other.additionalNeeds);
            return false;
        }

        logger.info("Expected == Actual");
        return true;
    }
}
