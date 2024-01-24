package az.aistgroup.exception;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(int seatNumber) {
        super("Seat " + seatNumber + " is already booked!");
    }
}
