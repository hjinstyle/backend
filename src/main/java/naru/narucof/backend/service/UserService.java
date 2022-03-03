package naru.narucof.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import naru.narucof.backend.model.UserEntity;
import naru.narucof.backend.persistence.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	//--------���̵� �űԻ���(sign up)-----------//
	public UserEntity create(final UserEntity userEntity) {
		if(userEntity == null || userEntity.getEmail() == null ) {
			throw new RuntimeException("Invalid arguments");
		}
		final String email = userEntity.getEmail();
		if(userRepository.existsByEmail(email)) {
			log.warn("Email already exists {}", email);
			throw new RuntimeException("Email already exists");
		}
		//insert�۾�
		return userRepository.save(userEntity);
	}

	//---------------�α���(sign in)---------//
	/*
	public UserEntity getByCredentials(final String email, final String password) {
		//����Ʈ �۾�
		return userRepository.findByEmailAndPassword(email, password);
	}
	*/
	
	public UserEntity getByCredentials(final String email, final String password, final PasswordEncoder encoder) {
		final UserEntity originalUser = userRepository.findByEmail(email);

		// matches �޼��带 �̿��� �н����尡 ������ Ȯ��
		if(originalUser != null && encoder.matches(password, originalUser.getPassword())) {
			return originalUser;
		}
		return null;
	}
}
