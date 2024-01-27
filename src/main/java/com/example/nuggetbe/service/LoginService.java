package com.example.nuggetbe.service;

import com.example.nuggetbe.config.jwt.JwtTokenProvider;
import com.example.nuggetbe.dto.request.LoginDto;
import com.example.nuggetbe.dto.request.SignUpDto;
import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private final Environment env;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CallbackGoogleResponse socialLogin(String code, String registrationId) {
        try {
            System.out.println("code = " + code);
            System.out.println("registrationId = " + registrationId);

            String accessToken = getAccessToken(code, registrationId);
            JsonNode userResourceNode = getUserResource(accessToken, registrationId);

            System.out.println("accessToken = " + accessToken);

            String id = userResourceNode.get("id").asText();
            String email = userResourceNode.get("email").asText();
            String nickname = userResourceNode.get("name").asText();

            System.out.println("id = " + id);
            System.out.println("email = " + email);
            System.out.println("nickname = " + nickname);

            // email로 역할 체크
            Role role = checkRole(email);
            System.out.println("role = " + role);
            Long memberId = null;

            if (role == Role.ROLE_NONE) {
                Member member = new Member();

                member.setEmail(email);
                member.setName(nickname);
                member.setPassword(passwordEncoder.encode("12345"));
                member.setCreatedAt(LocalDateTime.now());
                member.setRole(Role.ROLE_NONE);

                memberRepository.save(member);
                memberId = member.getId();
            }

            CallbackGoogleResponse response = CallbackGoogleResponse.builder()
                    .id(memberId)
                    .email(email)
                    .role(role)
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValueAsString(response);

            return response;
        } catch (JsonProcessingException e) {
            throw new BaseException(BaseResponseStatus.GET_OAUTH_TOKEN_FAILED);
        }

    }

    private Role checkRole(String email) {

        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            return Role.ROLE_NONE;
        }
        return member.getRole();
    }

    private String getAccessToken(String authorizationCode, String registrationId) {
        String clientId = env.getProperty("oauth2." + registrationId + ".client-id");
        String clientSecret = env.getProperty("oauth2." + registrationId + ".client-secret");
        String redirectUri = env.getProperty("oauth2." + registrationId + ".redirect-uri");
        String tokenUri = env.getProperty("oauth2." + registrationId + ".token-uri");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity entity = new HttpEntity<>(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();

        return accessTokenNode.get("access_token").asText();
    }

    private JsonNode getUserResource(String accessToken, String registrationId) {
        String resourceUri = env.getProperty("oauth2." + registrationId + ".resource-uri");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity<>(headers);

        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
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
            System.out.println("usernamePasswordAuthenticationToken = " + usernamePasswordAuthenticationToken);

            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            System.out.println("authentication success: " + authentication);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);

            System.out.println("context = " + context.getAuthentication());

            String jwt = jwtTokenProvider.createToken(authentication);
            String token = "Bearer" + jwt;

            System.out.println("token = " + token);

            return LoginResponse.builder()
                    .token(token)
                    .email(member.getEmail())
                    .uuid(member.getUuid())
                    .build();
        } catch (Exception e) {
            System.out.println("Authentication Failed = " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public SignUpResponse signup(SignUpDto signUpDto) {
        Role role = signUpDto.getRole();

        SignUpResponse response = null;
        Member member = memberRepository.findById(signUpDto.getId()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NO_SUCH_MEMBER)
        );

        if (role == Role.ROLE_MEMBER) {
            member.setEmail(signUpDto.getEmail());
            member.setName(signUpDto.getName());
            member.setPassword(passwordEncoder.encode("12345"));
            member.setAddress(signUpDto.getAddress());
            member.setPhoneNumber(signUpDto.getPhoneNumber());
            member.setRole(Role.ROLE_MEMBER);
            member.setCreatedAt(LocalDateTime.now());
            member.setUuid(UUID.randomUUID());

            memberRepository.save(member);

            response = SignUpResponse.builder()
                    .uuid(member.getUuid())
                    .build();
        } else if (role == Role.ROLE_GUARDIAN) {
            member.setEmail(signUpDto.getEmail());
            member.setName(signUpDto.getName());
            member.setPassword(passwordEncoder.encode("12345"));
            member.setCreatedAt(LocalDateTime.now());
            member.setAddress(signUpDto.getAddress());
            member.setPhoneNumber(signUpDto.getPhoneNumber());
            member.setRole(Role.ROLE_GUARDIAN);

            memberRepository.save(member);

            response = SignUpResponse.builder()
                    .uuid(null)
                    .build();
        } else {
            throw new BaseException(BaseResponseStatus.INVALID_ROLE);
        }

        return response;
    }
}
