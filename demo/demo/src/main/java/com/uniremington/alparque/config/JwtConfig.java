package com.uniremington.alparque.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class JwtConfig {

    @Value("${jwt.secret}")
    public String secret;
    
    @Value("${jwt.expiration}")
    public long expiration; //la expiración es en milisegundos

    
}
