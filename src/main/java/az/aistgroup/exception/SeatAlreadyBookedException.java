package az.aistgroup.exception;

/**
 * The exception will be thrown when {@link az.aistgroup.domain.entity.User} buys
 * a {@link az.aistgroup.domain.entity.Ticket} for already occupied {@link az.aistgroup.domain.entity.Seat}.
 */
public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(int seatNumber) {
        super("Seat " + seatNumber + " is already booked!");
    }
}
