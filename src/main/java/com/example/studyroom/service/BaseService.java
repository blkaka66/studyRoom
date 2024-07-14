//이 인터페이스는 서비스 계층의 기본적인 CRUD 연산을 정의합니다.
package com.example.studyroom.service;

import com.example.studyroom.model.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface BaseService <T extends BaseEntity>{//T는 제네릭 타입 파라미터로 BaseEntity를 상속받는 모든 엔티티타입을 의미합니다.
    //다양한 CRUD 메서드를 정의합니다. 모든 엔티티가 BaseEntity를 상속받아야 합니다.
    void update(T entity);
    void create(T entity);
    void delete(T entity);
    void saveOrUpdate(T entity);
    void create(Iterable<T> iterable);
    void delete(Iterable<T> iterable);
   // void delete(List<T> iterable)
    Optional <T> findById(Long id);
    List<T> findAll();
    Long count();
    void flush();
}
