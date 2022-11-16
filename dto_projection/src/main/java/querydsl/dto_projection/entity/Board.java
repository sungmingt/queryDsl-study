package querydsl.dto_projection.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Board  { //Board - User : 다대일 관계

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long boardId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    private String title;
    private String content;
    private Long viewCount;

    public Board(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Board(Long boardId, String title, String content) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
    }

    //=======비즈니스 로직==========

    public void update(Board board) {
        Optional.ofNullable(board.getTitle()).
                ifPresent(title -> this.title = title);
        Optional.ofNullable(board.getContent()).
                ifPresent(content -> this.content = content);
    }

    public void addViewCount() {
        this.viewCount++;
    }

}
