package querydsl.dto_projection.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import querydsl.dto_projection.entity.Users;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        @NotEmpty
        @Email
        private String email;
        @NotEmpty
        private String name;
        @NotEmpty
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Patch {
        private Long id;
        private String email;
        private String name;
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String email;
        private String name;
    }

    public static Response usersToResponse(Users user) {
        return new Response(user.getUserId(), user.getEmail(), user.getName());
    }

    public static List<Response> usersToResponseList(List<Users> users) {
        return users.stream()
                .map(UserDto::usersToResponse)
                .collect(Collectors.toList());
    }
}
