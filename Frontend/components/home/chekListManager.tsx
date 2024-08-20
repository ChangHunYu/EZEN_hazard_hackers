"use client";
import React, { useState, useEffect } from "react";
import CheckListCard from "@/components/home/checkListCard";
import { useGlobalState } from "@/context/globalStateContext";
import { ChecklistDto, ItemDto } from "@/components/dtoApi";
import { TodoItem } from "@/components/home/checkListCard";

interface CheckListManagerProps {
  title: string;
  description?: string;
}

const CheckListManager: React.FC<CheckListManagerProps> = ({
  title,
  description,
}) => {
  const { refreshCheckList } = useGlobalState();
  const [checklists, setChecklists] = useState<ChecklistDto[]>([]);
  const [newListTitle, setNewListTitle] = useState("");
  const [selectedList, setSelectedList] = useState<ChecklistDto | null>(null);
  const [error, setError] = useState<string | null>(null);

  // 로그인 여부 확인 함수
  const isLoggedIn = () => {
    return !!localStorage.getItem("accessToken");
  };

  useEffect(() => {
    const fetchChecklists = async () => {
      if (!isLoggedIn()) {
        // 로그인되지 않은 경우 에러 설정하지 않음
        setError(null);
        return;
      }

      try {
        const response = await fetch("http://localhost:8080/api/checklists", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          },
        });
        if (!response.ok) {
          throw new Error("Failed to fetch checklists");
        }
        const data: ChecklistDto[] = await response.json();
        setChecklists(data);
      } catch (error) {
        if (isLoggedIn()) {
          // 로그인된 상태에서만 에러 메시지 설정
          setError("체크리스트를 가져오는 데 실패했습니다.");
        }
        console.error(error);
      }
    };

    fetchChecklists();
  }, [refreshCheckList]);

  const saveChecklist = async (updatedItems: ItemDto[]) => {
    if (selectedList) {
      const updatedList = { ...selectedList, items: updatedItems };

      try {
        const response = await fetch(
          `http://localhost:8080/api/checklists/${selectedList.id}`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            },
            body: JSON.stringify(updatedList),
          },
        );

        if (!response.ok) {
          throw new Error("Failed to update checklist");
        }

        const updatedChecklist: ChecklistDto = await response.json();
        setChecklists((prev) =>
          prev.map((list) =>
            list.id === updatedChecklist.id ? updatedChecklist : list,
          ),
        );
        setSelectedList(null);
      } catch (error) {
        if (isLoggedIn()) {
          // 로그인된 상태에서만 에러 메시지 설정
          setError("체크리스트를 저장하는 데 실패했습니다.");
        }
        console.error(error);
      }
    }
  };

  const addChecklist = async () => {
    if (newListTitle.trim() !== "") {
      try {
        const response = await fetch("http://localhost:8080/api/checklists", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          },
          body: JSON.stringify({
            title: newListTitle.trim(),
            items: [],
            deleted: false,
          }),
        });

        if (!response.ok) {
          throw new Error("Failed to add checklist");
        }

        const newChecklist: ChecklistDto = await response.json();
        setChecklists((prev) => [...prev, newChecklist]);
        setNewListTitle("");
      } catch (error) {
        if (isLoggedIn()) {
          // 로그인된 상태에서만 에러 메시지 설정
          setError("체크리스트를 추가하는 데 실패했습니다.");
        }
        console.error(error);
      }
    }
  };

  return (
    <div className="relative col-span-1 flex h-full flex-col overflow-hidden rounded-xl border border-gray-200 bg-white shadow-md">
      <div className="w-full border-b border-gray-200 bg-gray-100 p-4 text-center">
        <h2 className="font-display text-xl font-bold text-gray-800 [text-wrap:balance] md:text-2xl">
          {title}
        </h2>
      </div>
      <div className="flex-grow overflow-auto p-4">
        {error && (
          <div className="mb-4 rounded border border-red-300 bg-red-100 p-2 text-red-600">
            {error}
          </div>
        )}
        {/* <div className="mb-4 flex">
          <input
            type="text"
            value={newListTitle}
            onChange={(e) => setNewListTitle(e.target.value)}
            className="flex-grow rounded-l-lg border border-gray-300 px-4 py-2 placeholder-gray-500 shadow-sm"
            placeholder="새 체크리스트 추가..."
          />
          <button
            onClick={addChecklist}
            className="rounded-r-lg bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
          >
            추가
          </button>
        </div> */}
        <div className="grid grid-cols-1 gap-4 md:grid-cols-1">
          {checklists.map((list) => (
            <div
              key={list.id}
              className="cursor-pointer rounded-lg border border-gray-300 bg-gray-50 p-4 shadow-sm transition hover:bg-gray-100"
              onClick={() => setSelectedList(list)}
            >
              <h3 className="text-lg font-semibold text-gray-800">
                {list.title}
              </h3>
            </div>
          ))}
        </div>
      </div>
      {selectedList && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-60">
          <div className="w-full max-w-md rounded-lg bg-white shadow-lg">
            <CheckListCard
              checklistId={selectedList.id}
              userId={selectedList.userId}
              itineraryId={selectedList.itineraryId}
              title={selectedList.title}
              showModal={!!selectedList}
              setShowModal={(show) => {
                if (!show) setSelectedList(null);
              }}
              onSave={async (updatedTodos: TodoItem[]) => {
                const updatedItems: ItemDto[] = updatedTodos.map((todo) => ({
                  id: todo.id,
                  isChecked: todo.isChecked,
                  isDeleted: false,
                  checklistId: selectedList.id,
                  createdAt: new Date().toISOString(),
                  updatedAt: new Date().toISOString(),
                  description: todo.description,
                }));
                await saveChecklist(updatedItems);
              }}
              initialTodos={selectedList.items.map((item) => ({
                id: item.id,
                isChecked: item.isChecked,
                description: item.description,
              }))}
              onClose={() => setSelectedList(null)} // 추가된 부분
            />
          </div>
        </div>
      )}
      {description && (
        <div className="border-t border-gray-200 bg-gray-100 p-4">
          <p className="text-gray-700">{description}</p>
        </div>
      )}
    </div>
  );
};

export default CheckListManager;
