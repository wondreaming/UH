package org.project.uh.user.service;

import java.util.List;

import org.project.uh.user.dto.MypageDto;
import org.project.uh.user.dto.UserDto;

public interface UserService {
	// 회원가입
	public int insertUser(UserDto dto);

	// 아이디 중복 체크
	public int idCheck(UserDto dto);

	// 회원 목록조회
	public List<UserDto> listUser();

	public int getUserId(UserDto dto);

	// 로그인
	public UserDto login(UserDto dto);

	// 닉네임 생성
	public int nickname(int userSeq, String userNickname);

	public int getUserNickname(String userNickname);

	// 마이페이지
	public MypageDto mypage(int userSeq);

	// 아이디로 유저 정보 조회
	public UserDto findById(String userId);
}
