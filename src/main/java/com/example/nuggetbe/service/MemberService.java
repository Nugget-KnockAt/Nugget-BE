package com.example.nuggetbe.service;

import com.example.nuggetbe.dto.request.CustomTouchPostDto;
import com.example.nuggetbe.dto.response.GetCustomTouchResponse;
import com.example.nuggetbe.entity.Connection;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.entity.Message;
import com.example.nuggetbe.repository.ConnectionRepository;
import com.example.nuggetbe.repository.MemberRepository;
import com.example.nuggetbe.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.UUID;

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


    @Transactional
    public void createConnection(UUID uuid, Long id) {


        Member member1 = memberRepository.findByUuid(uuid);
        Member member2 = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (member1 != null && member2 != null) {
            Connection connection = new Connection();
            connection.setMember(member1);
            connection.setGuardian(member2);
            connectionRepository.save(connection);
        }
    }



    public void saveCustomTouch(CustomTouchPostDto customTouchPostDto, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
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

    public GetCustomTouchResponse getCustomTouch(int touchCount, Long memberIdLong) {
        Member member = memberRepository.findById(memberIdLong)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberIdLong));

        Message message = (Message) messageRepository.findByMemberAndTouchCount(member, touchCount).orElseThrow(() -> new IllegalArgumentException("Message not found with touchCount: " + touchCount));

        return GetCustomTouchResponse.builder()
                .text(message.getText())
                .build();
    }

    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        memberRepository.delete(member);
    }
}
