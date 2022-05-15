package spring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class SetTimeEndRequest implements Serializable {
    private String timeStart;
    private String seasonTicket;
}
