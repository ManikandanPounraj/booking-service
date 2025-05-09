package com.rak.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rak.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByUserId(Long userId);
}
