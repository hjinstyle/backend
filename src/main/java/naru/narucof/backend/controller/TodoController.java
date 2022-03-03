package naru.narucof.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import naru.narucof.backend.dto.ResponseDTO;
import naru.narucof.backend.dto.TodoDTO;
import naru.narucof.backend.model.TodoEntity;
import naru.narucof.backend.service.TodoService;


@RestController
@RequestMapping("todo") //���ҽ�
public class TodoController {
	@Autowired
	private TodoService service;

	@GetMapping("/test")
	public ResponseEntity<?> testTodo() {
		String str = service.testService(); // �׽�Ʈ ���� ���
		List<String> list = new ArrayList<>();
		list.add(str);
		ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
		return ResponseEntity.ok(response);
	}
	/*
	 * �Խñ� ��� ��ȸ (���� localhost:3030/ �϶��� ����Ѵ�.
	 */
	@GetMapping
	public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId) {
		System.out.println("UserID : " + userId);

		// (1) ���� �޼����� retrieve�޼��带 ����� Todo����Ʈ�� �����´�
		List<TodoEntity> entities = service.retrieve(userId);

		// (2) �ڹ� ��Ʈ���� �̿��� ���ϵ� ��ƼƼ ����Ʈ�� TodoDTO����Ʈ�� ��ȯ�Ѵ�.
		List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

		// (6) ��ȯ�� TodoDTO����Ʈ�� �̿���ResponseDTO�� �ʱ�ȭ�Ѵ�.
		ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

		// (7) ResponseDTO�� �����Ѵ�.
		return ResponseEntity.ok().body(response);
	}	
	
	/*
	 * �Խñ� �߰���ư Ŭ��
	 */
	@PostMapping
	public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
		try {
			// (1) TodoEntity�� ��ȯ�Ѵ�.
			TodoEntity entity = TodoDTO.toEntity(dto);

			// (2) id�� null�� �ʱ�ȭ �Ѵ�. ���� ��ÿ��� id�� ����� �ϱ� �����̴�.
			entity.setId(null);

			// (3) �ӽ� ���� ���̵� ���� �� �ش�. �� �κ��� 4�� ������ �ΰ����� ���� �� �����̴�. ������ ������ �ΰ� ����� �����Ƿ� �� ����(temporary-user)�� �α��� ���� ��� ������ ���ø����̼��� ���̴�
			entity.setUserId(userId);

			// (4) ���񽺸� �̿��� Todo��ƼƼ�� �����Ѵ�. ���񽺴ܿ����� ��ƼƼ�� ������ ó���ϰ�, ��ƼƼ(����Ʈ)���·� �������ش�.
			List<TodoEntity> entities = service.create(entity);

			
			// (5) �ڹ� ��Ʈ���� �̿��� ���ϵ� ��ƼƼ ����Ʈ�� TodoDTO����Ʈ�� ��ȯ�Ѵ�.
			//���񽺴ܿ��� �޾ƿ� ��ƼƼlist�� dto list �� ���� ���ش�.
			List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
			//List<TodoDTO> dtos = entities.stream().map(x-> new TodoDTO(x)).collect(Collectors.toList());

			// (6) ��ȯ�� TodoDTO����Ʈ�� �̿���ResponseDTO�� �ʱ�ȭ�Ѵ�.
			//ResponseDTO<T>�� <TodoDTO>�� ���׸� �����Ͽ� data������ todoDTO�� �������ش�. 
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
			
						
			return ResponseEntity.ok().body(response);
			
		}catch(Exception e) {
			String error=e.getMessage();
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	


	/*
	 * �Խñ� ���� ��ư Ŭ��
	 */
	@PutMapping
	public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
		// (1) dto�� entity�� ��ȯ�Ѵ�.
		TodoEntity entity = TodoDTO.toEntity(dto);

		// (2) id�� temporaryUserId�� �ʱ�ȭ �Ѵ�. ����� 4�� ������ �ΰ����� ���� �� �����̴�.
		entity.setUserId(userId);

		// (3) ���񽺸� �̿��� entity�� ������Ʈ �Ѵ�.
		List<TodoEntity> entities = service.update(entity);

		// (4) �ڹ� ��Ʈ���� �̿��� ���ϵ� ��ƼƼ ����Ʈ�� TodoDTO����Ʈ�� ��ȯ�Ѵ�.
		List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

		// (5) ��ȯ�� TodoDTO����Ʈ�� �̿���ResponseDTO�� �ʱ�ȭ�Ѵ�.
		ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

		// (6) ResponseDTO�� �����Ѵ�.
		return ResponseEntity.ok().body(response);
	}

	/*
	 * �Խñ� �� ������ ������ Ŭ�� �� ����
	 */
	@DeleteMapping
	public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
		try {
			// (1) TodoEntity�� ��ȯ�Ѵ�.
			TodoEntity entity = TodoDTO.toEntity(dto);

			// (2) �ӽ� ���� ���̵� ���� �� �ش�. �� �κ��� 4�� ������ �ΰ����� ���� �� �����̴�. ������ ������ �ΰ� ����� �����Ƿ� �� ����(temporary-user)�� �α��� ���� ��� ������ ���ø����̼��� ���̴�
			entity.setUserId(userId);

			// (3) ���񽺸� �̿��� entity�� ���� �Ѵ�.
			List<TodoEntity> entities = service.delete(entity);

			// (4) �ڹ� ��Ʈ���� �̿��� ���ϵ� ��ƼƼ ����Ʈ�� TodoDTO����Ʈ�� ��ȯ�Ѵ�.
			List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

			// (5) ��ȯ�� TodoDTO����Ʈ�� �̿���ResponseDTO�� �ʱ�ȭ�Ѵ�.
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

			// (6) ResponseDTO�� �����Ѵ�.
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			// (8) Ȥ�� ���ܰ� ���� ��� dto��� error�� �޽����� �־� �����Ѵ�.
			String error = e.getMessage();
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	
	/*
	@GetMapping("/test")
	public ResponseEntity<?> testTodo() {
		String str = service.testService();
		List<String> list = new ArrayList<>();
		list.add("my id is "+ str);
		ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
		return ResponseEntity.ok().body(response);
	}	
	*/


}
