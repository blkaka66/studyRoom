//이 클래스는 BaseService 인터페이스를 구현한 기본 서비스 구현체입니다.
package com.example.studyroom.service;

import com.example.studyroom.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {
    private JpaRepository<T, Long> repository;
    public BaseServiceImpl(JpaRepository<T, Long> repository) {// JPA 레포지토리를 주입받아 다양한 CRUD 연산을 수행합니다.
        this.repository = repository;
    }

    @Override
    public void update(T entity) {
        repository.save(entity);
    }

    @Override
    public void create(T entity) {
        repository.saveAndFlush(entity);
    }

    @Override
    public void delete(T entity) {
        // entity.setIsDel(true);
        // this.update(entity); 원래는 이렇게하는데 where가 안돼서 나중에
        repository.delete(entity);
    }

    @Override
    public void saveOrUpdate(T entity) {
        if(entity.getId() == null || entity.getId()<1){
            this.create(entity);
        }else{
            this.update(entity);
        }
    }

    @Override
    public void create(Iterable<T> iterable) {
        repository.saveAllAndFlush(iterable);
    }

    @Override
    public void delete(Iterable<T> iterable) {
        repository.deleteAllInBatch(iterable);
    }

    @Override
    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
