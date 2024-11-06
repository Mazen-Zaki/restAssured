package models;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class partialBookingDetails
{
    @JsonSetter("firstname")
    private String firstName;

    @JsonSetter("lastname")
    private String lastName;
}
