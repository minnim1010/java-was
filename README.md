# Java WAS

## 2024 우아한 테크캠프 프로젝트 WAS

---

### 요구사항

**[1단계]**

**기본 요구 사항**

- http://localhost:8080/index.html 로 접속했을 때 src/main/resources/static/index.html 파일을 읽어 클라이언트에 응답한다.
- 서버로 들어오는 Request를 파싱해서 logger를 이용해 출력한다.
- 요청은 멀티 스레드로 처리되어야 한다.

**추가 요구 사항**

- Header의 Key가 중복된 요청도 잘 처리할 수 있어야 한다.

<br>

**[2단계]**

- html이 아닌 다른 정적 파일을 요청했을 때도 응답할 수 있어야 한다.

<br>

**[3단계]**

**기본 요구 사항**

- “회원가입” 메뉴를 클릭하면 http://localhost:8080/registeration.html 로 이동, 회원가입 폼을 표시한다.
- 회원가입 폼에서 `가입` 버튼을 클릭하면 다음 형태로 사용자가 입력한 값이 서버에 전달되어야 한다.
  - `/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net`
- 사용자가 입력한 값을 파싱해 model.User 클래스에 저장한다.

**추가 요구 사항**

- 원활한 HTTP 처리를 위해 Content-Length(바디가 있는 경우), Date 헤더를 설정한다.

<br>

**[4단계]**

**기본 요구 사항**

- 로그인을 GET에서 POST로 수정 후 정상 동작하도록 구현한다.
  - GET으로 회원가입을 시도할 경우 실패해야 한다.
  - 가입을 완료하면 `/index.html` 페이지로 이동한다.
  - 바디에 존재하는 데이터는 Content-Length만큼 받아 처리해야 한다.

<br>
<br>

### 고민 사항

- 헤더 파싱 시, 헤더 이름이 중복되는 경우는 후속 필드 행값을 쉼표로 구분하여 합쳤습니다.
  - rf: https://www.rfc-editor.org/rfc/rfc9110#name-field-order
- 프로그램 안정성을 위해 JVM Runtime.getRuntime().addShutdownHook()을 사용하여 종료 시, 소켓 및 스레드 풀 자원을 정리하도록 했습니다.
- 