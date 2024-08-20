/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  output: "export", // 정적 사이트 생성을 위한 설정
};

module.exports = nextConfig;
