package az.aistgroup.controller;

import az.aistgroup.domain.dto.HallDto;
import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.service.HallService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/halls")
public class HallController {
    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public ResponseEntity<List<HallDto>> getAllHalls() {
        List<HallDto> halls = hallService.getAllHalls();
        return new ResponseEntity<>(halls, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HallDto> getHallById(@PathVariable("id") Long id) {
        HallDto hall = hallService.getHallById(id);
        return new ResponseEntity<>(hall, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HallDto> addHall(@Valid @RequestBody HallDto hallDto) {
        HallDto hall = hallService.addHall(hallDto);
        return new ResponseEntity<>(hall, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HallDto> updateHall(@PathVariable("id") Long id, @Valid @RequestBody HallDto hallDto) {
        HallDto hall = hallService.updateHall(id, hallDto);
        return new ResponseEntity<>(hall, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponseDto> deleteHall(@PathVariable("id") Long id) {
        hallService.deleteHall(id);
        var response = new OperationResponseDto(true, "Hall with " + id + " deleted...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
