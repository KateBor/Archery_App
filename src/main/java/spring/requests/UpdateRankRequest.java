package spring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class UpdateRankRequest implements Serializable {
    private long id;
    private String rank;
}
