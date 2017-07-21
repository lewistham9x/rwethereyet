package s10171744d.rwethereyet.model;

/**
 * Lewis Tham Jee Peng | Group 9 | S10171744D
 */

public class SettingVar {
    private static Integer sensitivity = 50; //10 is too small radius for checking nearby bus stop, 50 is perfect
    private static Integer stopsToAlert = 1;

    public static Integer getSensitivity() {
        return sensitivity;
    }

    public static Integer getStopsToAlert() {
        return stopsToAlert;
    }
}
