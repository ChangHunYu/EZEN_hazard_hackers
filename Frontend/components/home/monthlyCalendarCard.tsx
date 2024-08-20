import dynamic from "next/dynamic";

const DynamicMonthlyCalendar = dynamic(
  () => import("@/components/shared/monthlyCalendar"),
  {
    ssr: false,
  },
);

interface MonthlyCalendarCardProps {
  title?: string;
  description?: string;
}

const MonthlyCalendarCard: React.FC<MonthlyCalendarCardProps> = ({
  title,
  description,
}) => {
  return (
    <div className="relative col-span-1 flex flex-col overflow-hidden rounded-xl border border-gray-200 bg-white shadow-md md:col-span-2">
      {/* 타이틀 */}
      {title && (
        <div className="w-full border-b border-gray-200 bg-gray-100 p-4 text-center">
          <h2 className="font-display text-xl font-bold text-gray-800 [text-wrap:balance] md:text-2xl">
            {title}
          </h2>
        </div>
      )}

      {/* 달력 */}
      <div className="h-[500px] flex-1 overflow-hidden">
        {" "}
        {/* 높이 조정 */}
        <DynamicMonthlyCalendar />
      </div>

      {/* 설명 */}
      {description && (
        <div className="border-t border-gray-200 bg-gray-100 p-4">
          <p className="font-medium text-gray-800 transition-colors">
            {description}
          </p>
        </div>
      )}
    </div>
  );
};

export default MonthlyCalendarCard;
