//이 클래스는 Spring REST 컨트롤러로서, SampleEntity와 관련된 HTTP 요청을 처리합니다.
package com.example.studyroom.controller;
import com.example.studyroom.model.SampleEntity;
import com.example.studyroom.service.SampleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController//이 클래스가 RESTful 웹 서비스의 컨트롤러임을 나타냅니다.
@RequestMapping("/api/v1/sample")//이 클래스의 모든 메서드는 /api/v1/sample 경로 하위에서 매핑됩니다.
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;//비즈니스 로직을 처리하는 서비스 계층을 주입받습니다.
    }

    @PostMapping("/test")
    public List<SampleEntity> test() {
        return this.sampleService.findByTest("안녕");// /api/v1/sample/test 경로로 POST 요청이 오면 sampleService를 사용하여 "안녕"이라는 값을 가진 엔티티를 검색하여 반환합니다.
    }

    @PostMapping("/{testValue}")
    public boolean post(@PathVariable String testValue){///api/v1/sample/{testValue} 경로로 POST 요청이 오면, testValue 값을 가진 새로운 SampleEntity를 생성하고 저장합니다.
        SampleEntity testEntity = new SampleEntity();
        testEntity.setTest(testValue);
        this.sampleService.create(testEntity);
        System.out.println(testEntity);
        return  true;
    }
}
