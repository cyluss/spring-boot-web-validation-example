package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@SpringBootApplication
@RestController
@RestControllerAdvice // activates ExceptionHandler
@Validated // required by method parameter & return validation
@Tag(name="DemoApp") // openapi controller name
public class DemoApplication {
	public enum MessageEnum {
		REGULAR("REGULAR", "Regular"),
		BOLD("BOLD", "Bold"),
		ITALIC("ITALIC", "Italic");

		String code;
		String description;

		MessageEnum(String code, String description) {
			this.code = code;
			this.description = description;
		}
	}
	public static class Message {
		@NotNull(message = "foo is required") // implies required: true, nullable: false
		Integer foo; // NOT int -- null is SILENTLY converted to 0

		@NotNull(message = "message enum is required") // implies required: true, nullable: false
		MessageEnum messageEnum;

		@NotNull(message = "bar is required") // implies required: true, nullable: false
		String bar;

		@NotNull(message = "baz is required") // implies required: true, nullable: false
		@NotBlank // requires baz.length > 0
		String baz;

		@NotNull(message = "xyz is required") // implies required: true, nullable: false
		Boolean xyz; // NOT boolean -- null is SILENTLY converted to false

		@ArraySchema(schema = @Schema(nullable = true)) // required: false by default
		@Valid // activates element type validation -- ex) NO [{}]
		List<@NotNull Spam> spams; // requires NO null in list -- ex) NO [null]

		@Hidden // exclude from openapi schema
		@JsonIgnore // exclude from JSON serialization / deserialization
		String qux;

		public Integer getFoo() {
			return foo;
		}

		public MessageEnum getMessageEnum() {
			return messageEnum;
		}

		public String getBar() {
			return bar;
		}

		public String getBaz() {
			return baz;
		}

		public Boolean getXyz() { // NOT isXyz -- some openapi generators does NOT support isXXX method
			return xyz;
		}

		public List<Spam> getSpams() {
			return spams;
		}

		public String getQux() {
			return qux;
		}
	}

	public static class Spam {
		@NotNull
		Integer spamHam;
		@NotNull
		String spamEgg;

		public Integer getSpamHam() {
			return spamHam;
		}

		public String getSpamEgg() {
			return spamEgg;
		}
	}

	@Valid // activates return validation. If return is invalid, throws ConstraintViolationException
	@PostMapping("/")
	public Message getMessage(
			@Valid // activates parameter validation. If parameter is invalid, throws MethodArgumentNotValidException
			@RequestBody Message message) {
		System.out.println(message.getQux());
		if (message.qux == null) {
			message.qux = "";
		}
		System.out.println(message.getQux());
		return message;
	}

	@ExceptionHandler(value = {MethodArgumentNotValidException.class}) // invoked when the specified exception is thrown within the class
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void onInputValidationError(MethodArgumentNotValidException e) {
		System.out.println(e.getAllErrors());
	}

	@ExceptionHandler(value = {ConstraintViolationException.class}) // invoked when the specified exception is thrown within the class
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void onOutputValidationError(ConstraintViolationException e) {
		System.out.println(e.getConstraintViolations());
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
