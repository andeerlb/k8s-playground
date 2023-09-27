package com.api.controllers;

import com.api.servicies.HostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.InetAddress;

@RestController
@RequestMapping("/host")
@AllArgsConstructor
public class HostController {

    private final HostService service;

    @GetMapping("/info")
    public Mono<String> host() {
        return Mono.just(service.info());
    }
}
