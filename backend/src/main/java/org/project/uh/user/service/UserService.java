package org.project.uh.user.service;

import java.util.List;

import org.project.uh.user.dto.UserDto;

public interface UserService {
	// 회원가입
	public int insertUser(UserDto dto);

	// 회원 목록조회
	public List<UserDto> listUser();

	public int getUserId(UserDto dto);

	// 로그인
	public UserDto login(UserDto dto);


	// 닉네임 생성
	public int nickname(UserDto dto);
	public int getUserNickname(UserDto dto);

}