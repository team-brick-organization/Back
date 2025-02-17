package com.brick.demo.auth.jwt;

import com.brick.demo.common.CustomException;
import com.brick.demo.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

	public static final String BEARER_TYPE = "Bearer";
	private static final long ACCESS_TOKEN_EXPIRE_TIME =
			1000 * 60 * 60 * 12; // 12시간 //TODO : 30분으로 바꾸기
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; //7일

	private final Key key;

	public TokenProvider(@Value("${jwt.secret}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public Token generateToken(Authentication authentication) {
		long now = (new Date()).getTime();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
		String accessToken = Jwts.builder()
				.setSubject(userDetails.getUsername())       // payload "sub": "name"  (현재 로직에서는 이메일)
				.setExpiration(accessTokenExpiresIn)        // payload "exp": 151621022 (ex)
				.claim("name", userDetails.getName()) // payload "name": 사용자이름
				.signWith(key, SignatureAlgorithm.HS256)    // header "alg": "HS512"
				.compact();

		String refreshToken = Jwts.builder()
				.setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();

		return Token.builder()
				.grantType(BEARER_TYPE)
				.accessToken(accessToken)
				.accessTokenExpiresIn(accessTokenExpiresIn.getTime())
				.refreshToken(refreshToken)
				.build();
	}

	public Authentication getAuthentication(String accessToken) {
		// 토큰 복호화
		Claims claims = parseClaims(accessToken);

		// UserDetails 객체를 만들어서 Authentication 리턴
		CustomUserDetails principal = new CustomUserDetails(claims.getSubject(),
				claims.get("name", String.class), "");
		return new UsernamePasswordAuthenticationToken(principal, "", Collections.emptyList());
	}


	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			throw new CustomException(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			throw new CustomException(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			throw new CustomException(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다.");
		}
	}

	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parser().setSigningKey(key).build().
					parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}
