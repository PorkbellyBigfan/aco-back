package dev.aco.back.Security.Handler;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import dev.aco.back.Entity.User.Member;
import dev.aco.back.Repository.MemberRepository;
import dev.aco.back.Utils.JWT.JWTManager;
import dev.aco.back.Utils.Redis.RedisManager;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final MemberRepository mrepo;
    private final RedisManager redisManager;
    private final JWTManager jwtManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        Member member = mrepo.findByEmail(authentication.getName()).get();
        log.info(member.getNickname());
        List<String> tokenList = jwtManager.AccessRefreshGenerator(member.getMemberId(), member.getEmail());
        redisManager.setRefreshToken(tokenList.get(1), member.getMemberId());

        HashMap<String, Object> result = new HashMap<>();
        result.put("access", tokenList.get(0));
        result.put("refresh", tokenList.get(1));
        result.put("email", member.getEmail());
        result.put("memberid", member.getMemberId());
        result.put("nickname", member.getNickname());
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();


        // 쿠키로 왜 안나가지는지 나중에 다시 확인
        // ResponseCookie cookie1 = ResponseCookie.from("access", "Bearer%20" + tokenList.get(0)).path("/").build();
        // ResponseCookie cookie2 = ResponseCookie.from("refresh", "Bearer%20" + tokenList.get(1)).path("/").build();
        //         log.info(cookie1);
        //         log.info(cookie2);
        // response.addHeader("SetCookie", cookie1.toString());
        // response.addHeader("SetCookie", cookie2.toString());
        
        
        //         //127.0.0.1:8080은 나중에 프론트 서버 주소로 변경해줍니다

        // if(member.getNickname().length()==0 || member.getPassword().length()==0){
        //     Cookie noneinituser = new Cookie("user", "{%22id%22:%22"+member.getEmail()+"%22%2C%22num%22:"+member.getMemberId().toString()
        //     +"%2C%22username%22:%22"+"PleaseInitYourInformation" +"%22}");
        //     noneinituser.setPath("/");
        //     response.addCookie(noneinituser);
        //     response.sendRedirect("http://localhost:3075/initoauth");
        // }else{
        //     Cookie user = new Cookie("user", "{%22id%22:%22" + member.getEmail() + "%22%2C%22num%22:" + member.getMemberId().toString()
        //     + "%2C%22username%22:%22" + URLEncoder.encode(member.getNickname(), "UTF-8") + "%22}");
        //     user.setPath("/");
        //     response.sendRedirect("http://localhost:3075/");
        // }
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
