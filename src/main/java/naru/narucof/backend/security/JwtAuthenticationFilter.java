package naru.narucof.backend.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private TokenProvider tokenProvider;

	/*
	 * 로그인을 하였다면 화면에서 던진 토큰이 존재한다 -> 토큰이 존재한다면 @AuthenticationPrincipal에 userId를 저장한다-> 컨트롤러의 인자 값으로 넘겨준다.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			// 화면에서 던진 토큰을 파싱한다.
			String token = parseBearerToken(request);
			
			log.info("Filter is running...");
			
			//만약 화면에서 던진 토큰이 존재한다면   @AuthenticationPrincipal에 userId를 저장한다.    (토큰 검사하기. JWT이므로 인가 서버에 요청 하지 않고도 검증 가능.)
			if (token != null && !token.equalsIgnoreCase("null")) {
				
				
				//화면에서 던진 토큰이존재한다면 파싱해서 userId를 가져오기. (위조 된 경우 익셉션 처리 된다.)
				String userId = tokenProvider.validateAndGetUserId(token);
				log.info("Authenticated user ID : " + userId );
				
				// 인증 완료; SecurityContextHolder에 등록해야 인증된 사용자라고 생각한다.
				AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userId, // <- 추후 컨트롤러의 파라미터인 @AuthenticationPrincipal (또는 principal) 로 불러올 수 있다.
								null, //
								AuthorityUtils.NO_AUTHORITIES
				);
				/*
				 * SecurityContext는 통행도장이 찍힌 통행증이다.(접근 주체와 인증에 대한 정보를 갖고있는 context
				 * 아래소스에 등록 해주면 WebSecurityConfig의 권한 통과된다. 
				 */
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
				securityContext.setAuthentication(authentication);

				//등록해주는 이유? : 추후 인증여부 확인을 위해.
				SecurityContextHolder.setContext(securityContext);
				
				
				
			}
		} catch (Exception ex) {
			logger.error("Could not set user authentication in security context", ex);
		}

		filterChain.doFilter(request, response);
	}

	private String parseBearerToken(HttpServletRequest request) {
		// Http 리퀘스트의 헤더를 파싱해 Bearer 토큰을 리턴한다.
		String bearerToken = request.getHeader("Authorization");

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
