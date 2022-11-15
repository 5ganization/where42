package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.Oauth.OAuthToken;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.api.dto.Utils;
import openproject.where42.cookie.AES;
import openproject.where42.Oauth.TokenDao;
import openproject.where42.cookie.MakeCookie;
import openproject.where42.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private TokenDao tokenDAO = new TokenDao();
    static private MakeCookie oven = new MakeCookie();
    private final AES aes = new AES();
    static private ApiService apiService = new ApiService();

    @GetMapping("/auth/logins")
    public String login() {
        return "auth/logins";
    }

    @GetMapping("/auth/login/callback")
    public String loginCallback(@RequestParam("code") String code, Model model, HttpServletResponse response) {

        System.out.println("callback memory  ==== " + this);
        OAuthToken oAuthToken = tokenDAO.getAllToken(code); //access_code setting
        /*** 쿠키 등록 ***/
        response.addCookie(oven.bakingCookie("access_token", aes.encoding(oAuthToken.getAccess_token()), 7200));
        response.addCookie(oven.bakingCookie("refresh_token", aes.encoding(oAuthToken.getRefresh_token()), 1209600));
        response.addCookie(oven.bakingMaxAge("1209600", 1209600));

        ResponseEntity<String> response2 = tokenDAO.callMeInfo(oAuthToken.getAccess_token()); // v2/me 부르는 로직
        Seoul42 seoul42 = apiService.seoul42Mapping(response2.getBody());

        if (!memberRepository.checkMemberByName(seoul42.getLogin())) {
            memberService.saveMember(seoul42.getLogin(), seoul42.getImage_url(), seoul42.getLocation());
            model.addAttribute("seoul42", seoul42);
            return "/member/checkAgree";
        }
//        Member member = memberRepository.findByName(seoul42.getLogin());
//
//        model.addAttribute("member", member);
        return "redirect:/auth/logins/member";
    }

    @GetMapping("/auth/logins/member")
    public String loginMember(@CookieValue("access_token") String token, Model model) {
        System.out.println("===== auth/login/member call =====");
        ResponseEntity<String> response2 = tokenDAO.callMeInfo(aes.decoding(token));
        Seoul42 seoul42 = apiService.seoul42Mapping(response2.getBody());

        /*** 로그인할 때마다 새로 갱신해야함 ***/
        Member member = memberRepository.findByName(seoul42.getLogin());
        System.out.println(member.getLocate().getFloor());
        if (member.getLocate().getFloor() == 0) // 층이 0이면 정보 없으므로 42api 호출해서 갱신
            memberService.updateLocate(member, Utils.parseLocate(seoul42.getLocation()));
        return "redirect:/auth/logins";
    }

    @GetMapping("/checkCookie")
    public String checkCookie(@CookieValue(value = "access_token", required = false) String token,
                              @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (token != null)
            return "redirect:/auth/logins/member";
        if (refreshToken == null)
            return "redirect:https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Flogin%2Fcallback&response_type=code";
        return "redirect:/invalidRefreshToken";
    }
}