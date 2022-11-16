package querydsl.dto_projection.repository.board;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import querydsl.dto_projection.dto.board.BoardSearch;
import querydsl.dto_projection.entity.Board;
import querydsl.dto_projection.entity.QBoard;
import querydsl.dto_projection.entity.QUsers;

import java.util.ArrayList;
import java.util.List;

import static querydsl.dto_projection.dto.board.BoardDto.*;
import static querydsl.dto_projection.entity.QBoard.*;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Board> findAllByTitle(BoardSearch boardSearch) {
        JPAQuery<?> query = jpaQueryFactory.query();

        QBoard board = QBoard.board;

        return query
                .select(board)
                .from(board)
                .where(titleEq(boardSearch.getTitle()), contentLike(boardSearch.getContent()))
                .orderBy(board.title.desc())
                .limit(10)
                .fetch();
    }

    //전체 검색 + 회원 이름 조회
    public List<Response> findAllWithUserName() {
        JPAQuery<?> query = jpaQueryFactory.query();

        QBoard board = QBoard.board;
        QUsers user = QUsers.users;

        query.select(board)
                .from(board)
                .fetchJoin()
                .fetch();

        //그냥 join
        List<Tuple> fetch = query.select(board, board.user.name)
                .from(board)
                .leftJoin(board.user, user)
                .fetch();

        List<Response> result = new ArrayList<>();
        for (Tuple tuple : fetch) {

            result.add(new Response(
                    tuple.get(board.boardId), tuple.get(board.title),
                    tuple.get(board.content), tuple.get(board .viewCount), tuple.get(board.user.name)));

        }

        //fetchJoin
        List<Board> resultByFetchJoin = jpaQueryFactory.selectFrom(board)
                .join(board.user, user).fetchJoin()
                .fetch();

        //그냥 Projection (Dto 가 아닌 몇가지 프로퍼티만 조회하고 싶은 경우)
        List<Tuple> resultWithTuple = query.select(board.boardId, board.title, board.content, board.viewCount, board.user.name)
                .from(board)
                .leftJoin(board.user, user)
                .fetch();

        for (Tuple tuple : resultWithTuple) {
            Response response = new Response(
                    tuple.get(board.boardId), tuple.get(board.title),
                    tuple.get(board.content), tuple.get(board.viewCount), tuple.get(board.user.name));
        }


        //class fields 참조를 이용한 Dto Projection
        List<Response> resultByFields = query.select(Projections.fields(
                        Response.class, board.boardId, board.title, board.content, board.viewCount, board.user.name))
                .from(board)
//                .leftJoin(board.user, user)
                .fetch();

        //생성자를 이용한 Dto Projection
        List<Response> resultByConstructor = query.select(Projections.constructor(
                        Response.class, board.boardId, board.title, board.content, board.viewCount, board.user.name))
                .from(board)
                .fetch();

        return result;
    }

    //제목으로 검색
    public BooleanExpression titleEq(String title) {
        if (title == null) {
            return null;
        }
        return board.title.eq(title);  // eq : 일치해야 함
    }

    //내용으로 검색
    public BooleanExpression contentLike(String content) {
        if (content == null) {
            return null;
        }
        return board.content.like(content);   // like : 포함
    }
}
