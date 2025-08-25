## 이력서 평가 플랫폼 Wishy

> 당신이 **Wish**하는 삶에 함께하는 이력서 평가 플랫폼 **Wishy** 입니다
<br>

### 프로젝트 시작 계기
- 취업과 이직을 준비하던 시절, 제가 작성한 이력서와 자기소개서가 다른 사람들이 보기에도 매력적인지
판단하기 어려웠습니다. 또 여러 채용 사이트나 커뮤니티에서 저와 비슷한 고민을 하고 평가를 요청하는 많은 사람들을 보면서
이 문제가 취업이나 이직을 준비하는 많은 사람들이 공감할 수 있는 문제라고 생각했습니다.
이에 부가적인 기능으로 이러한 기능을 제공해주는 많은 사이트들이 있지만 이력서 평가에 특화된 플랫폼을 만들고 싶어 기획했습니다
<br>

### 배포 URL
<br>

### 주요 기능
1. 이력서 목록 조회를 ***Redis Cache***를 활용하여 수행 시간을 개선
2. ***Redis sorted set***을 활용하여 최근 본 이력서 기능 구현
3. 평가자가 이력서에 대한 평가를 남기면 ***SMTP를 활용하여 이력서 작성자에게 이메일 전송***
4. ***Github Action***을 활용하여 CI/CD 프로세스 구축
<br>

### 기술 스택
- Java 17, Spring Boot 3.4.2
- JPA, QueryDSL
- MySQL
- Docker
- Redis
- AWS EC2
- Github Actions
<br>

### 아키텍쳐
<img width="900" height="400" alt="image" src="https://github.com/user-attachments/assets/7e5fdcb7-cadb-4e38-9bcc-aa6128daa7e1" />

<br>

### ERD

