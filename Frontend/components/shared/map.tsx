"use client";
import React, { useEffect, useRef, useState } from "react";
import * as d3 from "d3";
import { feature } from "topojson-client";
import { FeatureCollection, Geometry } from "geojson";

// 여행경보 단계별 색상 정의
const alertColors = {
  1: "#3b82f6", // 여행유의 (라벤더 블루)
  2: "#facc15", // 여행자제 (파스텔 옐로우)
  3: "#ef4444", // 출국권고 (코랄)
  4: "#000000", // 여행금지 (연한 그레이)
};

// 바다 색상 정의
const oceanColor = "#a8e1f7"; // 연한 청색

const WorldMap: React.FC = () => {
  const svgRef = useRef<SVGSVGElement | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);
  const [dimensions, setDimensions] = useState({ width: 0, height: 0 });
  const [alertData, setAlertData] = useState<{ [key: string]: number }>({});

  useEffect(() => {
    const updateDimensions = () => {
      if (containerRef.current) {
        setDimensions({
          width: containerRef.current.clientWidth,
          height: containerRef.current.clientHeight,
        });
      }
    };

    updateDimensions();
    window.addEventListener("resize", updateDimensions);

    return () => window.removeEventListener("resize", updateDimensions);
  }, []);

  useEffect(() => {
    fetch("http://localhost:8080/country")
      .then((response) => response.json())
      .then((data) => {
        const dataMap = data.reduce(
          (acc: { [key: string]: number }, item: any) => {
            acc[item.countryEngName] = item.alertLevel;
            return acc;
          },
          {},
        );
        setAlertData(dataMap);
        // console.log("Alert Data:", dataMap); // 여기에 데이터가 올바르게 들어오는지 확인
      })
      .catch((error) => console.error("Error fetching alert data:", error));
  }, []);

  useEffect(() => {
    // alertData가 존재하지 않으면 지도 그리기를 하지 않음
    if (
      !svgRef.current ||
      dimensions.width === 0 ||
      dimensions.height === 0 ||
      Object.keys(alertData).length === 0
    )
      return;

    const svg = d3.select(svgRef.current);
    svg.selectAll("*").remove(); // Clear previous content

    const { width, height } = dimensions;

    // 프로젝션 설정
    const projection = d3
      .geoMercator()
      .scale((width / 6.28) * 0.9)
      .translate([width / 2, height / 1.5]);
    const path = d3.geoPath().projection(projection);

    // 줌 기능 설정
    const zoom = d3
      .zoom<SVGSVGElement, unknown>()
      .scaleExtent([1, 8])
      .translateExtent([
        [0, 0],
        [width, height],
      ])
      .on("zoom", (event) => {
        g.attr("transform", event.transform.toString());
      });

    // SVG 뷰포트 설정
    svg.attr("viewBox", `0 0 ${width} ${height}`);

    // 바다 배경 추가
    svg
      .append("rect")
      .attr("width", width)
      .attr("height", height)
      .attr("fill", oceanColor);

    // g 요소 추가 (줌 적용을 위해)
    const g = svg.append("g");

    // 툴팁 추가
    const tooltip = d3
      .select("body")
      .append("div")
      .attr("class", "tooltip")
      .style("position", "absolute")
      .style("visibility", "hidden")
      .style("background-color", "white")
      .style("border", "solid")
      .style("border-width", "1px")
      .style("border-radius", "5px")
      .style("padding", "5px")
      .style("font-size", "12px")
      .style("color", "black") // 글자 색을 검은색으로 설정
      .style("pointer-events", "none");

    // 지도 데이터 로드
    d3.json(
      "https://cdn.jsdelivr.net/npm/world-atlas@2/countries-110m.json",
    ).then((worldData: any) => {
      const countries = feature(
        worldData,
        worldData.objects.countries,
      ) as unknown as FeatureCollection<Geometry>;

      // 지도 그리기
      g.selectAll("path")
        .data(countries.features)
        .enter()
        .append("path")
        .attr("d", path as any)
        .attr("class", "country")
        .style("fill", (d: any) => {
          const countryName = d.properties.name;
          // console.log("Country Name from map:", countryName); // 지도 데이터의 국가 이름 확인
          const alertLevel = alertData[countryName];
          return alertColors[alertLevel as keyof typeof alertColors] || "#fff";
        })
        .style("stroke", "#333")
        .style("stroke-width", "0.5px")
        .on("mouseover", function (event, d: any) {
          const countryName = d.properties.name;
          const alertLevel = alertData[countryName];
          tooltip
            .style("visibility", "visible")
            .html(
              `${countryName}<br>경보 단계: ${
                alertLevel !== undefined ? alertLevel : "정보 없음"
              }`,
            );
          d3.select(this)
            .style("stroke", "#000") // 경계선 색상 변경
            .style("stroke-width", "1.0px"); // 경계선 두께 변경
        })
        .on("mousemove", function (event) {
          tooltip
            .style("top", event.pageY - 10 + "px")
            .style("left", event.pageX + 10 + "px");
        })
        .on("mouseout", function (event, d: any) {
          tooltip.style("visibility", "hidden");
          d3.select(this)
            .style("stroke", "#333") // 원래 경계선 색상
            .style("stroke-width", "0.5px"); // 원래 경계선 두께
        });

      // 줌 기능 적용
      svg.call(zoom);
    });
  }, [dimensions, alertData]); // alertData와 dimensions가 변경될 때마다 실행

  return (
    <div ref={containerRef} className="h-full w-full">
      <svg ref={svgRef} className="h-full w-full" />
    </div>
  );
};

export default WorldMap;
