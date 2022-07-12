package spring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EditDayOfWeekRequest
{
    private int dayOfWeek;
    private String beginning;
    private String end;
}
