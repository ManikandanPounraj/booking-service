package com.rak.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

	private Long userId;
	private Long eventId;
	private String type; // "BOOKING" or "CANCELLATION"

}
