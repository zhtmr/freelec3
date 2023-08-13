# freelec3

todo : 
  1. github action 연동
  2. 도커 멀티플랫폼 빌드
  3. ~~node lib gradle 빌드~~

trouble shooting :
  1. ec2 배포 후 빌드 단계에서 멈춤 현상 (메모리부족. 스왑메모리 설정)  
    - https://hjjooace.tistory.com/42  
    - https://kth022.tistory.com/15?category=1045193
  2. 크롬에서 ec2 dns 로 접속 안됨. (https -> http)  
    - 크롬에서 https 권유 정책으로 바뀌면서 기본적으로 도메인 앞에 https 로 세팅됨. https -> http 로 변경 후 접속
