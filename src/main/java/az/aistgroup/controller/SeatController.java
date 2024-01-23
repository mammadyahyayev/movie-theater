package az.aistgroup.controller;

import az.aistgroup.domain.dto.SeatDto;
import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    public ResponseEntity<List<SeatDto>> getAllSeats() {
        List<SeatDto> seats = seatService.getAllSeats();
        return new ResponseEntity<>(seats, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatDto> getSeatById(@PathVariable("id") Long id) {
        SeatDto seat = seatService.getSeatById(id);
        return new ResponseEntity<>(seat, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SeatDto> addSeat(@Valid @RequestBody SeatDto seatDto) {
        SeatDto seat = seatService.addSeat(seatDto);
        return new ResponseEntity<>(seat, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatDto> updateSeat(@PathVariable("id") Long id, @Valid @RequestBody SeatDto seatDto) {
        SeatDto seat = seatService.updateSeat(id, seatDto);
        return new ResponseEntity<>(seat, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponseDto> deleteSeat(@PathVariable("id") Long id) {
        seatService.deleteSeat(id);
        var response = new OperationResponseDto(true, "Seat with " + id + " deleted...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
