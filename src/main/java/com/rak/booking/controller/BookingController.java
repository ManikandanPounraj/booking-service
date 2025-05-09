package com.rak.booking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rak.booking.model.Booking;
import com.rak.booking.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

	private final BookingService service;

	@PostMapping
	@Operation(summary = "Book tickets", description = "Books tickets after checking user validity and seat availability")
	public ResponseEntity<Booking> book(@RequestParam Long userId, @RequestParam Long eventId,
			@RequestParam int seats) {
		return ResponseEntity.ok(service.bookTicket(userId, eventId, seats));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get booking by ID")
	public ResponseEntity<Booking> get(@PathVariable Long id) {
		return ResponseEntity.ok(service.getTicketDetails(id));
	}

	@GetMapping("/user/{userId}")
	@Operation(summary = "Get bookings by user")
	public ResponseEntity<List<Booking>> getByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(service.getByUserId(userId));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Cancel booking")
	public ResponseEntity<Void> cancel(@PathVariable Long id) {
		service.cancel(id);
		return ResponseEntity.noContent().build();
	}
}
