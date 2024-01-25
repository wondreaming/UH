import axios from "axios";

axios.defaults.headers.post["Content-Type"] = "application/json";

const APPLICATION_SERVER_URL =
  process.env.NODE_ENV === "production" ? "" : "http://localhost:5000/";

/**
 * 플레이어의 팀 변경
 * @param {string} sessionId 방의 세션 아이디
 * @param {string} connectionId 플레이어의 커넥션 아이디
 * @param {string} team A | B, 서버의 기본 설정은 적은 팀의 값 할당
 */
export const playerTeam = async (sessionId, connectionId, team) => {
  console.log("플레이어 팀 변경", sessionId, connectionId, team);
  try {
    const response = await axios.put(
      APPLICATION_SERVER_URL + "team",
      {
        sessionId: sessionId,
        connectionId: connectionId,
        team: team,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    );
    console.log(response.data); // "팀이 변경 되었습니다."
  } catch (error) {
    console.log("플레이어 팀 변경 에러");
    console.error("Error:", error.message);
  }
};

export const passHost = async (sessionId, connectionId) => {
  console.log("반장 변경", sessionId, connectionId);
  try {
    const response = await axios.put(
      APPLICATION_SERVER_URL + "host",
      {
        sessionId: sessionId,
        connectionId: connectionId,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    );
    console.log(response.data); // "방장 권한을 전달했습니다."
  } catch (error) {
    console.log("반장 변경 에러");
    console.error("Error:", error.message);
  }
};

export const ready = async (sessionId, connectionId) => {
  console.log("준비", sessionId, connectionId);
  try {
    const response = await axios.put(
      APPLICATION_SERVER_URL + "ready",
      {
        sessionId: sessionId,
        connectionId: connectionId,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    );
    console.log(response.data); // "방장 권한을 전달했습니다."
  } catch (error) {
    console.log("준비");
    console.error("Error:", error.message);
  }
};