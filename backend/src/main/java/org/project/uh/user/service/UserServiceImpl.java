package org.project.uh.user.service;

import java.util.List;

import org.project.uh.user.dao.UserDao;
import org.project.uh.user.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

	private final UserDao dao;

	public UserServiceImpl(UserDao dao) {
		super();
		this.dao = dao;
	}

	// 회원 가입
	@Override
	public int insertUser(UserDto dto) {
		// 회원가입 시 userId 중복 체크
		if (dao.checkUserId(dto.getUserId()) > 0) {
			// 존재하는 userId, 회원가입 불가
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "중복된 아이디");
		}
		// 중복 없으면 회원가입
		return dao.insertUser(dto);
	}

	// 회원 목록 조회
	@Override
	public List<UserDto> listUser() {
		return dao.listUser();
	}


	@Override
	public UserDto login(UserDto dto) {
		UserDto user = dao.login(dto);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "로그인 오류");
		}
		return user;
	}

	@Override
	public int getUserId(UserDto dto) {
		return dao.getUserId(dto);
		// int n = dao.getUserId(dto);
		// return n;
	}

	// 닉네임 생성
	@Override
	public int nickname(UserDto dto) {
		// 닉네임 중복 체크
		int nicknameCount = dao.checkUserNickname(dto.getUserNickname());
		if (nicknameCount > 0) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "중복된 닉네임");
		}
		// 중복 없으면 닉네임 생성
		return dao.nickname(dto);
	}

	@Override
	public int getUserNickname(UserDto dto) {
		return dao.getUserNickname(dto);
	}

}