import UseIsLobbyStore from "../../store/UseIsLobbyStore";
import QuestionMarkIcon from '@mui/icons-material/QuestionMark';

const ManualTab = () => {
    const { setIsLobby } = UseIsLobbyStore();
  
    return (
      <button
        className="py-2 px-5 text-xl text-center rounded-t-lg bg-tab3 transform origin-bottom transition duration-200 hover:scale-y-125"
        onClick={() => {
          setIsLobby("Manual");
        }}
      >
        <QuestionMarkIcon color="disabled"/>
      </button>
    );
  };
  export default ManualTab;