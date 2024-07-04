import time
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service

# Chrome 옵션 설정
chrome_options = Options()
chrome_options.add_argument("--incognito")  # 시크릿 모드 활성화
chrome_options.add_argument("--auto-open-devtools-for-tabs")  # 개발자 도구 자동 열기

service = Service("/Users/woowatech22/Downloads/chromedriver-mac-x64/chromedriver")

driver = webdriver.Chrome(service=service, options=chrome_options)

driver.get("http://localhost:8080/index.html")

driver.execute_script("""
    let timeout;
    function resetTimeout() {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            window.close();
        }, 60000);
    }

    window.onload = resetTimeout;
    window.onmousemove = resetTimeout;
    window.onmousedown = resetTimeout; // 사용자가 클릭한 경우
    window.ontouchstart = resetTimeout; // 사용자가 터치한 경우
    window.onclick = resetTimeout; // 사용자가 클릭한 경우
    window.onkeypress = resetTimeout; // 사용자가 키를 누른 경우
    window.addEventListener('scroll', resetTimeout, true); // 사용자가 스크롤한 경우
""")

time.sleep(120)

driver.quit()
