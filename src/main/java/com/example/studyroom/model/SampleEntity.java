package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity//이 클래스가 JPA 엔티티(데이터베이스에 직접 매팅되는 모델. basentity는 테이블에 직접 매핑되지않는 추상클래스)임을 나타냅니다.
@Table(name = "sampleEntity")//이 엔티티가 sampleEntity 테이블에 매핑됨을 나타냅니다.

public class SampleEntity extends BaseEntity {
    @Column
    private String test;//sampleEntity 테이블의 test 열과 매핑되는 필드입니다.
}
