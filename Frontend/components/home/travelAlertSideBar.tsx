"use client";
import React, { useState, useEffect } from "react";
import { FaChevronRight, FaChevronLeft } from "react-icons/fa"; // 아이콘 임포트
import Modal from "@/components/shared/modal";

interface TravelAlert {
  id: number;
  countryName: string;
  level: number;
  message: string;
  description: string;
  regionType: string;
  remark: string;
  dang_map_download_url: string;
  written_dt: string;
}

const TravelAlertSidebar: React.FC = () => {
  const [alerts, setAlerts] = useState<TravelAlert[]>([]);
  const [isOpen, setIsOpen] = useState<boolean>(true);
  const [selectedAlert, setSelectedAlert] = useState<TravelAlert | null>(null);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const response = await fetch("http://localhost:8080/alerts");
        if (!response.ok) {
          throw new Error("데이터를 불러오는데 실패했습니다.");
        }

        const data: TravelAlert[] = await response.json();
        setAlerts(data);
      } catch (error) {
        console.error("Error fetching travel alerts:", error);
      }
    };

    fetchAlerts();
  }, []);

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };

  const openModal = (alert: TravelAlert) => {
    setSelectedAlert(alert);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedAlert(null);
  };

  const openImageInNewWindow = (imageUrl: string) => {
    const newWindow = window.open("", "_blank");
    if (newWindow) {
      newWindow.document.write(`
        <html>
          <head>
            <title>Image Preview</title>
            <style>
              body {
                margin: 0;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                background: #000;
              }
              img {
                max-width: 100%;
                max-height: 100vh;
                object-fit: contain;
              }
            </style>
          </head>
          <body>
            <img src="${imageUrl}" alt="Image Preview" />
          </body>
        </html>
      `);
      newWindow.document.close();
    }
  };

  return (
    <div>
      <button
        onClick={toggleSidebar}
        className="fixed right-4 top-4 z-50 flex items-center justify-center rounded bg-blue-500 p-2 text-white"
      >
        {isOpen ? <FaChevronLeft size={20} /> : <FaChevronRight size={20} />}
      </button>

      <div
        className={`z-45 fixed right-0 top-0 h-full w-64 overflow-y-auto bg-gray-100 p-4 shadow-lg transition-transform duration-300 ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        <h2 className="mb-4 text-xl font-bold">여행경보 알람</h2>
        <ul>
          {alerts.map((alert) => (
            <li
              key={alert.id}
              className="mb-4 cursor-pointer rounded-lg bg-white p-3 shadow"
              onClick={() => openModal(alert)}
            >
              <h3 className="font-semibold">{alert.countryName}</h3>
              <p className="mt-1 text-sm">지역 유형: {alert.regionType}</p>
              <p className={`mt-1 text-sm ${getAlertLevelColor(alert.level)}`}>
                경보 수준: {getAlertLevelToString(alert.level)}
              </p>
              {alert.message !== "no message" && (
                <p className="mt-1 text-sm">{alert.message}</p>
              )}
              <p className="mt-1 flex items-center text-xs text-gray-500">
                <span className="mr-2">🗓️</span>
                <span>{new Date(alert.written_dt).toLocaleDateString()}</span>
              </p>
            </li>
          ))}
        </ul>
      </div>

      {/* 모달 컴포넌트 */}
      {selectedAlert && (
        <Modal showModal={isModalOpen} setShowModal={setIsModalOpen}>
          <div className="p-6">
            <h2 className="mb-3 text-xl font-bold text-gray-800">
              {selectedAlert.countryName}
            </h2>
            <p
              className={`text-sm ${getAlertLevelColor(
                selectedAlert.level,
              )} mb-3`}
            >
              경보 수준: {getAlertLevelToString(selectedAlert.level)}
            </p>
            {selectedAlert.message !== "no message" && (
              <p className="mt-2 text-sm text-gray-700">
                {selectedAlert.message}
              </p>
            )}

            {/* 추가된 정보들 */}
            {selectedAlert.regionType && (
              <p className="mt-2 text-sm text-gray-600">
                <strong>지역 유형:</strong> {selectedAlert.regionType}
              </p>
            )}
            {selectedAlert.description !== "no description" && (
              <p className="mt-2 text-sm text-gray-600">
                <strong>설명:</strong> {decodeHtml(selectedAlert.description)}
              </p>
            )}
            {selectedAlert.remark && (
              <p className="mt-2 text-sm text-gray-600">
                <strong>비고:</strong> {selectedAlert.remark}
              </p>
            )}

            {/* 이미지 표시 */}
            {selectedAlert.dang_map_download_url && (
              <div className="mt-4">
                <img
                  src={selectedAlert.dang_map_download_url}
                  alt="위험 지도"
                  className="h-auto max-h-80 w-full rounded-lg object-cover shadow-md"
                  onClick={() =>
                    openImageInNewWindow(selectedAlert.dang_map_download_url)
                  }
                />
              </div>
            )}

            <p className="mt-4 text-xs text-gray-500">
              작성일: {new Date(selectedAlert.written_dt).toLocaleDateString()}
            </p>

            <button
              onClick={closeModal}
              className="mt-6 w-full rounded-lg bg-blue-500 py-2 text-white hover:bg-blue-600"
            >
              닫기
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
};

function getAlertLevelToString(level: number): string {
  switch (level) {
    case 1:
      return "여행유의(1단계)";
    case 2:
      return "여행자제(2단계)";
    case 3:
      return "출국권고(3단계)";
    case 4:
      return "여행금지(4단계)";
    default:
      return "정보없음";
  }
}

function getAlertLevelColor(level: number): string {
  const stringLevel = getAlertLevelToString(level);
  switch (stringLevel) {
    case "여행유의(1단계)":
      return "text-blue-600";
    case "여행자제(2단계)":
      return "text-yellow-600";
    case "출국권고(3단계)":
      return "text-purple-600";
    case "여행금지(4단계)":
      return "text-red-600";
    default:
      return "text-black-600";
  }
}
function decodeHtml(html: string): string {
  // 임시 textarea 요소 생성
  const txt = document.createElement("textarea");
  txt.innerHTML = html;
  let decoded = txt.value;

  // &nbsp;를 공백으로 변환
  decoded = decoded.replace(/&nbsp;/g, " ");

  // '□' 기호를 줄 바꿈으로 변환
  decoded = decoded.replace(/□/g, "\n□");

  // 추가적인 줄 바꿈을 유지하고, 필요에 따라 공백을 조정
  decoded = decoded.replace(/ {2,}/g, " "); // 여러 연속된 공백을 단일 공백으로 변환

  return decoded.trim(); // 문자열 양끝의 공백 제거
}
export default TravelAlertSidebar;
