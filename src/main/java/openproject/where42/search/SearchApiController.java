package openproject.where42.search;

import lombok.RequiredArgsConstructor;
import openproject.where42.Oauth.OAuthToken;
import openproject.where42.Oauth.TokenService;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.Utils;
import openproject.where42.api.dto.SearchCadet;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.exception.CookieExpiredException;
import openproject.where42.exception.SessionExpiredException;
import openproject.where42.member.MemberRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class SearchApiController {

    private final MemberRepository memberRepository;
    private final ApiService api;
    private final TokenService tokenService;

    @GetMapping("/v1/search")
    public List<SearchCadet> search42UserResponse(HttpServletRequest req, HttpServletResponse rep,
                                                  @RequestParam("begin") String begin,
                                                  @CookieValue("access_token") String token42, @CookieValue("ID") String key) {
        HttpSession session = req.getSession(false); // 이거 어디 유틸로 뺄 수 있음 뺴자
        if (session == null)
            throw new SessionExpiredException();
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        List<Seoul42> searchList = api.get42UsersInfoInRange(token42, begin, getEnd(begin));
        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();

        for (Seoul42 cadet : searchList) {
            CompletableFuture<SearchCadet> cf = api.get42DetailInfo(token42, cadet);
            SearchCadet searchCadet = null;
            try {
                searchCadet = cf.get();
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (searchCadet != null) { // json e 처리?!
                if (memberRepository.checkFriendByMemberIdAndName((Long)session.getAttribute("id"), searchCadet.getLogin()))
                    searchCadet.setFriend(true);
                searchCadetList.add(searchCadet);
            }
        }
       return searchCadetList;
    }

    private String getEnd(String begin) { // z를 여러개 넣는 거.. 뭐가 더 나을까?
        char first = begin.charAt(0); // abc - abd
        char last = begin.charAt(begin.length() - 1);
        if (first != 'z' && last == 'z') // az 면 az ~ b 까지 b 한글자인 인 intra 는 없으니까?
            return String.valueOf((char)((int)first + 1));
        else if (first == 'z' && last == 'z') // zz면 zz ~ zzz 까지 검색..? 어떻게 해야할 지 모르겟음 끝을 모름..ㅎ
            return begin + "z";
        else // a000b면 a000b ~ a000c 인데 a000c가 포함되어있어성.. 거의 일치하는 게 없을 거 같긴한데...
            return begin.substring(0, begin.length() - 1) + String.valueOf((char)((int)last + 1));
    }

    @PostMapping("/v1/search/select")
    public SearchCadet getSelectCadetInfo(@RequestBody SearchCadet cadet) {
        Utils parseInfo = new Utils(OAuthToken.tokenHane, memberRepository.findByName(cadet.getLogin()), cadet.getLocation());
        cadet.setMsg(parseInfo.getMsg());
        cadet.setLocate(parseInfo.getLocate());
        cadet.setInOrOut(parseInfo.getInOrOut());
        return cadet;
    }
}