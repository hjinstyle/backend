package naru.narucof.backend.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import naru.narucof.backend.model.TodoEntity;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, String> {
	//@Query(value="select * from Todo t where t.userId = ?1", nativeQuery=true)
	List<TodoEntity> findByUserId(String userId);
}
