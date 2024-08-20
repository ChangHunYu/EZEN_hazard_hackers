"use client";
import React, { useState, useEffect } from "react";
import { ChecklistDto, ItemDto } from "@/components/dtoApi";
import Modal from "@/components/shared/modal";

export interface TodoItem {
  id: number;
  isChecked: boolean;
  description: string;
}

interface CheckListCardProps {
  checklistId: number;
  userId: number;
  itineraryId: number;
  title: string;
  description?: string;
  showModal: boolean;
  setShowModal: React.Dispatch<React.SetStateAction<boolean>>;
  onSave?: (todos: TodoItem[]) => void;
  initialTodos?: TodoItem[];
  onClose?: () => void; // 추가된 부분
}

const CheckListCard: React.FC<CheckListCardProps> = ({
  checklistId,
  userId,
  itineraryId,
  title,
  description,
  showModal,
  setShowModal,
  onSave,
  initialTodos = [],
  onClose, // 추가된 부분
}) => {
  const [todos, setTodos] = useState<TodoItem[]>(initialTodos);
  const [input, setInput] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editText, setEditText] = useState("");

  useEffect(() => {
    setTodos(initialTodos);
  }, [initialTodos]);

  const addTodo = () => {
    if (input.trim() !== "") {
      setTodos([
        ...todos,
        { id: Date.now(), description: input.trim(), isChecked: false },
      ]);
      setInput("");
    }
  };

  const toggleTodo = (id: number) => {
    setTodos(
      todos.map((todo) =>
        todo.id === id ? { ...todo, isChecked: !todo.isChecked } : todo,
      ),
    );
  };

  const startEditing = (id: number, description: string) => {
    setEditingId(id);
    setEditText(description);
  };

  const saveEdit = (id: number) => {
    setTodos(
      todos.map((todo) =>
        todo.id === id ? { ...todo, description: editText.trim() } : todo,
      ),
    );
    setEditingId(null);
    setEditText("");
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditText("");
  };

  const deleteTodo = (id: number) => {
    setTodos(todos.filter((todo) => todo.id !== id));
  };

  const handleSave = async () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      console.error("로그인 정보가 없습니다.");
      return;
    }

    const updatedItems: ItemDto[] = todos.map((todo) => ({
      id: todo.id,
      isChecked: todo.isChecked,
      isDeleted: false,
      checklistId: checklistId,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      description: todo.description,
    }));

    const checklistData: ChecklistDto = {
      id: checklistId,
      userId: userId,
      itineraryId: itineraryId,
      title: title,
      items: updatedItems,
      deleted: false,
    };

    try {
      const response = await fetch(
        `http://localhost:8080/api/checklists/${checklistId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(checklistData),
        },
      );

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(`서버 응답 실패 (${response.status}): ${errorMessage}`);
      }

      const result = (await response.json()) as ChecklistDto;

      setTodos(
        result.items.map((item) => ({
          id: item.id,
          isChecked: item.isChecked,
          description: item.description,
        })),
      );

      alert("체크리스트가 저장되었습니다.");
      if (onSave) {
        onSave(
          result.items.map((item) => ({
            id: item.id,
            isChecked: item.isChecked,
            description: item.description,
          })),
        );
      }
    } catch (error) {
      console.error("체크리스트 저장 실패:", error);
      alert("체크리스트 저장 실패. 다시 시도해 주세요.");
    }
  };

  return (
    <Modal showModal={showModal} setShowModal={setShowModal}>
      <div className="flex h-full flex-col overflow-hidden">
        <div className="flex w-full items-center justify-between border-b border-gray-200 bg-gray-100 p-4">
          <h2 className="font-display text-lg font-bold text-gray-800 [text-wrap:balance] md:text-xl">
            {title}
          </h2>
          <div>
            {onSave && (
              <button
                onClick={handleSave}
                className="mr-2 rounded bg-green-500 px-4 py-2 text-white transition hover:bg-green-600"
              >
                저장
              </button>
            )}
            <button
              onClick={() => {
                setShowModal(false);
                if (onClose) onClose(); // 추가된 부분
              }}
              className="rounded bg-red-500 px-4 py-2 text-white transition hover:bg-red-600"
            >
              닫기
            </button>
          </div>
        </div>
        <div className="flex-grow overflow-auto p-4">
          <div className="mb-4 flex">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              className="flex-grow rounded-l border border-gray-300 p-2"
              placeholder="목록 추가..."
            />
            <button
              onClick={addTodo}
              className="rounded-r bg-blue-500 px-4 py-2 text-white transition hover:bg-blue-600"
            >
              추가
            </button>
          </div>
          <ul>
            {todos.map((todo) => (
              <li key={todo.id} className="mb-2 flex items-center">
                <input
                  type="checkbox"
                  checked={todo.isChecked}
                  onChange={() => toggleTodo(todo.id)}
                  className="mr-2"
                />
                {editingId === todo.id ? (
                  <>
                    <input
                      type="text"
                      value={editText}
                      onChange={(e) => setEditText(e.target.value)}
                      className="flex-grow rounded border border-gray-300 p-1"
                      autoFocus
                    />
                    <button
                      onClick={() => saveEdit(todo.id)}
                      className="ml-2 rounded bg-green-500 px-2 py-1 text-white transition hover:bg-green-600"
                    >
                      저장
                    </button>
                    <button
                      onClick={cancelEdit}
                      className="ml-2 rounded bg-yellow-500 px-2 py-1 text-white transition hover:bg-yellow-600"
                    >
                      취소
                    </button>
                  </>
                ) : (
                  <>
                    <span
                      className={`flex-grow ${
                        todo.isChecked ? "text-gray-500 line-through" : ""
                      }`}
                    >
                      {todo.description}
                    </span>
                    <button
                      onClick={() => startEditing(todo.id, todo.description)}
                      className="ml-2 rounded bg-yellow-500 px-2 py-1 text-white transition hover:bg-yellow-600"
                    >
                      수정
                    </button>
                    <button
                      onClick={() => deleteTodo(todo.id)}
                      className="ml-2 rounded bg-red-500 px-2 py-1 text-white transition hover:bg-red-600"
                    >
                      삭제
                    </button>
                  </>
                )}
              </li>
            ))}
          </ul>
        </div>
        {description && (
          <div className="border-t border-gray-200 bg-gray-100 p-4">
            <p className="font-medium text-gray-800 transition-colors">
              {description}
            </p>
          </div>
        )}
      </div>
    </Modal>
  );
};

export default CheckListCard;
