package com.rak.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.rak.booking.client.EventClient;
import com.rak.booking.client.UserClient;
import com.rak.booking.model.Booking;
import com.rak.booking.repository.BookingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

	private final BookingRepository repo;
	private final UserClient userClient;
	private final EventClient eventClient;

	public Booking book(Long userId, Long eventId, int seats) {
		log.info("Processing booking for user {} for event {} ({} seats)", userId, eventId, seats);

		try {
			userClient.validateUser(userId);
			
			int available = eventClient.getAvailableSeats(eventId);
			if (available < seats) {
				throw new IllegalArgumentException("Not enough seats available.");
			}

			Booking booking = Booking.builder().userId(userId).eventId(eventId).numberOfSeats(seats)
					.bookingTime(LocalDateTime.now()).build();

			Booking saved = repo.save(booking);
			eventClient.updateBookedSeats(eventId, Map.of("bookedSeats", available - seats));

			log.info("Booking confirmed with ID: {}", saved.getId());
			return saved;
		} catch (Exception e) {
			log.error("Booking failed: {}", e.getMessage());
			throw e;
		}
	}

	public Booking get(Long id) {
		return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Booking not found"));
	}

	public List<Booking> getByUser(Long userId) {
		return repo.findByUserId(userId);
	}

	public void cancel(Long id) {
		Booking b = get(id);
		log.warn("Cancelling booking ID: {}", id);
		repo.delete(b);
		// Optional: reverse booked seats via event service
	}
}
