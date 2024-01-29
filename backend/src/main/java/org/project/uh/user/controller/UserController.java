package org.project.uh.user.controller;

import java.util.List;
import java.util.Map;

import org.project.uh.user.dto.MypageDto;
import org.project.uh.user.dto.UserDto;
import org.project.uh.user.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=UTF8")
@Tag(name = "유저 api")
public class UserController {
	private final UserService service;

	private final String clientId = "4fffa78521feee5e1eb947c704c08cf2"; // 카카오 앱의 Client ID
	private final String redirectUri = "http://localhost:3000/callback/kakao"; // Redirect URI

	// 유저 목록
	@Operation(
		summary = "유저 목록"
	)
	@GetMapping("/user")
	public List<UserDto> listUser() {
		return service.listUser();
	}

	@Operation(
		summary = "유저 가입"
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "가입 성공"),
		@ApiResponse(responseCode = "400", description = "중복된 아이디")
	})
	// 회원가입
	@PostMapping("/user/join")
	public ResponseEntity<String> insertUser(@RequestBody UserDto dto) {
		int result = service.insertUser(dto);
		if (result == 0) {
			return new ResponseEntity<>("중복된 아이디", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("가입 성공", HttpStatus.OK);
	}

	// 유저 정보 확인
	@Operation(
		summary = "로그인 상태 확인",
		description = "로그인 상태라면 user정보를 아니라면 null을 반환"
	)
	@GetMapping("/user/check")
	public ResponseEntity<Object> userCheck(HttpSession session) {
		System.out.println("요청 들어옴");
		// 세션에서 'user' 속성 가져오기
		UserDto user = (UserDto)session.getAttribute("user");
		if (user != null) {
			// 사용자 정보가 세션에 있으면, 해당 정보 반환
			System.out.println(user);
			return new ResponseEntity<>(user, HttpStatus.OK);
		} else {
			// 사용자 정보가 세션에 없으면, null 또는 적절한 응답 반환
			System.out.println("null입니다");
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}

	// 로그인
	@Operation(
		summary = "로그인"
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "400", description = "로그인 오류")
	})
	@PostMapping("/user/login")
	public ResponseEntity<Object> login(@RequestBody UserDto dto, HttpSession session) {
		UserDto result = service.login(dto);
		if (result == null) {
			return new ResponseEntity<>("로그인 오류", HttpStatus.BAD_REQUEST);
		}
		// 로그인 성공
		result.setUserPassword(null);
		session.setAttribute("user", result);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// 로그아웃
	@Operation(
		summary = "로그아웃"
	)
	@PostMapping("/user/logout")
	public ResponseEntity<Object> logout(HttpSession session) {
		session.invalidate();
		return new ResponseEntity<>("로그아웃", HttpStatus.OK);
	}

	// 닉네임 생성
	@Operation(
		summary = "닉네임 생성",
		description = "처음 로그인한 유저의 닉네임 생성"
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "닉네임 생성 성공"),
		@ApiResponse(responseCode = "400", description = "중복된 닉네임")
	})
	@PostMapping("/user/nickname")
	public ResponseEntity<String> nickname(@RequestBody UserDto dto) {
		int result = service.nickname(dto);
		if (result == 0) {
			return new ResponseEntity<>("중복된 닉네임", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("닉네임 생성 성공", HttpStatus.OK);
	}

	// 회원가입 시 아이디 중복 체크
	@Operation(
		summary = "아이디 중복 체크",
		description = "중복된 아이디가 있으면 0, 없으면 1 반환"
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "닉네임 생성 성공"),
		@ApiResponse(responseCode = "400", description = "중복된 닉네임")
	})
	@PostMapping("/user/idcheck")
	public int idCheck(@RequestBody UserDto dto) {
		return service.idCheck(dto);
	}

	// 마이페이지
	@Operation(
		summary = "회원 정보 조회",
		description = "회원 정보와 전적 제공"
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
		@ApiResponse(responseCode = "500", description = "비정상적인 접근")
	})
	@GetMapping("/mypage/{userSeq}")
	public ResponseEntity<MypageDto> mypage(@PathVariable(value = "userSeq") int userSeq) {
		try {
			return new ResponseEntity<>(service.mypage(userSeq), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/user/login/kakao")
	// 인가 코드로 Access 토큰 발급 받기
	public ResponseEntity<Object> kakaoLogin(@RequestBody String code, HttpSession session) {
		// System.out.println("code = " + code);
		RestTemplate restTemplate = new RestTemplate();
		String tokenRequestUri = "https://kauth.kakao.com/oauth/token";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(tokenRequestUri, request, String.class);
			// System.out.println(response);
			ObjectMapper objectMapper = new ObjectMapper();
			String responseBody = response.getBody();
			JsonNode rootNode = objectMapper.readTree(responseBody);
			JsonNode accessTokenNode = rootNode.path("access_token");
			String accessToken = accessTokenNode.asText();
			// System.out.println("Access Token: " + accessToken);

			// 카카오에 사용자 정보 요청
			RestTemplate template = new RestTemplate();
			String requestUri = "https://kapi.kakao.com/v2/user/me";

			HttpHeaders header = new HttpHeaders();
			header.add("Authorization", "Bearer " + accessToken);
			header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<String> rq = new HttpEntity<>(header);

			ResponseEntity<Map> rr = template.exchange(requestUri, HttpMethod.GET, rq, Map.class);
			Map<String, Object> rpbody = rr.getBody();
			// System.out.println("rpbody = " + rpbody);

			Long kakaoId = (Long)rpbody.get("id");
			// System.out.println("ID = " + kakaoId);

			UserDto kakaoDto = new UserDto();
			kakaoDto.setUserId("K" + kakaoId);
			int result = service.insertUser(kakaoDto);
			// System.out.println("kakaoResult = " + result);
			if (result == 0) {
				// 이미 회원가입 된 회원, 카카오 로그인 진행
				// userId 가지고 DB 조회해서 정보값 가져오기
				Object kakaoUserInfo = service.findById("K" + kakaoId);
				session.setAttribute("user", kakaoUserInfo);
				System.out.println("카카오톡 부분 세션 = " + session);
				System.out.println("session.getAttribute(user) = " + session.getAttribute("user"));
				// System.out.println("kakaoUserInfo = " + kakaoUserInfo);
				return new ResponseEntity<>(kakaoUserInfo, HttpStatus.OK);
			}
			// 처음 접속한 회원이라면 회원가입
			Object kUserInfo = service.findById("K" + kakaoId);
			session.setAttribute("user", kUserInfo);
			System.out.println("카카오톡 부분 세션 = " + session);
			System.out.println("session.getAttribute(user) = " + session.getAttribute("user"));
			return new ResponseEntity<>(kUserInfo, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("카카오 로그인 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}