package com.api.servicies;

import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class HostService {
    public String info() {
        try {
            return String.format("%s/%s", InetAddress.getLocalHost().getHostAddress(), InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
