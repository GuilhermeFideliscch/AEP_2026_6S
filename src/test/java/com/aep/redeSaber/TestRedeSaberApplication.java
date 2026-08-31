package com.aep.redeSaber;

import org.springframework.boot.SpringApplication;

public class TestRedeSaberApplication {

	public static void main(String[] args) {
		SpringApplication.from(RedeSaberApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
