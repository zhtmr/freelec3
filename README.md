# freelec3

todo : 
  1. github action 연동
  2. 도커 멀티플랫폼 빌드
  3. node lib gradle 빌드
  4. 카카오 로그인 추가 

trouble shooting :
  1. ec2 배포 후 빌드 단계에서 멈춤 현상 (메모리부족. 스왑메모리 설정)  
     1. t2.micro 기준(램 1기가) 권장 스왑 파일은 2GB(128MB x 16)  
    ```$ sudo dd if=/dev/zero of=/swapfile bs=128M count=16```  
     2. 스왑 파일의 읽기 및 쓰기 권한을 업데이트    
    ```$ sudo chmod 600 /swapfile```  
     3. Linux 스왑 영역을 설정  
    ```$ sudo mkswap /swapfile```  
     4. 스왑 공간에 스왑 파일을 추가하여 스왑 파일을 즉시 사용할 수 있도록.  
     ```$ sudo swapon /swapfile```  
     5. 프로시저가 성공적인지 확인.  
     ```$ sudo swapon -s```  
     6. **/etc/fstab** 파일을 편집하여 부팅 시 스왑 파일을 시작.  
     ```$ sudo vi /etc/fstab```  
     
        파일 끝에 다음 줄을 새로 추가하고 파일을 저장한 다음 종료  
     ```/swapfile swap swap defaults 0 0```  
     
     - https://hjjooace.tistory.com/42  
     - https://kth022.tistory.com/15?category=1045193  
     - https://repost.aws/ko/knowledge-center/ec2-memory-swap-file

  2. 크롬에서 ec2 dns 로 접속 안됨. (https -> http)  
    - 크롬에서 https 권유 정책으로 바뀌면서 기본적으로 도메인 앞에 https 로 세팅됨. https -> http 로 변경 후 접속
