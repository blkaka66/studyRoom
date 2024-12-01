package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@MappedSuperclass
//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속 전략 지정
//@DiscriminatorColumn(name = "ticket_type", discriminatorType = DiscriminatorType.STRING) // 구분자 컬럼 설정
public class TicketEntity  extends BaseEntity{
    //referencedColumnName(참조할 외래 column명) 가없으면 코드가 알아서 id를 참조한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "FK_SHOP_ID"))
    private ShopEntity shop;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int amount;

//    @Enumerated(EnumType.STRING)  // Enum으로 ticket_type 지정
//    @Column(name = "ticket_type", nullable = false, insertable = false, updatable = false) // insertable=false, updatable=false로 설정하여 서브클래스에서 중복 매핑을 방지
//    private TicketTypeEnum ticketType; // PERIOD or TIME

//    // 공통된 필드
//    @Column(nullable = true)
//    private int days; // 기간 (nullable)
//
//    @Column(nullable = true)
//    private int hours; // 시간 (nullable)
}
