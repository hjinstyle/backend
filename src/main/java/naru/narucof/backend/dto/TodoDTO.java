package naru.narucof.backend.dto;

import naru.narucof.backend.model.TodoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TodoDTO {
	private String id; //이 오브젝트의 아이디
	private String title; //Todo 타이틀(예:운동하기)
	private boolean done; //true - todo를 완료한 경우(checked) 

	public TodoDTO(final TodoEntity entity) {

		this.id = entity.getId();
		this.title = entity.getTitle();
		this.done = entity.isDone();
	}
	
	public static TodoEntity toEntity(final TodoDTO dto) {
		return TodoEntity.builder()
				.id(dto.getId())
				.title(dto.getTitle())
				.done(dto.isDone())
				.build();
	}
}
