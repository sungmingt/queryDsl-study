package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository jpaRepository;
    private final MemberRepository memberRepository;


    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
        return jpaRepository.searchByWhere(condition);
    }

    @GetMapping("/v2/members")
    public List<MemberTeamDto> searchPageSimple(MemberSearchCondition condition,
                                                @RequestParam int page,
                                                @RequestParam int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<MemberTeamDto> searchPageSimple = memberRepository.searchPageSimple(condition, pageRequest);
        List<MemberTeamDto> content = searchPageSimple.getContent();

        System.out.println(searchPageSimple.getTotalElements());
        return content;
    }

    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageComplex(condition, pageable);
    }


}
