package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {

        queryFactory = new JPAQueryFactory(em);


        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {

        //member1 찾기
        Member findByJPQL = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findByJPQL.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQueryDsl() {

        //member1 찾기
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }






    //================projection======================


    @DisplayName("Projection 1개")
    @Test
    public void SimpleProjection() {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @DisplayName("Projection 여러개")
    @Test
    public void tupleProjection() {

        //반환타입이 여러개일 경우 tuple
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        //tuple 은 querydsl 에서 제공하는 타입이기 때문에 Repository 에서만 사용하는 것이 좋다. (의존성 최소화)
        //  -> 서비스 계층으로 던질때에는 DTO 로 변환해서 나가야함
        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);

            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }


    //==========DTO Projection================

    //JPQL 로 DTO projection  :  package 이름을 다 적어줘야 함 + 생성자 방식만 지원함
    @Test
    public void findDtoByJPQL() {
        List<MemberDto> result =
                em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age)" +
                                " from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }


    //QueryDsl 로 DTO projection :  3가지 방식 지원  ->  프로퍼티 접근, 필드 직접 접근, 생성자 사용
    @Test
    public void findDtoBySetter() {

        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test  //위의 프로퍼티 접근 : getter/setter를 통해 접근,  이건 필드에 그냥 바로 데이터 넣어줌
   public void findDtoByField() {

        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }


        //필드 접근의 경우 필드 이름이 맞아야 한다. 아래와 같이 MemberDto가 아닌 UserDto를 사용하더라도
        //alias를 사용해 필드 이름이 같도록 한다면 projection에 성공한다.
        List<UserDto> example = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        member.age))
                .from(member)
                .fetch();
    }

    @Test
    public void findDtoByConstructor() {

        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    //@QueryProjection
    @Test
    public void findDtoByQueryProjection() {  //constructor 방식과의 차이 : 이 방법은 컴파일시점에 필드 타입체크 등 가능
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))  //(Dto에 해당 애노테이션을 붙여주면 Q타입 Dto를 생성해준다)
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }

        //이 방식은 타입 체크 등 가장 안정적인 방법이라 할 수 있지만, 단점이 있따
        //DTO의 경우 서비스, API 계층 등 여러 곳에서 사용될 수 있는데, DTO가 queryDsl에 의존하게 되면서 의존관계가 꼬일 수 있다.

    }


    //===========동적 쿼리===============


    //BooleanBuilder 사용
    public void dynamicQuery_BooleanBuilder() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {

        BooleanBuilder builder = new BooleanBuilder();
        if (usernameParam != null) {
            builder.and(member.username.eq(usernameParam));
        }
        if (ageParam != null) {
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }


    //Where 다중 파라미터 사용
    public void dynamicQuery_WhereParam() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameParam), ageEq(ageParam))
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameParam) {
        if (usernameParam == null) {  //where에 null이 들어감 -> 무시됨
            return null;
        }
        return member.username.eq(usernameParam);
    }
    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;
    }

    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }



    //==============수정/삭제 벌크 연산 (쿼리 한번으로 대량 데이터 수정/삭제)==================
    //jpa의 변경감지는 한건한건 일어나기 때문에 쿼리가 많이 나가는데, 한 번에 수정해야 할 떄 이것을 사용

    @Test
    public void bulkUpdate() {
        //나이가 28 이하인 회원들의 이름을 비회원으로 변경
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        em.flush();
        em.clear();
        //이러한 벌크 연산은 영속성 컨텍스트를 거치지 않고 바로 DB로 쿼리가 나간다.
        //이 쿼리가 실행되고, update된 회원을 사용해야 하는 상황에, 만일 영속성 컨텍스트에 기존 회원1의 이름이 저장되어 있다면, DB에서 가져온 정보보다
        //영속성 컨텍스트의 정보가 우선시되어 데이터가 변하지 않은 채로 연산이 실행됨

        //해결책 : 벌크 연산 후에 항상 em.flush() + em.clear() 해서 DB랑 맞춰주는 게 좋다.
    }

    //벌크 숫자 연산도 가능
    @Test
    public void bulkAdd() {
        //모든 회원의 나이를 1 더하기
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .execute();
    }

    //벌크 삭제
    @Test
    public void bulkDelete() {
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }


    //==========SQL function 호출하기===========


}

