package com.example.nuggetbe.service;

import com.example.nuggetbe.dto.request.CustomTouchPostDto;
import com.example.nuggetbe.dto.request.member.LoginReq;
import com.example.nuggetbe.dto.request.member.SignupReq;
import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.GetCustomTouchResponse;
import com.example.nuggetbe.dto.response.LoginResponse;
import com.example.nuggetbe.dto.response.member.LoginRes;
import com.example.nuggetbe.entity.Connection;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.entity.Message;
import com.example.nuggetbe.entity.Role;
import com.example.nuggetbe.repository.ConnectionRepository;
import com.example.nuggetbe.repository.MemberRepository;
import com.example.nuggetbe.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import com.example.nuggetbe.config.jwt.JwtTokenProvider;
import com.example.nuggetbe.dto.response.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;


    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    public void createConnection(UUID uuid, String email) {


        Member member1 = memberRepository.findByUuid(uuid);
        Member member2 = memberRepository.findByEmail(email);
        if (member1 != null && member2 != null) {
            Connection connection = new Connection();
            connection.setMember(member1);
            connection.setGuardian(member2);
            connectionRepository.save(connection);
        }
    }


    public void saveCustomTouch(CustomTouchPostDto customTouchPostDto, String email) {

        Member member = memberRepository.findByEmail(email);
        System.out.println(member);

        processProperty(customTouchPostDto.getThird(), 3, member);
        processProperty(customTouchPostDto.getFourth(), 4, member);
        processProperty(customTouchPostDto.getFifth(), 5, member);
        processProperty(customTouchPostDto.getSixth(), 6, member);
    }

    private void processProperty(String propertyValue, int touchCount, Member member) {
        Optional<Message> result = messageRepository.findByMemberAndTouchCount(member, touchCount);

        if (StringUtils.isNotBlank(propertyValue)) {

            Message message = result.orElse(new Message());

            message.setMember(member);
            message.setTouchCount(touchCount);
            message.setText(propertyValue);

            messageRepository.save(message);
        }
    }

    public GetCustomTouchResponse getCustomTouch(int touchCount, String email) {
        Member member = memberRepository.findByEmail(email);

        Message message = (Message) messageRepository.findByMemberAndTouchCount(member, touchCount).orElseThrow(() -> new IllegalArgumentException("Message not found with touchCount: " + touchCount));

        return GetCustomTouchResponse.builder()
                .text(message.getText())
                .build();
    }

    public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email);

        memberRepository.delete(member);
    }

    public UUID getUuid(String email) {
        Member member = memberRepository.findByEmail(email);

        return member.getUuid();
    }

    public Boolean checkEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return true;
        }
        return false;
    }

    public LoginRes login(LoginReq loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail());

        if (member == null || !passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BaseException(BaseResponseStatus.INVALID_USER);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getEmail(), loginRequest.getPassword());

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtTokenProvider.createToken(authentication);

            // Generated access token
            String accessToken = "Bearer " + jwt;

            // Generated refresh token
            String refreshToken = "Bearer " + jwtTokenProvider.createRefreshToken(authentication);

            List<Connection> connections = member.getConnections();
            List<Member> guardianList = connections.stream()
                    .map(Connection::getGuardian)
                    .toList();

            return LoginRes.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .role(member.getRole())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phoneNumber(member.getPhoneNumber())
                    .uuid(member.getUuid())
                    .guardianList(guardianList)
                    .build();
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.INVALID_USER);
        }
    }


    public LoginRes signUp(SignupReq signupRequest) {
        try{
            Member existingMember = memberRepository.findByEmail(signupRequest.getEmail());
            if (existingMember != null) {
                throw new BaseException(BaseResponseStatus.USER_ALREADY_EXISTS);
            }

            Member member = new Member();
            member.setEmail(signupRequest.getEmail());
            member.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            member.setName(signupRequest.getName());
            member.setPhoneNumber(signupRequest.getPhoneNumber());
            member.setRole(signupRequest.getRole());
            member.setCreatedAt(LocalDateTime.now());
            if(signupRequest.getRole() == Role.ROLE_MEMBER) {
                member.setUuid(UUID.randomUUID());
            }

            memberRepository.save(member);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getEmail(), signupRequest.getPassword());


            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtTokenProvider.createToken(authentication);

            // Generated access token
            String accessToken = "Bearer " + jwt;

            // Generated refresh token
            String refreshToken = "Bearer " + jwtTokenProvider.createRefreshToken(authentication);

            return LoginRes.builder()

                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .role(member.getRole())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phoneNumber(member.getPhoneNumber())
                    .uuid(member.getUuid())
                    .build();
    } catch (Exception e) {
        throw new BaseException(BaseResponseStatus.INVALID_USER);
    }
    }
}
