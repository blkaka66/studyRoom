//이 클래스는 모든 엔티티의 공통 속성을 정의한 추상 클래스입니다.
package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass// 이 클래스는 다른 엔티티 클래스들이 상속받아 사용할 수 있는 슈퍼클래스임을 나타냅니다.
public class BaseEntity implements Serializable {
    @Id//기본 키 필드임을 나타냅니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)//기본 키 값이 자동으로 증가되도록 설정합니다.
    private Long id;

//    @Column
//    private boolean isDel = false;
}
