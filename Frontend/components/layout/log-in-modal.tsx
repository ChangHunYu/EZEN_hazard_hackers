"use client";

import { Dispatch, SetStateAction, FC, useState } from "react";
import Modal from "@/components/shared/modal";

interface LoginModalProps {
  showModal: boolean;
  setShowModal: Dispatch<SetStateAction<boolean>>;
}

const LoginModal: FC<LoginModalProps> = ({ showModal, setShowModal }) => {
  const [userEmail, setUserEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      const response = await fetch("http://localhost:8080/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ userEmail, password }),
      });

      if (response.ok) {
        const data = await response.json();
        if (data.accessToken) {
          // JWT 토큰을 로컬 스토리지에 저장
          localStorage.setItem("accessToken", data.accessToken);
          setShowModal(false); // 로그인 성공 후 모달 닫기
          window.location.reload(); // 로그인 성공 후 페이지 새로 고침 (상태 업데이트)
        } else {
          setError("액세스 토큰이 없습니다.");
        }
      } else {
        const errorData = await response.json();
        setError(errorData.message || "로그인에 실패했습니다.");
      }
    } catch (error) {
      console.error("로그인 에러:", error);
      setError("서버와의 통신 중 오류가 발생했습니다.");
    }
  };

  return (
    <Modal showModal={showModal} setShowModal={setShowModal}>
      <div className="p-6">
        <h2 className="mb-4 text-2xl font-bold">로그인</h2>
        {error && <p className="mb-4 text-red-500">{error}</p>}
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="email" className="mb-2 block">
              이메일
            </label>
            <input
              type="email"
              id="email"
              value={userEmail}
              onChange={(e) => setUserEmail(e.target.value)}
              className="w-full rounded border px-3 py-2"
              required
            />
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="mb-2 block">
              비밀번호
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded border px-3 py-2"
              required
            />
          </div>
          <button
            type="submit"
            className="w-full rounded bg-green-500 px-4 py-2 text-white hover:bg-green-600"
          >
            로그인
          </button>
        </form>
      </div>
    </Modal>
  );
};

export default LoginModal;
