package az.aistgroup.domain.enumeration;

public enum MovieSessionTime {
    MORNING(10), EVENING(19);

    private final int hourOfDay;

    MovieSessionTime(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }
}
