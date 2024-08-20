//package com.example.studyroom.repository;
//
//import com.example.studyroom.model.SampleEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//
//import java.util.List;
//import java.util.Optional;
//
//public interface SampleRepository extends JpaRepository<SampleEntity,Long> {//기본적인 CRUD 연산을 제공하는 JPA 레포지토리 인터페이스를 상속받습니다.
//    //Optional<SampleEntity> findById(Long id);
//    List<SampleEntity> findByTest(String test);// 값을 기준으로 엔티티를 검색하는 메서드입니다.
//}
