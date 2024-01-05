# [REST API 서버] "떨이 식품 중개 플랫폼 서비스"

![image](https://github.com/SUNGIN99/jatdauree/assets/90676644/4087f714-9909-43d9-a128-ce321be864fc)

> 판매자들은 매일 발생하는 떨이 식품을 처리하고, 소비자들은 저렴한 가격에 구입함으로써
>
> 음식물 낭비를 줄이고 판매 손해를 보완할 수 있는 일석삼조 효과의 중개 플랫폼 서비스



---

## 🍩 프로젝트 개요

### 서비스 소개
- 구매자 : 사회 초년생, 대학생 등 프렌차이즈 판매점의 비싼 가격 때문에 구매에 대한 부담 발생
- 판매자 : 미 판매 제품에 대한 처리비용 발생
> 위 두 가지 문제점을 해결하기 위한 떨이식품 중개 플랫폼 서비스를 고안.
>
> - 판매자 : 매일 발생하는 떨이 식품을 웹으로 할인 판매 등록
> - 구매자 : 판매자가 등록한 떨이 식품을 앱을 통해 확인하고 구매

### 진행사항
- UMC : 스터디 및 프로젝트 개발동아리
- 기획, 디자이너, 프론트 팀과의 협업 프로젝트
- 프론트 팀 레포지토리 : https://github.com/jaetteoli

---

## 🍠서버 구조도
![image](https://github.com/SUNGIN99/jatdauree/assets/90676644/7ec65b9d-ece7-49f3-9114-eb90e42e7363)

>- AWS EC2 : FreeTier t2.micro
>- OS : Ubuntu 20.04 LTS
>- Java : Oracle Open JDK "11.0.15"
>- SpringBoot : 2.7.13
>- AWS RDS : Freetier t2.micro 버스터블 클래스 & MySQL 8.0.33 
---

## 🌮기능 정의
### [API 명세서](https://docs.google.com/spreadsheets/d/1hR-vChgKXHmXQk5oXA3tq_3WOnSCR2WnJVpdXZDa1ZI/edit#gid=588458306)
 
### 판매자
#### 1. 회원제
- 회원가입, 본인인증, 로그인 
- 아이디 중복 확인, 아이디 찾기, 비밀번호 재설정

#### 2. 가게 서비스
- 가게 이름 중복확인
- 가게 등록 신청, 가게 정보 조회, 가게 정보 수정
- 영업종료

#### 3. 메뉴/원산지 등록(수정)
- 메뉴/사이드/원산지 등록, 메뉴 수정

#### 4. 주문 확인
- 주문 접수 목록 조회, 주문 접수/취소하기
- 주문표 인쇄, 영수증 인쇄
- 주문 처리 목록 조회, 픽업 완료
- 주문 완료 목록 조회

#### 5. 오늘의 메뉴 (떨이 메뉴)
- 떨이 메뉴 조회
- 떨이 메뉴 등록/ 수정

#### 6. 리뷰 확인
- 총 리뷰 평점, 리뷰 확인
- 리뷰 답글/수정/삭제, 리뷰 신고

#### 7. 매출 확인
- 오늘 매출 금액
- 어제 오늘 시간대별 매출금액
- 요일 별 매출금액
- 월간 메뉴별 판매량
- 월간 메뉴별 주문량

### 구매자
#### 1. 회원제
- 회원가입, 본인인증, 로그인
- 아이디 중복 확인, 아이디 찾기, 비밀번호 재설정
- 로그아웃, 회원 탈퇴, 회원정보 수정

#### 2. 위치 기반 지도 확인 & 가게정보
- 좌표(경/위도) 위치 설정
- 가게 조회, 지도 내 가게 목록 확인, 가게 구독 정보

#### 3. 메뉴, 장바구니, 주문, 리뷰
- 가게 등록 메뉴 확인
- 장바구니 담기/ 주문하기
- 주문내역 확인
- 리뷰 쓰기

### 관리자 
- 로그인, 가게 등록 승인, 신고 리뷰 처리

---
## ☕핵심 기능
### (구현 완료)
- Kakao 위치 기반 API를 이용한 유저 주변 가게 조회
- cool SMS를 이용한 본인인증

### (구현 예정)
- PortOne API를 이용한 결제 연동 -> 사업자 등록증 유무 때문에 구현 못함..
- 매일 갱신되는 떨이메뉴를 위한 배치 스케줄링


---
## 🍚데이터 플로우
![image](https://github.com/SUNGIN99/jatdauree/assets/90676644/c5784cf7-3806-4f95-85b9-cda04477ae6a)

---
## 🍜프로젝트 구조
```
jatdauree
	ㄴ src/main - 메인 패키지
    	ㄴ java
    		ㄴ com/example/jatdauree
         		ㄴ config - 설정 파일 및 반환 타입 클래스
           	 		ㄴ secret - 시크릿 키 및 암호화 파일 (git ignore 처리)
            	ㄴ IamPortRestClient - PortOne 결제 관련 패키지
            	ㄴ src
            		ㄴ domain - 도메인 패키지
                		ㄴ app - 구매자 도메인 패키지
                    	ㄴ kakao - 카카오 위치기반 서비스 클래스 패키지
                    	ㄴ web - 판매자 도메인 패키지
           		ㄴ utils - 암호화 및 이미지 파일 설정 클래스
            		ㄴ comein - 보안 관련 IP 클래스
                	ㄴ jwt - 인가 토큰 관련 패키지
     	ㄴ resources - 프로젝트 설정파일

```


--- 
## 🧀ERD
![재떨이_20230911_112819](https://github.com/SUNGIN99/jatdauree/assets/90676644/37bb47a2-333e-4fd6-b37f-7b016d44216c)
