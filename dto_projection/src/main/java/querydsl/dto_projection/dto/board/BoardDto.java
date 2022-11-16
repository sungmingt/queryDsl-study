package querydsl.dto_projection.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import querydsl.dto_projection.entity.Board;

public class BoardDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private String title;
        private String content;

        public Board postToBoard() {
            return new Board(this.title, this.content);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Patch {
        @Nullable
        private long id;
        private String title;
        private String content;

        public Board patchToBoard() {
            return new Board(this.id, this.title, this.content);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private long boardId;
        private String title;
        private String content;
        private long viewCount;
        private String userName;

        public static Response boardToResponse(Board board) {
            return new Response(board.getBoardId(), board.getTitle(), board.getContent(), board.getViewCount(), board.getUser().getName());
        }
    }
}
