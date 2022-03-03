package naru.narucof.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import naru.narucof.backend.dto.ResponseDTO;
import naru.narucof.backend.dto.UserDTO;
import naru.narucof.backend.model.UserEntity;
import naru.narucof.backend.security.TokenProvider;
import naru.narucof.backend.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenProvider tokenProvider;
	
	//@Autowired
	private PasswordEncoder passwordEncoder= new BCryptPasswordEncoder();

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
		try {
			// ������Ʈ�� �̿��� ������ ���� �����
			UserEntity user = UserEntity.builder()
							.email(userDTO.getEmail())
							.username(userDTO.getUsername())
							.password(passwordEncoder.encode(userDTO.getPassword()))
							.build();
			// ���񽺸� �̿��� �������丮�� ���� ����
			UserEntity registeredUser = userService.create(user);
			UserDTO responseUserDTO = UserDTO.builder()
							.email(registeredUser.getEmail())
							.id(registeredUser.getId())
							.username(registeredUser.getUsername())
							.build();
			// ���� ������ �׻� �ϳ��̹Ƿ� �׳� ����Ʈ�� �������ϴ� ResponseDTO�� ������� �ʰ� �׳� UserDTO ����.
			return ResponseEntity.ok(responseUserDTO);
		} catch (Exception e) {
			// ���ܰ� ���� ��� bad �������� ����.
			ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
			return ResponseEntity
							.badRequest()
							.body(responseDTO);
		}
	}

	//-----------�α���---------//
	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
		
		//ȭ�鿡�� �Ѿ�� �̸���, ����� db������� ���� ��ġ�ϴ� �����͸� ����Ʈ�ؿ�.
		UserEntity user = userService.getByCredentials(userDTO.getEmail(),userDTO.getPassword(),passwordEncoder);

		//������ db�� �����Ѵٸ�
		if(user != null) {

			//��ū ����
			final String token = tokenProvider.create(user);

			//ȭ�鿡 ���� ���� ������ ����
			final UserDTO responseUserDTO = UserDTO.builder()
							.email(user.getEmail())
							.id(user.getId())
							.token(token)
							.build();
			return ResponseEntity.ok().body(responseUserDTO);
		} else {
			ResponseDTO responseDTO = ResponseDTO.builder()
							.error("������ ���ų� �н����尡 Ʋ�Ƚ��ϴ�.")
							.build();
			return ResponseEntity
							.badRequest()
							.body(responseDTO);
		}
	}
}
