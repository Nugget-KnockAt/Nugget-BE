package com.example.nuggetbe.service;

import com.example.nuggetbe.config.jwt.JwtTokenProvider;
import com.example.nuggetbe.dto.request.LoginDto;
import com.example.nuggetbe.dto.request.SignUpDto;
import com.example.nuggetbe.dto.response.*;
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
public class KakaoService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Value("${kakao.redirect.url}")
    private String KAKAO_REDIRECT_URL;

    @Transactional
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
            System.out.println("카카오 엑세스 토큰 : " + kaKaoOAuthToken);

            //닉네임으로 역할 찾기
            String nickname = getOAuthInfo(kaKaoOAuthToken);
            Role role = checkRole(nickname);
            System.out.println("Role : " + role);
            Long memberId = null;

            //역할에 따른 회원가입 혹은 로그인을 위한 과정
            if(role != Role.ROLE_NONE){
                Member member = memberRepository.findByEmail(nickname);
                memberId = member.getId();
                System.out.println("memberId : " + memberId);
            } else{
                    Member member = new Member();
                    member.setEmail(nickname);
                    member.setName(nickname);
                    member.setPassword(passwordEncoder.encode("12345"));
                    member.setCreatedAt(LocalDateTime.now());
                    member.setRole(Role.ROLE_NONE);
                    memberRepository.save(member);
                    memberId = member.getId();
                    role = member.getRole();
                    System.out.println("member : " + memberId +"  "+ role);
                }

            CallbackResponse callbackResponse = CallbackResponse.builder()
                    .id(memberId)
                    .role(role)
                    .build();
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
            System.out.println("카카오 프로필 : " + response.getBody());
            ObjectMapper objectMapper = new ObjectMapper();
            KakaoOAuthProfile oAuthProfile = null;
            oAuthProfile = objectMapper.readValue(response.getBody(), KakaoOAuthProfile.class);
            System.out.println("카카오 닉네임 : " + oAuthProfile.getProperties());
            return oAuthProfile.getProperties().getNickname();
        } catch (JsonProcessingException e) {
            throw new BaseException(BaseResponseStatus.GET_OAUTH_INFO_FAILED);
        }
    }

    public Role checkRole(String email) {
        Member member = memberRepository.findByEmail(email);
        Role role = null;
        if (member == null) {
            role = Role.ROLE_NONE;
        }else {
            role = member.getRole();
        }
        return role;
    }


    public LoginResponse login(LoginDto loginDto) {

        Member member = memberRepository.findById(loginDto.getId()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NO_SUCH_MEMBER));

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

            return LoginResponse.builder()
                    .token(token)
                    .email(member.getEmail())
                    .uuid(member.getUuid())
                    .build();

        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public SignUpResponse signUp(SignUpDto signUpDto) {
        Role role = signUpDto.getRole();
        SignUpResponse signUpResponse = null;
        Member member = memberRepository.findById(signUpDto.getId()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NO_SUCH_MEMBER));


        if(role == Role.ROLE_MEMBER){
            member.setEmail(signUpDto.getEmail());
            member.setName(signUpDto.getName());
            member.setPassword(passwordEncoder.encode("12345"));
            member.setCreatedAt(LocalDateTime.now());
            member.setAddress(signUpDto.getAddress());
            member.setPhoneNumber(signUpDto.getPhoneNumber());
            member.setRole(Role.ROLE_MEMBER);
            member.setUuid(UUID.randomUUID());
            memberRepository.save(member);

            UUID uuid = member.getUuid();
            signUpResponse = SignUpResponse.builder()
                    .uuid(uuid)
                    .build();
        }else if(role == Role.ROLE_GUARDIAN) {
            member.setEmail(signUpDto.getEmail());
            member.setName(signUpDto.getName());
            member.setPassword(passwordEncoder.encode("12345"));
            member.setCreatedAt(LocalDateTime.now());
            member.setAddress(signUpDto.getAddress());
            member.setPhoneNumber(signUpDto.getPhoneNumber());
            member.setRole(Role.ROLE_GUARDIAN);
            memberRepository.save(member);

            signUpResponse = SignUpResponse.builder()
                    .uuid(null)
                    .build();
        } else{
            throw new BaseException(BaseResponseStatus.INVALID_ROLE);
        }

        return signUpResponse;
    }

}

