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
	
	//JWT������ �����ϰԳ��� �����ϱ� ���� ��α� ��
	//https://velog.io/@hellozin/JWT-Json-Web-Token
	//������ �ǽ� ����
	//https://charlie-choi.tistory.com/211
	//claim ���� ��
	//https://m.blog.naver.com/PostView.nhn?blogId=ithink3366&logNo=221362161213&proxyReferer=https:%2F%2Fwww.google.com%2F
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public ResponseEntity<String> login(@RequestBody AuthenticationRequest authenticationRequest){
		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();
		
		logger.info("username = " + username + "password = " + password);
		List<String> roles = new ArrayList<String>();
		roles.add("ROLE_MEMBER");
		
		//SecurityConstants�� JWT_SECRET �� ����� �о���δ�.
		byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
		
		/* 
		 *  jwt��ū�� �����ϴ� ����
		 *  (�����̶� ��ū�� Ż���ϴ°��� �����ϱ� ���� ���� ��������
		 *  ������ �޽��� ���� hash���� ������ ���Ű�� ��ȣȭ�� ��
		 *  ��ū�� �ڿ� �ٿ���
		 *  �̸� ���� �޽����� ����Ǿ����� HMAC���� ���� �� �� �ְ�
		 *  ���� HMAC���� ������� �ص� ���Ű�� �� �� ���� ������ ������ ��ƴ�)
		 *  
		 *  HMAC�� = hash(�����޽���)
		 *  �޽��� = {�����޽���}.{HMAC��}
		 */
		String token  = Jwts.builder()
				.signWith(Keys.hmacShaKeyFor(signingKey),SignatureAlgorithm.HS512) //������ ���Ű�� hs512��ȣȭ �˰��� �̿��� ������ ����
				.setHeaderParam("typ",SecurityConstants.TOKEN_TYPE) //����� �������� �����Ѵ�.
				.setIssuer(SecurityConstants.TOKEN_ISSUER) //��ū�߱���
				.setAudience(SecurityConstants.TOKEN_AUDIENCE) //��ū�� �߱� �޴���
				.setSubject(username) // ��ū�� ���� Ȥ�� ����(��ū�� ��� ������ ���Ѵٰ� �մϴ�)
				.setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 1000 * 60 * 60 * 24 = 1��. ��ȿ�Ⱓ�� 1�Ϸ� ����
				.claim("rol",roles) 
				.compact(); //��ū ����
		logger.info("token: " + token);
		return new ResponseEntity<String>(token,HttpStatus.OK);
	}
	
	@RequestMapping("/read")
	public ResponseEntity<String> read(@RequestHeader(value="Authorization") String header){
		logger.info("read: header" + header);
		//�Ű������� �޾ƿ� header index���� 7�� ������ ���ڿ� ���� �޾ƿ�
		String token = header.substring(7);
		
		logger.info("read:token" + token);
		
		//SecurityConstants�� JWT_SECRET �� ����� �о���δ�.
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
