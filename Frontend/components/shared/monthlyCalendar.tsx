"use client";

import React, { useState, useEffect, useCallback } from "react";
import { Calendar, momentLocalizer, Event } from "react-big-calendar";
import moment from "moment";
import "react-big-calendar/lib/css/react-big-calendar.css";
import "moment/locale/ko"; // 한글 로케일 임포트
import Modal from "@/components/shared/modal";
import Select from "react-select";
import EventDetailModal from "@/components/home/event-detail-modal";
import { useGlobalState } from "@/context/globalStateContext";

const localizer = momentLocalizer(moment);

export interface CalendarEvent extends Event {
  id?: number;
  title: string;
  startDate: Date;
  endDate: Date;
  allDay?: boolean;
  color: string;
  description?: string;
  countryId?: number;
  userCountryId?: number;
  userCountryEngName?: string;
}

const colors = [
  "#3B82F6", // Blue
  "#EF4444", // Red
  "#10B981", // Green
  "#F59E0B", // Yellow
  "#8B5CF6", // Purple
  "#EC4899", // Pink
  "#14B8A6", // Teal
  "#6366F1", // Indigo
];

const getRandomColor = () => {
  const randomIndex = Math.floor(Math.random() * colors.length);
  return colors[randomIndex];
};

const getColorById = (id: number): string => {
  return colors[id % colors.length];
};

