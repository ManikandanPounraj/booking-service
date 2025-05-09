package com.rak.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.rak.booking.client.EventClient;
import com.rak.booking.client.NotificationClient;
import com.rak.booking.client.UserClient;
import com.rak.booking.model.Booking;
import com.rak.booking.model.NotificationDto;
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
	private final NotificationClient notificationClient;
	

	public Booking bookTicket(Long userId, Long eventId, int seats) {
		log.info("Processing booking for user {} for event {} ({} seats)", userId, eventId, seats);

		try {
			userClient.validateUser(userId);
			
			int available = eventClient.getAvailableSeats(eventId);
			if (available < seats) {
				log.info("Seats are not available");
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

	public Booking getTicketDetails(Long id) {
		return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Booking not found"));
	}

	public List<Booking> getByUserId(Long userId) {
		return repo.findByUserId(userId);
	}

	public void cancel(Long id) {
		Booking booking = getTicketDetails(id);
		log.warn("Cancelling booking ID: {} and initiating notification", id);
		
		
		repo.delete(booking);	
		
		NotificationDto dto = NotificationDto.builder()
		        .userId(booking.getUserId())
		        .eventId(booking.getEventId())
		        .type("CANCELLATION")
		        .build();

		    notificationClient.send(dto);
	}
}
