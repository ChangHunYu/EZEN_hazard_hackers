"use client";

import Image from "next/image";
import Link from "next/link";
import useScroll from "@/lib/hooks/use-scroll";
import { useState, useEffect } from "react";
import { Session } from "next-auth";
import LoginModal from "@/components/layout/log-in-modal";
import SignUpModal from "@/components/layout/sign-up-modal";
import ProfileModal from "@/components/layout/profile-modal";
import LogoutModal from "@/components/layout/log-out-modal";

export default function NavBar({ session }: { session: Session | null }) {
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showSignUpModal, setShowSignUpModal] = useState(false);
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 초기 상태를 false로 설정
  const scrolled = useScroll(50);

  // 클라이언트 사이드에서만 로컬 스토리지 접근
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    setIsLoggedIn(!!token); // 로컬 스토리지에서 토큰이 있으면 로그인 상태로 설정
  }, []);

  useEffect(() => {
    // 세션이 변경될 때 로그인 상태를 업데이트
    const token = localStorage.getItem("accessToken");
    setIsLoggedIn(!!token);
  }, [session]);

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    setIsLoggedIn(false);
    setShowLogoutModal(false);
  };

  return (
    <>
      <LoginModal showModal={showLoginModal} setShowModal={setShowLoginModal} />
      <SignUpModal
        showModal={showSignUpModal}
        setShowModal={setShowSignUpModal}
      />
      <ProfileModal
        showModal={showProfileModal}
        setShowModal={setShowProfileModal}
      />
      <LogoutModal
        showModal={showLogoutModal}
        setShowModal={setShowLogoutModal}
        onLogout={handleLogout}
      />

      <div
        className={`fixed top-0 flex w-full justify-center ${
          scrolled
            ? "border-b border-gray-200 bg-white/50 backdrop-blur-xl"
            : "bg-white/0"
        } z-30 transition-all`}
      >
        <div className="mx-5 flex h-16 w-full max-w-screen-xl items-center justify-between">
          <Link href="/" className="flex items-center font-display text-2xl">
            <p>Hazard Hackers</p>
          </Link>
          <div>
            {isLoggedIn ? (
              <div className="space-x-2">
                <button
                  className="rounded-full border border-black bg-black p-1.5 px-4 text-sm text-white transition-all hover:bg-white hover:text-black"
                  onClick={() => setShowProfileModal(true)}
                >
                  프로필
                </button>
                <button
                  className="rounded-full border border-black bg-white p-1.5 px-4 text-sm text-black transition-all hover:bg-black hover:text-white"
                  onClick={() => setShowLogoutModal(true)}
                >
                  로그아웃
                </button>
              </div>
            ) : (
              <div className="space-x-2">
                <button
                  className="rounded-full border border-black bg-black p-1.5 px-4 text-sm text-white transition-all hover:bg-white hover:text-black"
                  onClick={() => setShowLoginModal(true)}
                >
                  로그인
                </button>
                <button
                  className="rounded-full border border-black bg-white p-1.5 px-4 text-sm text-black transition-all hover:bg-black hover:text-white"
                  onClick={() => setShowSignUpModal(true)}
                >
                  회원가입
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
