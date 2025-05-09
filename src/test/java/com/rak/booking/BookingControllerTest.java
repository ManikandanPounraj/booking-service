package com.rak.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.rak.booking.controller.BookingController;
import com.rak.booking.model.Booking;
import com.rak.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    private BookingService bookingService;
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        bookingService = mock(BookingService.class);
        bookingController = new BookingController(bookingService);
    }

    @Test
    void testCreateBooking() {
        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .eventId(2L)
                .numberOfSeats(2)
                .bookingTime(LocalDateTime.now())
                .build();

        when(bookingService.bookTicket(1L, 2L, 2)).thenReturn(booking);

        ResponseEntity<Booking> response = bookingController.book(1L, 2L, 2);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getUserId());
    }

    @Test
    void testGetBookingById_Found() {
        Booking booking = Booking.builder().id(1L).userId(1L).build();
        when(bookingService.getTicketDetails(1L)).thenReturn(booking);

        ResponseEntity<Booking> response = bookingController.get(1L);
        assertEquals(1L, response.getBody().getUserId());
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingService.getTicketDetails(999L)).thenThrow(new NoSuchElementException("Not found"));
        assertThrows(NoSuchElementException.class, () -> bookingController.get(999L));
    }

    @Test
    void testGetBookingsByUser() {
        List<Booking> bookings = List.of(
            Booking.builder().id(1L).userId(1L).build(),
            Booking.builder().id(2L).userId(1L).build()
        );

        when(bookingService.getByUserId(1L)).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getByUser(1L);
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCancelBooking() {
        doNothing().when(bookingService).cancel(1L);
        ResponseEntity<Void> response = bookingController.cancel(1L);
        assertEquals(204, response.getStatusCodeValue());
        verify(bookingService).cancel(1L);
    }
}
