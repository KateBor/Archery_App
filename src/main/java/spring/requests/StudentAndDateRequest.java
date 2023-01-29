package spring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class StudentAndDateRequest
{
    Long id;
    LocalDate date;
    Boolean hasCome;
}
