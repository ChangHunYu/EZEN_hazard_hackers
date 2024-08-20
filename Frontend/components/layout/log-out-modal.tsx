"use client";

import { Dispatch, SetStateAction, FC } from "react";
import Modal from "@/components/shared/modal";

interface LogoutModalProps {
  showModal: boolean;
  setShowModal: Dispatch<SetStateAction<boolean>>;
  onLogout: () => void;
}

const LogoutModal: FC<LogoutModalProps> = ({
  showModal,
  setShowModal,
  onLogout,
}) => {
  // 로그아웃 후 페이지를 새로고침하는 함수
  const handleLogout = () => {
    onLogout();
    window.location.reload();
  };

  return (
    <Modal showModal={showModal} setShowModal={setShowModal}>
      <div className="p-6">
        <h2 className="text-2xl font-bold">로그아웃</h2>
        <p className="mt-4">정말로 로그아웃하시겠습니까?</p>
        <div className="mt-4 flex space-x-2">
          <button
            onClick={handleLogout}
            className="rounded bg-green-500 px-4 py-2 text-white hover:bg-green-600"
          >
            로그아웃
          </button>
          <button
            onClick={() => setShowModal(false)}
            className="rounded bg-gray-500 px-4 py-2 text-white hover:bg-gray-600"
          >
            취소
          </button>
        </div>
      </div>
    </Modal>
  );
};

export default LogoutModal;
