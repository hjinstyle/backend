package naru.narucof.backend;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Builder
@RequiredArgsConstructor
public class BackendModel {

	@NonNull
	private String id;
	
}
