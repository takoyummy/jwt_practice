package org.hdcd.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hdcd.consatnts.SecurityConstants;
import org.hdcd.domain.AuthenticationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@RestController
public class LoginController {
	
	//JWT개념을 간략하게나마 이해하기 좋은 블로그 글
	//https://velog.io/@hellozin/JWT-Json-Web-Token
	//간단한 실습 예제
	//https://charlie-choi.tistory.com/211
	//claim 관련 글
	//https://m.blog.naver.com/PostView.nhn?blogId=ithink3366&logNo=221362161213&proxyReferer=https:%2F%2Fwww.google.com%2F
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public ResponseEntity<String> login(@RequestBody AuthenticationRequest authenticationRequest){
		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();
		
		logger.info("username = " + username + "password = " + password);
		List<String> roles = new ArrayList<String>();
		roles.add("ROLE_MEMBER");
		
		//SecurityConstants의 JWT_SECRET 값 상수를 읽어들인다.
		byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
		
		/* 
		 *  jwt토큰의 서명하는 과정
		 *  (서명이란 토큰을 탈취하는것을 방지하기 위해 나온 개념으로
		 *  원래의 메시지 값에 hash값을 추출해 비밀키로 복호화한 뒤
		 *  토큰의 뒤에 붙여줌
		 *  이를 통해 메시지가 변경되었는지 HMAC값을 통해 알 수 있고
		 *  새로 HMAC값을 만드려고 해도 비밀키를 알 수 없기 때문에 변조가 어렵다)
		 *  
		 *  HMAC값 = hash(원본메시지)
		 *  메시지 = {원본메시지}.{HMAC값}
		 */
		String token  = Jwts.builder()
				.signWith(Keys.hmacShaKeyFor(signingKey),SignatureAlgorithm.HS512) //지정한 비밀키와 hs512암호화 알고리즘 이용한 서명의 과정
				.setHeaderParam("typ",SecurityConstants.TOKEN_TYPE) //헤더에 유형값을 설정한다.
				.setIssuer(SecurityConstants.TOKEN_ISSUER) //토큰발급자
				.setAudience(SecurityConstants.TOKEN_AUDIENCE) //토큰을 발급 받는자
				.setSubject(username) // 토큰의 주제 혹은 제목(토큰에 담길 내용을 뜻한다고 합니다)
				.setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 1000 * 60 * 60 * 24 = 1일. 유효기간을 1일로 설정
				.claim("rol",roles) 
				.compact(); //토큰 생성
		logger.info("token: " + token);
		return new ResponseEntity<String>(token,HttpStatus.OK);
	}
	
	@RequestMapping("/read")
	public ResponseEntity<String> read(@RequestHeader(value="Authorization") String header){
		logger.info("read: header" + header);
		//매개변수로 받아온 header index값이 7인 이후의 문자열 값을 받아옴
		String token = header.substring(7);
		
		logger.info("read:token" + token);
		
		//SecurityConstants의 JWT_SECRET 값 상수를 읽어들인다.
		byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
		
		Jws<Claims> parsedToken = Jwts.parser()
				.setSigningKey(signingKey)
				.parseClaimsJws(token);
		
		String username = parsedToken.getBody().getSubject();
		logger.info("username : " + username);
		List<String> roles  = (List<String>) parsedToken.getBody().get("rol");
		
		logger.info("roles: " + roles);
		
		return new ResponseEntity<String>(parsedToken.toString(), HttpStatus.OK);
	}
	
	
}
