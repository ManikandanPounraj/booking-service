package com.rak.booking.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "event-service", url = "http://localhost:8082")
public interface EventClient {
	@GetMapping("/api/events/{id}/availability")
	int getAvailableSeats(@PathVariable Long id);

	@PutMapping("/api/events/{id}/bookedSeats")
	void updateBookedSeats(@PathVariable Long id, @RequestBody Map<String, Integer> req);
}
