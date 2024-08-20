import WorldMapCard from "@/components/home/worldmapcard";
import MonthlyCalendarCard from "@/components/home/monthlyCalendarCard";
import CheckListManager from "@/components/home/chekListManager";
import TravelAlertSidebar from "@/components/home/travelAlertSideBar";
import { GlobalStateProvider } from "@/context/globalStateContext";

export default async function Home() {
  return (
    <>
      <GlobalStateProvider>
        <div className="grid w-full max-w-screen-xl animate-fade-up grid-cols-1 gap-5 px-5 md:grid-cols-2 xl:px-0">
          <WorldMapCard title="세계 여행 경보 지도" description="" />
        </div>
        <div className="my-10 grid w-full max-w-screen-xl animate-fade-up grid-cols-1 gap-5 px-5 md:grid-cols-3 xl:px-0">
          <div className="md:col-span-2">
            <MonthlyCalendarCard title="달력" description="" />
          </div>
          <div className="md:col-span-1">
            <CheckListManager
              title="여행 체크리스트"
              description="여행 준비를 위한 체크리스트를 관리하세요"
            />
          </div>
          <TravelAlertSidebar />
        </div>
      </GlobalStateProvider>
    </>
  );
}
