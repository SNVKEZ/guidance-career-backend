package org.ssu.belous.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "gen_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NonNull
    @Column(nullable = false, name = "uuid")
    private UUID uuid;

    @Setter
    @NonNull
    @Column(nullable = false, length = 20, name = "username")
    private String username;

    @Setter
    @NonNull
    @Column(nullable = false, length = 64, name = "password")
    private String password;

    @Setter
    @NonNull
    @Column(name = "role")
    private String role;
}
