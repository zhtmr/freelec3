# freelec3

### todo : 
  1. github action 연동 ( + slack 노티)
  2. 도커 멀티플랫폼 빌드
  3. node lib gradle 빌드
  4. 카카오 로그인 추가
  5. 실시간 알림 구현 (Server Sent Event) - 조회수, 좋아요, 댓글알림, 공지알림 등
  6. ~~세션 기반 로그인 -> jwt 로 바꿔보기 (트래픽 고려할것)~~
      - 사이즈(트래픽)
        - 토큰의 경우 사이즈가 크다
      - 안전성과 보안 문제
        - 세션은 서버에 저장, 토큰은 클라이언트에 저장
        - 토큰의 경우 payload 데이터를 누구나 볼 수 있음
      - 확장성
        - 서버 scale out 시 세션 불일치 문제에 세션은 따로 분리 작업 등을 해야함
      - 서버의 부담
        - 이용자가 많아질 수록 세션은 서버에 부담 증가
     
      > 소규모 서비스, 저사양 서버 -> 세션 기반  
        중/대규모 서비스, 여러대 서버 -> 토큰 기반
  7. 스프링부트 애플리케이션 모니터링 추가 (메모리 85% 이상 사용시 앱 재부팅)

---

### troubleshooting :
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

  3. code deploy 배포 오류 시 로그 보는 방법  
     1. aws console 에서 확인  
        ![view event 클릭 후 확인](img.png "view event 클릭 후 확인")  

     2. ```/var/log/aws/codedeploy-agent``` 에서 로그 확인 가능  
       ![img_1.png](img_1.png)

     3. ```/opt/codedeploy-agent/deployment-root``` 에서 code deploy 로그 확인 가능
        echo 확인 가능
     
---

### memo :

- <b>appspec.yml</b>  
    ~~~yaml
      version: 0.0
      os: linux
      files:
        - source: /
          destination: /home/ec2-user/app/step3/zip/
          overwrite: yes
        
        permissions:
        - object: /
          pattern: "**"
          owner: ec2-user
          group: ec2-user
        
        hooks:
        AfterInstall:
        - location: stop.sh
        timeout: 60
        runas: ec2-user
        ApplicationStart:
          - location: start.sh
          timeout: 60
          runas: ec2-user
          ValidateService:
          - location: health.sh
          timeout: 60
          runas: ec2-user 
  ~~~ 
  
- code deploy 에 의해 s3 에서 ec2 로 파일 이동 후 실행 훅
  - stop.sh --> start.sh --> health.sh  
    

