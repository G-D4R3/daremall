package dare.daremall.service;

import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;

    @Test
    public void join() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("kim");
        memberDto.setLoginId("kim1234");
        memberDto.setPassword("password!");
        memberDto.setCity("서울");
        memberDto.setStreet("강가");
        memberDto.setZipcode("00000");

        // when
        Long id = memberService.join(memberDto);

        System.out.println("password : "+memberService.findOne(id).getPassword());
        // then
        Assertions.assertThat(memberDto.getLoginId()).isEqualTo(memberService.findOne(id).getLoginId());

    }
}
