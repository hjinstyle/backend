package naru.narucof.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Service;

import naru.narucof.backend.model.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenProvider {
	private static final String SECRET_KEY = "NMA8JPctFuna59f5";

	/*로그인 시 토근 암호화 생성 메서드
	 * 
	 */
	public String create(UserEntity userEntity) {
		// 기한 지금으로부터 1일로 설정
		Date expiryDate = Date.from(
						Instant.now()
						.plus(1, ChronoUnit.DAYS));

		/*
		{ // header
		  "alg":"HS512"
		}.
		{ // payload
		  "sub":"40288093784915d201784916a40c0001",
		  "iss": "demo app",
		  "iat":1595733657,
		  "exp":1596597657
		}.
		// SECRET_KEY를 이용해 서명한 부분
		Nn4d1MOVLZg79sfFACTIpCPKqWmpZMZQsbNrXdJJNWkRv50_l7bPLQPwhMobT4vBOG6Q3JYjhDrKFlBSaUxZOg
		 */
		// JWT Token 생성
		return Jwts.builder()
						// header에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
						.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
						// payload에 들어갈 내용
						.setSubject(userEntity.getId()) // sub     //subject에 아이디를 셋팅 해줬다. 추후 디코딩 시 subject 추출한다.
						.setIssuer("demo app") // iss
						.setIssuedAt(new Date()) // iat
						.setExpiration(expiryDate) // exp
						.compact();
	}

	/*화면접속 시 마다 실행되는 토큰 복호화 파싱 메서드
	 * 
	 * */
	public String validateAndGetUserId(String token) {
		/*내설명 : 화면에서던진 토큰던짐-> 인터셉터에서 토큰이존재 판단-> 토큰 존재한다면 
		 *     -> 이메서드에서 해당 토큰을 인자로 받아서 파싱한다(파싱방법:SECRET_KEY상수로 토큰을 복호화함) -> 복호화된 값 중 userId를 리턴해준다.
		 */
		
		// parseClaimsJws메서드가 Base 64로 디코딩 및 파싱.
		// 즉, 헤더와 페이로드를 setSigningKey로 넘어온 시크릿을 이용 해 서명 후, token의 서명 과 비교.
		// 위조되지 않았다면 페이로드(Claims) 리턴
		// 그 중 우리는 userId가 필요하므로 getBody를 부른다.
		Claims claims = Jwts.parser()
						.setSigningKey(SECRET_KEY)  //위에 작성한 상수키로 서명
						.parseClaimsJws(token)  //base64로 디코딩 및 파싱
						.getBody(); //바디 내용 추출

		return claims.getSubject();    //인코딩 시 서브젝트에 아이디를 셋팅해줬던거 추출한다.
	}
}
