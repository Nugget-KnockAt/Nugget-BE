package com.example.nuggetbe.service;

import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.entity.Role;
import com.example.nuggetbe.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final RestTemplate restTemplate = new RestTemplate();

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
            System.out.println("userResourceNode = " + userResourceNode);

            String id = userResourceNode.get("id").asText();
            String email = userResourceNode.get("email").asText();

            System.out.println("id = " + id);
            System.out.println("email = " + email);

            // email로 역할 체크
            Role role = checkRole(email);
            System.out.println("role = " + role);
            Long memberId = null;

            if (role == Role.ROLE_NONE) {
                Member member = new Member();

                member.setEmail(email);
                member.setName(email);
                member.setPassword(passwordEncoder.encode("12345"));
                member.setCreatedAt(LocalDateTime.now());
                member.setRole(Role.ROLE_NONE);

                memberRepository.save(member);
                memberId = member.getId();
            } else {
                memberId = memberRepository.findByEmail(email).getId();
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
}