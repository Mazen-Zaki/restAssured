package models;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthToken
{
    @JsonSetter("username")
    private String username;

    @JsonSetter("password")
    private String password;
}
