package com.rak.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rak.booking.client.EventClient;
import com.rak.booking.client.NotificationClient;
import com.rak.booking.client.UserClient;
import com.rak.booking.model.Booking;
import com.rak.booking.repository.BookingRepository;
import com.rak.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private BookingRepository bookingRepository;
    private UserClient userClient;
    private EventClient eventClient;
    private NotificationClient notificationClient;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userClient = mock(UserClient.class);
        eventClient = mock(EventClient.class);
        notificationClient = mock(NotificationClient.class);
        bookingService = new BookingService(bookingRepository, userClient, eventClient,notificationClient);
    }

    @Test
    void testCreateBooking_Success() {
        Long userId = 1L;
        Long eventId = 2L;
        int seats = 2;

        Booking booking = Booking.builder()
                .id(1L)
                .userId(userId)
                .eventId(eventId)
                .numberOfSeats(seats)
                .bookingTime(LocalDateTime.now())
                .build();

        when(eventClient.getAvailableSeats(eventId)).thenReturn(5);
        doNothing().when(userClient).validateUser(userId);
        doNothing().when(eventClient).updateBookedSeats(eq(eventId), eq(Map.of("bookedSeats", seats)));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.bookTicket(userId, eventId, seats);
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }


    @Test
    void testCreateBooking_InsufficientSeats() {
        when(eventClient.getAvailableSeats(2L)).thenReturn(1);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            bookingService.bookTicket(1L, 2L, 5)
        );

        assertEquals("Not enough seats available.", ex.getMessage());
    }

    
    @Test
    void testGetBookingById_Found() {
        Booking booking = Booking.builder().id(1L).userId(1L).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getTicketDetails(1L);
        assertEquals(1L, result.getUserId());
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> bookingService.getTicketDetails(99L));
    }

    @Test
    void testGetBookingsByUser() {
        List<Booking> bookings = List.of(
                Booking.builder().id(1L).userId(1L).build(),
                Booking.builder().id(2L).userId(1L).build()
        );

        when(bookingRepository.findByUserId(1L)).thenReturn(bookings);

        List<Booking> result = bookingService.getByUserId(1L);
        assertEquals(2, result.size());
    }
    
    @Test
    void testCancelBooking_Success() {
        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .eventId(2L)
                .numberOfSeats(2)
                .bookingTime(LocalDateTime.now())
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        doNothing().when(bookingRepository).deleteById(1L);
        doNothing().when(eventClient).updateBookedSeats(eq(2L), eq(Map.of("bookedSeats", -2)));
        doNothing().when(notificationClient).send(any());

        bookingService.cancel(1L);

//        verify(bookingRepository).deleteById(1L);
//        verify(eventClient).updateBookedSeats(2L, Map.of("bookedSeats", -2));
//        verify(notificationClient).send(argThat(notification ->
//                notification.getUserId().equals(1L) &&
//                notification.getEventId().equals(2L) &&
//                "CANCELLATION".equals(notification.getType())));
    }

}
