//이 인터페이스는 SampleEntity에 대한 추가적인 비즈니스 로직을 정의합니다.
package com.example.studyroom.service;

import com.example.studyroom.model.SampleEntity;

import java.util.List;
//구현을 위해 필요한것들의 목록
public interface SampleService extends BaseService<SampleEntity> {
    List<SampleEntity> findByTest(String test);//test 값을 기준으로 SampleEntity를 검색하는 메서드입니다.
}
