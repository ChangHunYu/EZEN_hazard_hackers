import React from "react";
import WorldMap from "@/components/shared/map";

interface WorldMapCardProps {
  title: string;
  description?: string;
}

const WorldMapCard: React.FC<WorldMapCardProps> = ({ title, description }) => {
  return (
    <div className="relative col-span-1 flex h-[800px] flex-col overflow-hidden rounded-xl border border-gray-200 bg-white shadow-md md:col-span-2">
      {/* 타이틀 */}
      <div className="w-full border-b border-gray-200 bg-gray-100 p-4 text-center">
        <h2 className="font-display text-xl font-bold text-gray-800 [text-wrap:balance] md:text-2xl">
          {title}
        </h2>
      </div>

      {/* 지도 */}
      <div className="flex-grow bg-[#a8e1f7]">
        <WorldMap />
      </div>

      {/* 설명 */}
      {
        <div className="border-t border-gray-200 bg-gray-100 p-4">
          <ul className="flex flex-row space-x-6">
            <li className="relative flex h-24 w-24 flex-col items-center justify-center rounded-full border-4 border-blue-200 bg-blue-500 text-white">
              <span className="absolute top-2 text-lg font-semibold">
                1단계
              </span>
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="h-px w-full bg-white"></div>
              </div>
              <span className="absolute bottom-2 text-base">여행유의</span>
            </li>
            <li className="relative flex h-24 w-24 flex-col items-center justify-center rounded-full border-4 border-yellow-200 bg-yellow-500 text-white">
              <span className="absolute top-2 text-lg font-semibold">
                2단계
              </span>
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="h-px w-full bg-white"></div>
              </div>
              <span className="absolute bottom-2 text-base">여행자제</span>
            </li>
            <li className="relative flex h-24 w-24 flex-col items-center justify-center rounded-full border-4 border-red-200 bg-red-500 text-white">
              <span className="absolute top-2 text-lg font-semibold">
                3단계
              </span>
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="h-px w-full bg-white"></div>
              </div>
              <span className="absolute bottom-2 text-base">출국권고</span>
            </li>
            <li className="relative flex h-24 w-24 flex-col items-center justify-center rounded-full border-4 border-gray-200 bg-black text-white">
              <span className="absolute top-2 text-lg font-semibold">
                4단계
              </span>
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="h-px w-full bg-white"></div>
              </div>
              <span className="absolute bottom-2 text-base">여행금지</span>
            </li>
          </ul>
        </div>
      }
    </div>
  );
};

export default WorldMapCard;
