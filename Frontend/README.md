# 프론트엔드

Hazard-Hackers 프로젝트의 프론트엔드입니다.

## 기술 스택

### Frameworks

- **[Next.js](https://nextjs.org/)**: React 기반의 웹 애플리케이션 프레임워크로, 서버사이드 렌더링(SSR)과 정적 사이트 생성(SSG)을 지원합니다.

### UI

- **[Tailwind CSS](https://tailwindcss.com/)**: 유틸리티 우선 CSS 프레임워크로, 빠르고 효율적인 UI 개발을 지원합니다.

### Data Visualization

- **[D3.js](https://d3js.org/)**: 데이터 시각화를 위한 JavaScript 라이브러리로, 복잡한 데이터 구조를 시각적으로 표현할 수 있습니다.
  - **세계 지도 시각화**: D3.js를 사용하여 세계 지도를 시각화했으며, 이를 통해 다양한 데이터를 직관적으로 표현할 수 있습니다. 프로젝트의 지리적 데이터를 시각화하거나 특정 지역에 대한 정보를 보여주는 데 활용되었습니다.

### Code Quality

- **[TypeScript](https://www.typescriptlang.org/)**: 정적 타입 검사를 제공하는 JavaScript의 상위 집합으로, 코드의 안정성과 유지 보수성을 향상시킵니다.
- **[Prettier](https://prettier.io/)**: 일관된 코드 스타일을 유지하기 위한 코드 포맷터입니다.
- **[ESLint](https://eslint.org/)**: 코드 품질을 높이기 위한 JavaScript 및 TypeScript 린터로, 코드 규칙을 적용하고 오류를 미리 방지합니다.

### 패키지 매니저

- **[pnpm](https://pnpm.io/)**: 빠르고 효율적인 패키지 매니저로, 설치 속도가 빠르고 디스크 공간을 절약할 수 있습니다.

### 런타임 환경

- **[Node.js](https://nodejs.org/)**: JavaScript 런타임 환경으로, 서버 측에서 JavaScript를 실행할 수 있도록 합니다.

### 개발 도구

- **[Visual Studio Code (VSCode)](https://code.visualstudio.com/)**: Microsoft에서 제공하는 강력한 코드 편집기로, 다양한 확장 프로그램과 함께 사용하여 개발 효율성을 높일 수 있습니다.

## 시작하기

### 필수 요구 사항

이 프로젝트를 로컬에서 실행하기 전에, 아래의 필수 요구 사항을 충족해야 합니다.

- **Node.js**
- **pnpm**

### Node.js 설치

`Node.js`가 설치되어 있지 않다면, 공식 웹사이트에서 [Node.js](https://nodejs.org/)를 설치하세요.

### pnpm 설치

`pnpm`이 설치되어 있지 않다면, 공식 웹사이트에서 [pnpm](https://pnpm.io/ko/installation)를 설치하세요.

### 라이브러리 설치

프로젝트를 클론한 후, 필요한 라이브러리를 설치합니다:

```bash
pnpm install
```

### 서버 실행

개발 서버를 시작하려면 다음 명령어를 사용하세요:

```bash
pnpm run dev
```

서버가 실행된 후, http://localhost:3000에서 애플리케이션을 확인할 수 있습니다.
