package com.jojoldu.book.springboot.web;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileControllerUnitTest {

    @Test
    public void real_profile이_조회된다() {
        // given
        String expectedProfile = "real";
        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.addActiveProfile(expectedProfile);
        mockEnvironment.addActiveProfile("oauth");
        mockEnvironment.addActiveProfile("real-db");

        // 생성자 주입의 장점 : 의존 관계에 있는 객체의 테스트코드 짜기가 수월. 만약 @Autowired 로 했다면 의존성을 주입받을 방법이 없음.(스프링에 의해 주입이 되기때문)
        ProfileController controller = new ProfileController(mockEnvironment);

        // when
        String profile = controller.profile();

        // then
        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    public void real_profile이_없으면_첫_번째가_조회된다() {
        // given
        String expectedProfile = "oauth";
        MockEnvironment mockEnvironment = new MockEnvironment();

        mockEnvironment.addActiveProfile(expectedProfile);
        mockEnvironment.addActiveProfile("real-db");

        // 생성자 주입의 장점 : 의존 관계에 있는 객체의 테스트코드 짜기가 수월. 만약 @Autowired 로 했다면 의존성을 주입받을 방법이 없음.(스프링에 의해 주입이 되기때문)
        ProfileController controller = new ProfileController(mockEnvironment);

        // when
        String profile = controller.profile();

        // then
        assertThat(profile).isEqualTo(expectedProfile);
    }
}
