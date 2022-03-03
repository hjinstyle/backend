package naru.narucof.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import naru.narucof.backend.dto.ResponseDTO;
import naru.narucof.backend.dto.TestRequestBodyDTO;

@RestController
@RequestMapping("test") //¸®¼Ò½º
public class TestController {
	@GetMapping("testGet")
	public String testController() {
		return "Hello testGet";
		
	}
	@GetMapping("/{id}")
	public String test123(@PathVariable(required=false) String id)
	{
		return "hello world! id : "+id;
	}
	
	@GetMapping("/testRequest")
	public String test456(@RequestParam(required=false) String id)
	{
		return "hello world! id : "+id;
	}
	@GetMapping("/testRequestBody")
	public String testControllerRequestBody(@RequestBody TestRequestBodyDTO testRequestBodyDTO) {
		return "Hello world! id : " + testRequestBodyDTO.getId() + "Message : " + testRequestBodyDTO.getMessage();
	}
	@GetMapping("/testResponseBody")
	public ResponseDTO<String> testControllerResponseBody() {
		List<String> list = new ArrayList<>();
		list.add("hello world! I'm responseDTO");
		ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
		return response;
	}
	@GetMapping("/testResponseEntity")
	public ResponseEntity<?> testControllerResponseEntity() {
		List<String> list = new ArrayList<>();
		list.add("hello world! I'm responseEntity. and you got 400!");
		ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
		return ResponseEntity.ok().body(response);
	}	
}










