package com.uniremington.alparque.sync;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SincronizacionJob {

	@Scheduled(cron = "0 */30 * * * *")
	public void ejecutarRevisionSincronizacion() {
		log.info("SincronizacionJob ejecutado: revision periodica de lotes offline");
	}
}
