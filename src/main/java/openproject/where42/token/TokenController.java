package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.OAuthToken;
import openproject.where42.util.Define;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TokenController {
	private final ApiService apiService;
	private final TokenRepository tokenRepository;

	// 오픈소스 전환 시 삭제
	@GetMapping(Define.WHERE42_VERSION_PATH + "/admin")
	public String TokenAccess() {
		/*** 로컬용 ***/
//		return "redirect:https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-b62a88b0deb7cdc85c7d9228410c2d1d1ca49a033772c41e26c06c0234674392&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fv1%2Fsavecode&response_type=code";
		/*** 서버용 ***/
		return "redirect:https://api.intra.42.fr/oauth/authorize?client_id=56448d39501e3f2a4d1c574a72de267e8def4da40b4b98fa29bce33063e1feff&redirect_uri=https%3A%2F%2Fwww.where42.kr%2Fv1%2Fsavecode&response_type=code";
	}

	@GetMapping("/v1/savecode")
	public String savecode(@RequestParam("code") String code) {
		OAuthToken oAuthToken = apiService.getAdminOAuthToken(code);
		tokenRepository.saveAdmin("admin", oAuthToken);
		return "success";
	}
}
