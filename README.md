# Java WAS

## 2024 우아한 테크캠프 프로젝트 WAS

---

### 요구사항

**[1단계]**
- 

- http://localhost:8080/index.html 로 접속했을 때 src/main/resources/static/index.html 파일을 읽어 클라이언트에 응답한다.
- 서버로 들어오는 Request를 파싱해서 logger를 이용해 출력한다.
  - Header의 Key는 중복될 수 있다.
- 요청은 멀티 스레드로 처리되어야 한다.

**[2단계]**

- html이 아닌 다른 정적 파일을 요청했을 때도 응답할 수 있어야 한다.

### 고민사항
- 

- 헤더 파싱 시, 헤더 이름이 중복되는 경우는 후속 필드 행값을 쉼표로 구분하여 합쳤습니다.
  - rf: https://www.rfc-editor.org/rfc/rfc9110#name-field-order

### 배운 것




