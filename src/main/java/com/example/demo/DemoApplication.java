package com.example.demo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SpringBootApplication
@RestController
@RestControllerAdvice // activates ExceptionHandler
@Validated // activates method parameter & return validation
@Tag(name="DemoApp") // openapi controller name
public class DemoApplication {
	public static class Message {
		@NotNull(message = "required") // implies required: true, nullable: false
		String foo;

		public String getFoo() {
			return foo;
		}
	}

	@Valid // if invalid: throws ConstraintViolationException
	@PostMapping("/")
	public Message getMessage(
			@Valid // if invalid: throws MethodArgumentNotValidException
			@RequestBody Message message) {
		return message;
	}

	@ExceptionHandler(value = {MethodArgumentNotValidException.class}) // invoked when exception is thrown within the class
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void inputError(MethodArgumentNotValidException e) {
		System.out.println(e.getAllErrors());
	}

	@ExceptionHandler(value = {ConstraintViolationException.class}) // invoked when exception is thrown within the class
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void outputError(ConstraintViolationException e) {
		System.out.println(e.getConstraintViolations());
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
