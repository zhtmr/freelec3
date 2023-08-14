package com.jojoldu.book.springboot.web;

import com.jojoldu.book.springboot.config.auth.LoginUser;
import com.jojoldu.book.springboot.config.auth.dto.SessionUser;
import com.jojoldu.book.springboot.config.useragent.UserInfo;
import com.jojoldu.book.springboot.service.posts.PostsService;
import com.jojoldu.book.springboot.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;


@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user, @UserInfo String useragent) {
        model.addAttribute("posts", postsService.findAllDesc());
        // 로그인한 유저 세션 가져오기
        //  -> 어노테이션 기반(LoginUserArgumentResolver) 으로 변경 시 컨트롤러로 전달되는 파라미터에서(user) 모두 검증이 끝난 상태로 가져옴.
        //     - 파라미터는 세션에서 가져옴(resolveArgument 메소드 리턴 부분. httpsession.getAttribute)
        //     - 정의한 어노테이션이 붙어있는지 / 타입 체크

//        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("userAgent", useragent);
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);
        return "posts-update";
    }

}
