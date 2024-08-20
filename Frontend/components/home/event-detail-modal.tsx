"use client";

import React, { useState } from "react";
import Modal from "@/components/shared/modal";
import { CalendarEvent } from "@/components/shared/monthlyCalendar";
import moment from "moment";

interface EventDetailModalProps {
  showModal: boolean;
  setShowModal: React.Dispatch<React.SetStateAction<boolean>>;
  event: CalendarEvent | null;
  onUpdate: (event: CalendarEvent) => void;
  onDelete: (id: number) => void;
}

const EventDetailModal: React.FC<EventDetailModalProps> = ({
  showModal,
  setShowModal,
  event,
  onUpdate,
  onDelete,
}) => {
  const [startDate, setStartDate] = useState<string>(
    moment(event?.startDate).format("YYYY-MM-DD"),
  );
  const [endDate, setEndDate] = useState<string>(
    moment(event?.endDate).format("YYYY-MM-DD"),
  );
  const [description, setDescription] = useState<string>(
    event?.description || "",
  );

  if (!event) return null;

  const handleUpdate = () => {
    if (event) {
      onUpdate({
        ...event,
        startDate: new Date(startDate),
        endDate: new Date(endDate),
        description,
      });
      setShowModal(false);
    }
  };

  const handleDelete = () => {
    if (event) {
      onDelete(event.id as number);
      setShowModal(false);
    }
  };

  return (
    <Modal showModal={showModal} setShowModal={setShowModal} className="p-4">
      <div className="p-4">
        <h2 className="mb-4 text-xl font-semibold">일정 상세</h2>
        <div className="mb-4">
          <label className="block text-gray-700">제목</label>
          <input
            type="text"
            value={event.title}
            readOnly
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm"
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700">시작 날짜</label>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm"
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700">종료 날짜</label>
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm"
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700">설명</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm"
          />
        </div>
        <div className="mt-4 flex justify-end space-x-2">
          <button
            onClick={() => setShowModal(false)}
            className="rounded bg-gray-500 px-4 py-2 text-white hover:bg-gray-600"
          >
            닫기
          </button>
          <button
            onClick={handleUpdate}
            className="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
          >
            수정
          </button>
          <button
            onClick={handleDelete}
            className="rounded bg-red-500 px-4 py-2 text-white hover:bg-red-600"
          >
            삭제
          </button>
        </div>
      </div>
    </Modal>
  );
};

export default EventDetailModal;
