package querydsl.dto_projection.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import querydsl.dto_projection.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

}