const MonthlyCalendar: React.FC = () => {
  const [refreshKey, setRefreshKey] = useState<number>(0);
  const [events, setEvents] = useState<CalendarEvent[]>([]);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [formStart, setFormStart] = useState<string>("");
  const [formEnd, setFormEnd] = useState<string>("");
  const [formDescription, setFormDescription] = useState<string>("");
  const [selectedEvent, setSelectedEvent] = useState<CalendarEvent | null>(
    null,
  );
  const [showDetailModal, setShowDetailModal] = useState<boolean>(false);
  const [selectedCountry, setSelectedCountry] = useState<{
    value: number;
    label: string;
    countryEngName: string;
    countryName: string;
  } | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [countryOptions, setCountryOptions] = useState<
    {
      value: number;
      label: string;
      countryEngName: string;
    }[]
  >([]);
  const { setRefreshCheckList } = useGlobalState();

  // Fetch countries from server
  const fetchCountries = useCallback(async () => {
    try {
      const response = await fetch("http://localhost:8080/country");
      if (!response.ok) {
        throw new Error("Failed to fetch countries");
      }
      const data = await response.json();
      const options = data.map((country: any) => ({
        value: country.id,
        label: country.countryName,
        countryEngName: country.countryEngName,
        countryName: country.countryName,
      }));
      setCountryOptions(options);
    } catch (error) {
      console.error("Error fetching countries:", error);
      setError("국가 정보를 가져오는 데 실패했습니다.");
    }
  }, []);

  // Fetch events from server
  const fetchEvents = useCallback(async () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      console.error("로그인 정보가 없습니다.");
      return;
    }
    try {
      const response = await fetch("http://localhost:8080/itinerary", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("인증에 실패했습니다. 다시 로그인해 주세요.");
        }
        const errorMessage = await response.text();
        throw new Error(`서버 응답 실패 (${response.status}): ${errorMessage}`);
      }
      const data = await response.json();
      const formattedEvents: CalendarEvent[] = data.map((event: any) => ({
        id: event.id,
        title: event.title || event.userCountryEngName,
        startDate: new Date(event.startDate),
        endDate: new Date(event.endDate),
        color: getColorById(event.id),
        description: event.description,
        countryId: event.countryId,
        userCountryId: event.userCountryId,
        userCountryEngName: event.userCountryEngName,
      }));
      setEvents(formattedEvents);
    } catch (error) {
      console.error("일정 조회 실패:", error);
      setError("일정 조회 실패. 다시 시도해 주세요.");
    }
  }, []);

  useEffect(() => {
    fetchCountries();
    fetchEvents();
  }, [fetchCountries, fetchEvents]);

  const fetchUserCountryId = async (
    countryEngName: string,
  ): Promise<number | null> => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      throw new Error("로그인 정보가 없습니다. 다시 로그인해 주세요.");
    }
    try {
      const response = await fetch(
        `http://localhost:8080/UserCountries?userCountryEngName=${countryEngName}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("인증에 실패했습니다. 다시 로그인해 주세요.");
        }
        const errorMessage = await response.text();
        throw new Error(`서버 응답 실패 (${response.status}): ${errorMessage}`);
      }
      const data = await response.json();
      if (data.length > 0) {
        return data[0].id;
      } else {
        throw new Error("사용자 국가 정보를 찾을 수 없습니다.");
      }
    } catch (error) {
      console.error("사용자 국가 ID 가져오기 실패:", error);
      throw new Error("사용자 국가 ID를 가져오는 데 실패했습니다.");
    }
  };

  const addEventToServer = async (
    event: CalendarEvent,
  ): Promise<CalendarEvent> => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      throw new Error("로그인 정보가 없습니다. 다시 로그인해 주세요.");
    }
    try {
      const response = await fetch(`http://localhost:8080/itinerary`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(event),
      });
      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("인증에 실패했습니다. 다시 로그인해 주세요.");
        }
        const errorMessage = await response.text();
        throw new Error(`서버 응답 실패 (${response.status}): ${errorMessage}`);
      }
      const responseText = await response.text();
      let data: CalendarEvent;
      try {
        data = JSON.parse(responseText);
      } catch {
        data = {
          title: responseText,
          startDate: new Date(),
          endDate: new Date(),
          color: getRandomColor(),
        } as CalendarEvent;
        if (data.id) {
          data.color = getColorById(data.id);
        }
      }
      return data;
    } catch (error) {
      console.error("일정 추가 중 오류 발생:", error);
      throw new Error("일정 추가 중 오류 발생. 다시 시도해 주세요.");
    }
  };

  const handleUpdateEvent = async (updatedEvent: CalendarEvent) => {
    if (
      !updatedEvent.title ||
      !updatedEvent.startDate ||
      !updatedEvent.endDate
    ) {
      console.error("제목, 시작 날짜, 종료 날짜는 필수입니다.");
      return;
    }

    try {
      setLoading(true);

      const userCountryId = await fetchUserCountryId(
        updatedEvent.userCountryEngName || "",
      );

      if (!userCountryId) {
        throw new Error("userCountryId를 가져오는 데 실패했습니다.");
      }

      const response = await fetch(
        `http://localhost:8080/itinerary/${updatedEvent.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          },
          body: JSON.stringify({
            countryId: updatedEvent.countryId,
            title: updatedEvent.title,
            startDate: moment(updatedEvent.startDate).format("YYYY-MM-DD"),
            endDate: moment(updatedEvent.endDate).format("YYYY-MM-DD"),
            description: updatedEvent.description,
            userCountryId: userCountryId,
          }),
        },
      );
      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("인증에 실패했습니다. 다시 로그인해 주세요.");
        }
        const errorMessage = await response.text();
        throw new Error(`서버 응답 실패 (${response.status}): ${errorMessage}`);
      }
      fetchEvents();
    } catch (error) {
      console.error("일정 수정 실패:", error);
      setError("일정 수정 실패. 다시 시도해 주세요.");
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteEvent = async (eventId: number) => {
    try {
      setLoading(true);
      const response = await fetch(
        `http://localhost:8080/itinerary/${eventId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          },
        },
      );
      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("인증에 실패했습니다. 다시 로그인해 주세요.");
        }
        const errorMessage = await response.text();
        throw new Error(`서버 응답 실패 (${response.status}): ${errorMessage}`);
      }
      setEvents((prevEvents) =>
        prevEvents.filter((event) => event.id !== eventId),
      );
      setShowDetailModal(false);
      setRefreshCheckList((prev) => !prev);
    } catch (error) {
      console.error("일정 삭제 실패:", error);
      setError("일정 삭제 실패. 다시 시도해 주세요.");
    } finally {
      setLoading(false);
    }
  };

  const handleAddEvent = async () => {
    if (!selectedCountry) {
      console.error("여행 국가를 선택해 주세요.");
      return;
    }
    if (!formStart || !formEnd) {
      console.error("시작 날짜와 종료 날짜를 입력해 주세요.");
      return;
    }
    if (new Date(formStart) >= new Date(formEnd)) {
      console.error("종료 날짜가 시작 날짜보다 빨라야 합니다.");
      return;
    }

    const newEvent: CalendarEvent = {
      title: selectedCountry.countryName,
      startDate: new Date(formStart),
      endDate: new Date(formEnd),
      color: getRandomColor(),
      description: formDescription,
      countryId: selectedCountry.value,
    };

    try {
      setLoading(true);
      await addEventToServer(newEvent);
      await fetchEvents();
      setRefreshKey((prevKey) => prevKey + 1);
      setShowModal(false);
      setFormStart("");
      setFormEnd("");
      setFormDescription("");
      setSelectedCountry(null);
      setRefreshCheckList((prev) => !prev);
    } catch (error) {
      console.error("일정 추가 실패:", error);
      setError("일정 추가 실패. 다시 시도해 주세요.");
    } finally {
      setLoading(false);
    }
  };

  const handleEventClick = (event: CalendarEvent) => {
    setSelectedEvent(event);
    setShowDetailModal(true);
  };

  return (
    <>
      <div className="p-4">
        <button
          onClick={() => setShowModal(true)}
          className="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
        >
          일정 추가
        </button>
      </div>
      <Calendar
        localizer={localizer}
        events={events}
        startAccessor="startDate"
        endAccessor="endDate"
        style={{ height: 600 }}
        onSelectEvent={handleEventClick}
        eventPropGetter={(event) => ({
          style: {
            backgroundColor: (event as CalendarEvent).color,
          },
        })}
        messages={{
          allDay: "종일",
          previous: "이전",
          next: "다음",
          today: "오늘",
          month: "월",
          week: "주",
          day: "일",
          agenda: "일정",
          date: "날짜",
          time: "시간",
          event: "이벤트",
          noEventsInRange: "해당 기간에 이벤트가 없습니다.",
        }}
      />
      <Modal showModal={showModal} setShowModal={setShowModal} className="p-4">
        <h2 className="mb-4 text-xl font-semibold">일정 추가</h2>
        <div className="mb-4">
          <label className="block text-gray-700">여행 국가</label>
          <Select
            options={countryOptions}
            value={selectedCountry}
            onChange={setSelectedCountry}
            placeholder="국가를 선택해 주세요."
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700">시작 날짜</label>
          <input
            type="date"
            value={formStart}
            onChange={(e) => setFormStart(e.target.value)}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm"
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700">종료 날짜</label>
          <input
            type="date"
            value={formEnd}
            onChange={(e) => setFormEnd(e.target.value)}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm"
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700">설명</label>
          <textarea
            value={formDescription}
            onChange={(e) => setFormDescription(e.target.value)}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm"
          />
        </div>
        <div className="flex justify-end space-x-2">
          <button
            onClick={() => setShowModal(false)}
            className="rounded bg-gray-500 px-4 py-2 text-white hover:bg-gray-600"
          >
            닫기
          </button>
          <button
            onClick={handleAddEvent}
            className="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
            disabled={loading}
          >
            {loading ? "로딩 중..." : "추가"}
          </button>
        </div>
      </Modal>
      {selectedEvent && (
        <EventDetailModal
          showModal={showDetailModal}
          setShowModal={setShowDetailModal}
          event={selectedEvent}
          onUpdate={handleUpdateEvent}
          onDelete={handleDeleteEvent}
        />
      )}
    </>
  );
};

export default MonthlyCalendar;
