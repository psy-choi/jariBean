package com.example.jariBean.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDateTime;

@Document
@Getter
@NoArgsConstructor
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matchingId")
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private LocalDateTime matchingTime = LocalDateTime.now();

    @Column(nullable = false)
    private Status status;

    enum Status {
        CANCEL, PROCESSING, COMPLETE, NOSHOW
    }

    @Column(nullable = false)
    private Cafe cafe;

    @DBRef
    private User user;

    @CreatedDate
    @Column(updatable = false) // 생성일자(createdDate)에 대한 정보는 생성시에만 할당 가능, 갱신 불가
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Version //
    private Integer version;

    @Builder
    public Matching(String userId, Cafe cafe, Integer number){
        this.userId = userId;
        this.cafe = cafe;
        this.number = number;
    }

}
