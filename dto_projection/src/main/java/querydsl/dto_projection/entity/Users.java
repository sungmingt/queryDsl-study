package querydsl.dto_projection.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String email;

    private String name;
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Board> boards = new ArrayList<>();

    @Column(nullable = false)
    @ElementCollection(targetClass = String.class)  //////Could not determine type for: java.util.List, at table: user
    private List<String> roles = new ArrayList<>();

    public Users(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public Users(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public Users(Long userId, String email, String name, String password) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    //======비즈니스 로직=======

    public void update(Users user) {
        Optional.ofNullable(user.getEmail()).
                ifPresent(email -> this.email = email);
        Optional.ofNullable(user.getName()).
                ifPresent(name -> this.name = name);
        Optional.ofNullable(user.getPassword()).
                ifPresent(password -> this.password = password);
    }
}
