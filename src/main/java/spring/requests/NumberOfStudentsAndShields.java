package spring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class NumberOfStudentsAndShields implements Serializable {
    private int juniors;
    private int middles;
    private int seniors;
    private int shields;
}
