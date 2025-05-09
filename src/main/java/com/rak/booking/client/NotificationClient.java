package com.rak.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.rak.booking.model.NotificationDto;

@FeignClient(name = "notification-service", url = "http://localhost:8085")
public interface NotificationClient {
    @PostMapping("/api/notify")
    void send(@RequestBody NotificationDto notification);
}