package ru.kata.blockchain.infrastructure;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication(scanBasePackages = "ru.kata.blockchain")
@RestController
public class DemoApplication {


//test
	@GetMapping("/")
	public String home() {
		return "Spring is here!";
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
