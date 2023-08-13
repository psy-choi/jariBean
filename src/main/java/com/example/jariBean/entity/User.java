package com.example.jariBean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.jariBean.entity.User.UserRole.CUSTOMER;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Document
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private String id;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private String socialId;

    private String image;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String description;

    @Enumerated(STRING)
    @Column(nullable = false)
    private UserRole role;

    private boolean alarm;

    @CreatedDate
    @Column(updatable = false) // 생성일자(createdDate)에 대한 정보는 생성시에만 할당 가능, 갱신 불가
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @DBRef
    private List<Matching> matchingList;

    @DBRef
    private List<Reserved> reservedList;

    @DBRef
    private List<Table> tableList;

    @DBRef
    private List<TableClass> tableClassList;


    @Version //
    private Integer version;

    public void register() {
        this.role = CUSTOMER;
    }

    // TODO userRole, UserType

    @Getter
    @AllArgsConstructor
    public enum UserRole {
        ADMIN("관리자"), CUSTOMER("고객"), MANAGER("매니저"), UNREGISTERED("미등록");
        private String role;
    }

    @Builder
    public User(String id, String nickname, String socialId, String password, String image, UserRole role) {
        this.id = id;
        this.nickname = nickname;
        this.socialId = socialId;
        this.password = password;
        this.image = image;
        this.role = role;
        this.alarm = true;
        this.description = null;
    }

    public void updateAlarm() {
        this.alarm = !alarm;
    }

    public void updateBySocialInfo(String nickname, String image, String password) {
        this.nickname = nickname;
        this.image = image;
        this.password = password;
    }

    public void updateInfo(String nickname, String image, String description) {
        if (nickname != null){
            this.nickname = nickname;
        }
        if (image != null){
            this.image = image;
        }
        if (description != null){
            this.description = description;
        }
    }


}
