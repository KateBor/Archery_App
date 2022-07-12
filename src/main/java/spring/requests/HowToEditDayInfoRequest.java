package spring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HowToEditDayInfoRequest
{
    private boolean canEdit;
    private boolean isRemovingBottom;
}
