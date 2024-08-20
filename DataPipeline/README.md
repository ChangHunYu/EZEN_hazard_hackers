# Data Pipeline

Hazard-Hackers 프로젝트의 데이터 파이프라인입니다.

## 기술 스택

### 프로그래밍 언어

- **[Python](https://www.python.org/)**: 데이터 처리 및 분석을 위한 프로그래밍 언어입니다.

### 데이터 분석 및 처리

- **[Pandas](https://pandas.pydata.org/)**: 데이터 조작 및 분석을 위한 Python 라이브러리로, 구조화된 데이터를 처리하는 데 유용합니다.
- **[Requests](https://requests.readthedocs.io/en/latest/)**: HTTP 요청을 보내기 위한 Python 라이브러리로, 웹 API와의 상호작용을 간편하게 할 수 있습니다.

### 노트북 환경

- **[Jupyter Notebook](https://jupyter.org/)**: 데이터 분석 및 시각화를 위한 인터랙티브한 웹 애플리케이션입니다. 코드, 시각화 및 문서를 하나의 문서에서 통합하여 작업할 수 있습니다.

## 데이터 처리

이 프로젝트에서는 외교부에서 제공하는 두 가지 API를 사용하여 국가별 여행경보 데이터를 수집하고 처리했습니다.

### 외교부 API

1. **[국가별 여행경보 히스토리 목록조회 API](https://www.data.go.kr/data/15059195/openapi.do)**

   - 외교부에서 지정하는 여행 유의, 자제, 제한 등 해외여행 경보 대상국가 목록 및 상세정보의 히스토리 내역을 조회할 수 있습니다.

2. **[국가∙지역별 여행경보 조정 API](https://www.data.go.kr/data/15076243/openapi.do)**
   - 외교부에서 지정하는 여행 유의, 자제, 제한 등 해외 여행경보 대상국가 목록 및 상세정보를 제공하는 공공데이터 API 서비스입니다.

## 시작하기

### 필수 요구 사항

이 프로젝트를 로컬에서 실행하기 전에, 아래의 필수 요구 사항을 충족해야 합니다.

- **Python**
- **pip**

### Python 설치

`Python`이 설치되어 있지 않다면, 공식 웹사이트에서 [Python](https://www.python.org/)을 설치하세요.

### 라이브러리 설치

프로젝트를 클론한 후, 필요한 라이브러리를 설치합니다. `requirements.txt` 파일을 사용하여 필요한 패키지를 한 번에 설치할 수 있습니다:

```bash
pip install -r requirements.txt
```

### Jupyter Notebook 실행

Jupyter Notebook을 실행하려면, 다음 명령어를 사용하세요:

```bash
jupyter notebook
```

Jupyter Notebook 서버가 실행되면, 웹 브라우저에서 http://localhost:8888에서 노트북 인터페이스에 접근할 수 있습니다.
