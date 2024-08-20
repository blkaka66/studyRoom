////이 클래스는 SampleService 인터페이스를 구현한 구체적인 서비스 구현체입니다.
//package com.example.studyroom.service;
////구현이 된거
//import com.example.studyroom.model.SampleEntity;
//import com.example.studyroom.repository.SampleRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service//이 클래스가 서비스 계층의 빈임을 나타냅니다.
//public class SampleServiceImpl extends BaseServiceImpl<SampleEntity> implements SampleService{
//    private final SampleRepository repository;
//
//    public SampleServiceImpl(SampleRepository repository) {
//        super(repository);
//        this.repository = repository;
//    }
//
//    @Override
//    public List<SampleEntity> findByTest(String test) {
//
//        return this.repository.findByTest(test);
//    }
//}
