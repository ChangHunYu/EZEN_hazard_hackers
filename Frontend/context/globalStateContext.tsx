"use client";
// context/GlobalStateContext.tsx
import React, { createContext, useContext, useState } from "react";

interface GlobalState {
  refreshCheckList: boolean;
  setRefreshCheckList: React.Dispatch<React.SetStateAction<boolean>>;
}

const GlobalStateContext = createContext<GlobalState | undefined>(undefined);

export const GlobalStateProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [refreshCheckList, setRefreshCheckList] = useState<boolean>(false);

  return (
    <GlobalStateContext.Provider
      value={{ refreshCheckList, setRefreshCheckList }}
    >
      {children}
    </GlobalStateContext.Provider>
  );
};

export const useGlobalState = () => {
  const context = useContext(GlobalStateContext);
  if (!context) {
    throw new Error("useGlobalState must be used within a GlobalStateProvider");
  }
  return context;
};
