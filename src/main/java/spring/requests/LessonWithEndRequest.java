package spring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class LessonWithEndRequest implements Serializable {
    private String date;
    private String timeStart;
    private String timeEnd;
}
