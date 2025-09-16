package com.tien.userservice.entity;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "user_profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "userId", nullable = false, unique = true)
    String userId;

    String firstName;
    String lastName;

    String username;

    LocalDate dob;
    String city;
}
