package spring.utils;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class Constants {
    public static class ProfileStatusConst {
        public static String ON_CHECKING = "onChecking";
        public static String APPROVED = "approved";
        public static String DELETED = "deleted";
        public static String NOT_REGISTERED = "notRegistered";
    }

    public static class RankConst {
        public static String NO_RANK = "no rank";
        public static String JUNIOR = "juniors";
        public static String MIDDLE = "middles";
        public static String SENIOR = "seniors";
    }

    public static class LimitsConst {
        public static int numberOfShields = 12;
        public static int wishedNumberOfJuniors = 5;
        public static int wishedNumberOfDemandingTrainer = 7;

        public static LocalTime lenghtOfMasterClass = LocalTime.of(2, 0);
    }

    public static class SeasonTicketConst {
        public static String LONG_LIMIT = "LONG_LIMIT";
        public static String SHORT_LIMIT = "SHORT_LIMIT";
        public static String UNLIMITED = "UNLIMIT";
        public static String NO_TICKET = "NO_TICKET";

    }

}
