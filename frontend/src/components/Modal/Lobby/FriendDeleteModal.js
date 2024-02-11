import React, { useEffect ,useCallback} from "react";
import { useNavigate } from "react-router-dom";
import axios from "../../../api/axios.js";
import useFriends from "../../../hooks/useFriends";
import UseFriendsStore from "../../../store/UseFriendsStore";
import useLobbyApiCall from "../../../api/useLobbyApiCall";

const FriendDeleteModal = (props) => {
    const { friendRefs } = useFriends();
    const { friends,setFriends } = UseFriendsStore();
    const { rejectFriends,listFriends } = useLobbyApiCall();

    // 친구 목록 갱신을 위한 함수 정의
    const updateFriendsList = useCallback(async () => {
        const friendsList = await listFriends();
        setFriends(friendsList);
    }, [listFriends, setFriends]);
    
    // 삭제 버튼 클릭 시 친구 삭제
    const handleDeleteClick = async () => {
        try {
            await rejectFriends(props.selectedFriendId);
            console.log("친구아이디",props.selectedFriendId)
            updateFriendsList(); // 친구 목록 업데이트
            props.setModal(false); // 모달 닫기
        } catch (error) {
            console.error("친구 삭제 실패:", error);
        }
    };

    return (
        <>
        <div
            className="fixed inset-0 bg-black bg-opacity-10 flex justify-center items-center z-50"
            onClick={() => {
            props.setModal(false);
            }}
        >
            <div className="bg-white rounded-3xl border-gray-200 border shadow-lg p-5 md:p-6 mx-2"
            onClick={(e) => e.stopPropagation()}>
            <h2 className="text-lg font-medium text-gray-900 mb-4">{props.selectedFriend}님을 친구 삭제하시겠습니까?</h2>
            <div className="flex justify-center items-center space-x-4">
                <button onClick={handleDeleteClick} className="bg-tab10 hover:bg-[#95c75a] py-2 px-4 rounded-xl">
                삭제
                </button>
                <button onClick={props.setModal} className="bg-gray-200 hover:bg-gray-300 text-gray-800 py-2 px-4 rounded-xl">
                취소
                </button>
            </div>
            </div>
        </div>
        </>
    );
};

export default FriendDeleteModal;

