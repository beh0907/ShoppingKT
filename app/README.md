# <div align=center>Shopping</div>
[Firebase](https://firebase.google.com/)를 활용한 kotlin 언어 기반의 쇼핑 안드로이드 어플리케이션입니다.

# 특징
* 기본 로그인과 Google,FaceBook 소셜 로그인 지원
* 가구별 상품 목록 제공
* 상품 주문 및 상태 정보 제공

# 기술스택 및 라이브러리
* 최소 SDK 24 / 타겟 SDK 34
* kotlin 언어 기반, 비동기 처리를 위한 coroutine + Flow
* 종속성 주입을 위한 [Dagger Hilt](https://dagger.dev/hilt/)
* JetPack
  * LifeCycle - Android의 수명 주기를 관찰하고 수명 주기 변경에 따라 UI 상태를 처리합니다.
  * ViewModel - UI와 DATA 관련된 처리 로직을 분리합니다.
  * ViewBinding - View(XML)과 코드(kotlin)간의 상호작용을 원활하게 처리합니다.
  * Navigation - [SmoothBottomBar](https://github.com/ibrahimsn98/SmoothBottomBar)과 연계 fragment 화면간의 이동을 처리하고 데이터 전달을 관리합니다.
  * Permissions - [TedPermission](https://github.com/ParkSangGwon/TedPermission)을 활용해 저장장치에 관한 권한을 요청하고 처리합니다.
* SharedPreferences - 유저의 로그인 정보를 저장하여 자동 로그인 처리합니다.
* Architecture
  * MVVM 패턴 적용 - Model + View + ViewModel
* 소셜 로그인
  * [Google](https://github.com/googleapis/google-auth-library-java) - Google 로그인을 위한 OAuth 2.0 인증, 권한을 부여를 통해 Google 계정 로그인을 지원합니다.
  * [FaceBook](https://developers.facebook.com/docs/facebook-login/) - FaceBook 로그인을 위한 OAuth 2.0 인증, 권한을 부여를 통해 FaceBook 계정 로그인을 지원합니다.
* Firebase
  * [Auth](https://firebase.google.com/docs/auth?hl=ko) - 유저의 인증을 관리하여 이메일/비밀번호 및 소셜 로그인과 연계하여 계정 관리
  * [FireStore](https://firebase.google.com/docs/firestore?hl=ko) - 구조화된 상품과 주문 정보를를 관리하는 NoSQL 클라우드 데이터베이스
  * [Storage](https://firebase.google.com/docs/storage/android/start?hl=ko) - 상품의 소개 이미지를 저장하고 관리하는 스토리지 서비스
* 이미지 관리
  * [Glide](https://github.com/bumptech/glide) - 효율적으로 이미지를 로드하고 적용합니다.
  * [StepView](https://github.com/shuhart/StepView) - 주문한 상품의 상태 정보를 보여줍니다.
  * [CircleImageView](https://github.com/hdodenhof/CircleImageView) - 프로필, 상품 속성 정보 등 circle 형태의 이미지뷰를 제공합니다.

# 스크린샷
|온보딩 화면-1|온보딩 화면-2|로그인 화면|
|---|---|---|
|![온보딩 화면-1](https://github.com/user-attachments/assets/8da197c5-2fd2-4e67-a929-a2e15d09ed7c)|![온보딩 화면-2](https://github.com/user-attachments/assets/5509ef58-a385-479f-a59a-ca2953e02df5)|![로그인 화면](https://github.com/user-attachments/assets/e202dd8f-2798-480c-af9f-b579bb11fd01)|

|회원가입 화면|메인 화면|검색 화면|
|---|---|---|
|![회원가입 화면](https://github.com/user-attachments/assets/40662f06-be4e-4f31-ba33-1b0df4c5f876)|![메인 화면](https://github.com/user-attachments/assets/68dc5c26-b76d-43f1-969a-c06134dd3930)|![검색 화면](https://github.com/user-attachments/assets/e0a0edc0-971b-4b4f-8698-f8e865bcbf08)|

|내 정보 화면|상품 화면|장바구니 화면|
|---|---|---|
|![내 정보 화면](https://github.com/user-attachments/assets/d1f4d487-6551-4030-9588-255d68c4b3de)|![상품 화면](https://github.com/user-attachments/assets/a47746c4-0f11-4c52-8403-4a801baa219a)|![장바구니 화면](https://github.com/user-attachments/assets/8502dcfe-0c0c-4343-a974-5930c2565f2d)|

|주문 목록 화면|주문 상태 화면|상품 주문 화면|
|---|---|---|
|![주문 목록 화면](https://github.com/user-attachments/assets/3f76bbdf-5956-4d81-a34d-a4b6f72c6239)|![주문 상태 화면](https://github.com/user-attachments/assets/5cbcd6bd-4aa8-41a7-a548-a53f36b3ea18)|![상품 주문 화면](https://github.com/user-attachments/assets/7cb556e2-1ccd-4b0e-93d6-4cbedb71c616)|