- <b>stop.sh</b>  
  ~~~sh
    #!/usr/bin/env bash

    ABSPATH=$(readlink -f $0)
    ABSDIR=$(dirname $ABSPATH)
    source ${ABSDIR}/profile.sh
    
    IDLE_PORT=$(find_idle_port)
    
    echo "> $IDLE_PORT 에서 구동 중인 애플리케이션 pid 확인"
    IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})
    
    if [ -z ${IDLE_PORT} ]
    then
    echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
    else
    echo "> kill -15 $IDLE_PID"
    kill -15 ${IDLE_PID}
    sleep 5
    fi
  ~~~

  - profile.sh 에서 쉬고있는 profile 과 port 를 찾는다
  - <U>*현재 nginx 가 가리키지않는 프로필(IDLE_PROFILE)의 jar를 종료시킴*</U>
  - 처음 배포 시 종료 할 애플리케이션 없음. (8081로 구동)
    - 첫번째 배포 시 : 
      - CURRENT_PROFILE = ```real2``` (∵ RESPONSE_CODE >= 400)
      - IDLE_PROFILE = ```real1```
      - IDLE_PORT = ```8081```
      - 이 경우 기존 구동중인 애플리케이션 없으므로 ```real1``` 프로필로 ```8081``` 에서 jar 실행될 예정
    - 두번째 배포 시 :
      - CURRENT_PROFILE = ```real1``` (∵ curl -s http://localhost/profile 시 기존에 배포된 프로필 : ```real1```)
      - IDLE_PROFILE =  ```real2```
      - IDLE_PORT = ```8082```
      - 이 경우 기존의 ```real1``` 프로필 종료
    - 이후 :
      - 계속 번갈아 가면서 바뀐다  

   
- <b>start.sh</b>  
  ~~~sh
    #!/usr/bin/env bash

    ABSPATH=$(readlink -f $0)
    ABSDIR=$(dirname $ABSPATH)
    source ${ABSDIR}/profile.sh
    
    REPOSITORY=/home/ec2-user/app/step3
    PROJECT_NAME=freelec3
    
    echo "> Build 파일 복사"
    cp $REPOSITORY/zip/*.jar $REPOSITORY/
    
    
    echo "> 새 애플리케이션 배포"
    JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
    
    echo "> JAR_NAME: $JAR_NAME"
    echo "> $JAR_NAME 에 실행권한 추가"
    chmod +x $JAR_NAME
    
    echo "> $JAR_NAME 실행"
    
    IDLE_PROFILE=$(find_idle_profile)
    
    echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행"
    
    nohup java -jar \
    -Dspring.config.location=classpath:/application.yml,classpath:/application-$IDLE_PROFILE.yml,/home/ec2-user/app/application-oauth.yml,/home/ec2-user/app/application-real-db.yml \
    -Dspring.profiles.active=$IDLE_PROFILE \
    $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
  ~~~
  
    - 기존의 deploy.sh 와 같음. 실행 프로필만 IDLE_PROFILE 로 세팅.
      - stop.sh 에 의해 종료된 프로필이 새로 배포되어 실행될 프로필이 된다.
      - <U>*현재 nginx가 가리키고 있지 <b>않는</b> 프로필 실행시킴 (health.sh 에서 nginx 가 가리키게 될 예정)*</U>  
  

  
    
- <b>health.sh</b>  
  ~~~sh
    #! /usr/bin/env bash

    ABSPATH=$(readlink -f $0)
    ABSDIR=$(dirname $ABSPATH)
    source ${ABSDIR}/profile.sh
    source ${ABSDIR}/switch.sh
    
    IDLE_PORT=$(find_idle_port)
    
    echo "> Health Check Start!"
    echo "> IDLE_PORT: $IDLE_PORT"
    echo "> curl -s http://localhost:$IDLE_PORT/profile"
    sleep 10
    
    for RETRY_COUNT in {1..10}
    do
    RESPONSE=$(curl -s http://localhost:${IDLE_PORT}/profile)
    UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

    if [ ${UP_COUNT} -ge 1 ]
    then # $up_count >=1 ("real" 문자열이 있는지 검증)
        echo "> Health check 성공"
        switch_proxy
        break
    else
        echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
        echo "> Health check: ${RESPONSE}"
    fi

    if [ ${RETRY_COUNT} -eq 10 ]
    then
        echo "> Health check 실패."
        echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
        exit 1
    fi

    echo "> Health check 연결 실패. 재시도..."
    sleep 10
    done

  ~~~

  - start.sh 이후 실행된 *nginx가 바라보고 있지 않는 프로필*을 nginx가 바라보게 함
  - ```real*``` 프로필로 잘 배포되었는지 확인 + <U>*nginx 가 바라보게 포트 변경 (switch.sh)*</U>


- 모니터링
  ~~~sh
    #!/bin/bash

    APP_URL="http://localhost:8080/actuator/prometheus"  # Update with your actual app URL
    MEMORY_THRESHOLD=85  # Percentage threshold
    
    while true; do
    MEMORY_USAGE=$(curl -s "$APP_URL" | grep jvm_memory_used_bytes | awk '{print $2}')
    MEMORY_MAX=$(curl -s "$APP_URL" | grep jvm_memory_max_bytes | awk '{print $2}')
    MEMORY_PERCENTAGE=$(bc <<< "scale=2; $MEMORY_USAGE / $MEMORY_MAX * 100")

    if (( $(echo "$MEMORY_PERCENTAGE > $MEMORY_THRESHOLD" | bc -l) )); then
        echo "Memory utilization is above $MEMORY_THRESHOLD%. Taking action..."
        # Add your action here, such as sending a notification or restarting the app
        # For example: systemctl restart your-app-service
    fi

    sleep 300  # Check every 5 minutes
    done

  ~~~