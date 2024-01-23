package com.example.nuggetbe.service;

import com.example.nuggetbe.config.jwt.JwtTokenProvider;
import com.example.nuggetbe.dto.request.KakaoSignUpOAuthDto;
import com.example.nuggetbe.dto.request.LoginDto;
import com.example.nuggetbe.dto.request.SignUpDto;
import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import com.example.nuggetbe.dto.response.CallbackResponse;
import com.example.nuggetbe.dto.response.LoginRes;
import com.example.nuggetbe.entity.KakaoOAuthToken;
import com.example.nuggetbe.entity.KakaoOAuthProfile;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Value("${kakao.redirect.url}")
    private String KAKAO_REDIRECT_URL;

    public CallbackResponse getKakaoToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", "b721de5fbf402ff1131e42e1ce771b92");
            params.add("redirect_uri", KAKAO_REDIRECT_URL);
            params.add("code", code);
            HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token", // https://{요청할 서버 주소}
                    HttpMethod.POST, // 요청할 방식
                    kakaoTokenRequest, // 요청할 때 보낼 데이터
                    String.class // 요청 시 반환 되는 데이터 타입
            );
            //response objectMapper 로 파싱 하여 oAuthAccessToken 얻어냄
            ObjectMapper objectMapper = new ObjectMapper();
            KakaoOAuthToken kaKaoOAuthToken = null;
            kaKaoOAuthToken = objectMapper.readValue(response.getBody(), KakaoOAuthToken.class);

            //get nickname
            String nickname = getOAuthInfo(kaKaoOAuthToken);
            System.out.println(nickname);
            //check if nickname is already signed up
            CallbackResponse callbackResponse = new CallbackResponse();
            if(checkEmail(nickname)==false) {
                Member member = new Member();
                member.setEmail(nickname);
                member.setName(nickname);
                member.setPassword(passwordEncoder.encode("12345"));
                member.setCreatedAt(LocalDateTime.now());
                //And return id of new member
                Member memberInfo = memberRepository.saveAndFlush(member);

                Long result = memberInfo.getId();

                callbackResponse.setId(result);
                callbackResponse.setIsSignedUp(false);
            } else{
                Member member = memberRepository.findByEmail(nickname);
                Long result = member.getId();

                callbackResponse.setId(result);
                callbackResponse.setIsSignedUp(true);
            }

            return callbackResponse;
        } catch (JsonProcessingException e) {
            throw new BaseException(BaseResponseStatus.GET_OAUTH_TOKEN_FAILED);
        }
    }


    public String getOAuthInfo(KakaoOAuthToken oAuthAccessToken) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + oAuthAccessToken.getAccess_token());
            headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me", // https://{요청할 서버 주소}
                    HttpMethod.POST, // 요청할 방식
                    kakaoProfileRequest, // 요청할 때 보낼 데이터
                    String.class // 요청 시 반환 되는 데이터 타입
            );
            ObjectMapper objectMapper = new ObjectMapper();
            KakaoOAuthProfile oAuthProfile = null;
            oAuthProfile = objectMapper.readValue(response.getBody(), KakaoOAuthProfile.class);
            return oAuthProfile.getProperties().getNickname();
        } catch (JsonProcessingException e) {
            throw new BaseException(BaseResponseStatus.GET_OAUTH_INFO_FAILED);
        }
    }

    public boolean checkEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return false;
        }
        return true;
    }

    @Transactional
    public void signUpOAuth(KakaoSignUpOAuthDto signUpOAuthDto) {
        if(checkEmail(signUpOAuthDto.getEmail())==true) {
            Member member = new Member();
            member.setEmail(signUpOAuthDto.getEmail());
            member.setName(signUpOAuthDto.getName());
            member.setPassword(passwordEncoder.encode("12345"));
            member.setCreatedAt(LocalDateTime.now());
            memberRepository.save(member);

        } else{
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }
    }

    public LoginRes login(LoginDto loginDto) {

        Member member = memberRepository.findById(loginDto.getId()).orElseThrow(() -> new BaseException(BaseResponseStatus.NO_SUCH_MEMBER));
        if (member == null) {
            throw new BaseException(BaseResponseStatus.NO_SUCH_EMAIL);
        }

        if (!passwordEncoder.matches("12345", member.getPassword())) {
            throw new BaseException(BaseResponseStatus.WRONG_PASSWORD);
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(member.getId().toString(), "12345");

        try {
            System.out.println(usernamePasswordAuthenticationToken);

            // authenticationManager를 사용하여 인증을 수행합니다.
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            System.out.println("Authentication success: ");

            System.out.println("Authentication success: " + authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("SecurityContextHolder success: " + SecurityContextHolder.getContext().getAuthentication());
            String jwt = jwtTokenProvider.createToken(authentication);
            String token = "Bearer " + jwt;
            System.out.println("Authentication success: " + token);

            return LoginRes.builder()
                    .token(token)
                    .email(member.getEmail())
                    .uuid(member.getUuid())
                    .build();

        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return null;
        }
    }

    public void signUp(SignUpDto signUpDto) {
        Member member = memberRepository.findById(signUpDto.getId()).orElseThrow(() -> new BaseException(BaseResponseStatus.NO_SUCH_MEMBER));
        member.setEmail(signUpDto.getEmail());
        member.setName(signUpDto.getName());
        member.setPassword(passwordEncoder.encode("12345"));
        member.setCreatedAt(LocalDateTime.now());
        member.setAddress(signUpDto.getAddress());
        member.setIsSignedUp(true);
        member.setPhoneNumber(signUpDto.getPhoneNumber());
        member.setUuid(UUID.randomUUID());
        memberRepository.save(member);
    }
}

