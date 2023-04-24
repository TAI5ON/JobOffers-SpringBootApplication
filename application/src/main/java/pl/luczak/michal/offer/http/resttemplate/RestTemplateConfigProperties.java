package pl.luczak.michal.offer.http.resttemplate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties(prefix = "job-offers.rest-template-config")
public record RestTemplateConfigProperties(
        @Name("read-time-out") int readTimeOut,
        @Name("connection-time-out") int connectionTimeOut
) {}
