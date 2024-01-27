# Rules for Tickets

## TODOS

- Add a new parameter to session entity (isClosed)

- Write a scheduler for checking session closed or not for only today, if they are closed
  then make them close

- Create ticketViewDto (show owner, price, seat, hall, session, movie)

## Buying a new Ticket process

1. If the session has already begun, then user can't buy a ticket. (maybe add a new parameter, to say session is closed
   bool param)
2. If the session hasn't begun yet, then user can buy a ticket.
    1. Ticket price will be decreased from User's balance if there is a sufficient amount of money.
    2. Otherwise, `InSufficientFundsException` will be thrown.

## Refunding a Ticket process

1. If ticket isn't found in the system, then `ResourceNotFoundException` will be thrown
2. If the ticket is found:
    1. The owner of the ticket is different from refunding user, then `InvalidRequestException`
       will be thrown.
    2. The owner of the ticket and refunding user are the same, but session is already begun or, 1 hour left for session
       to begin, then user can't refund and `TicketExpiredException` will be thrown.
    3. The owner of the ticket and refunding user are the same, and there are more hours for session to begin, then user
       can refund. The price of the session will be returned to user's balance.

## Updating a Ticket process (ADMIN users can update)

1. If the ticket isn't found in the system, then `ResourceNotFoundException` will be thrown.
2. If the ticket is found in the system:
    1. Ticket's owner wants to change the owner of the ticket to somebody else (session and seat are same), then price
       will be returned to the current owner and will be removed from the new owner's balance.
    2. Ticket's owner wants to change the session or seat then he must refund the ticket, then buy a new ticket.


## Swapping Tickets