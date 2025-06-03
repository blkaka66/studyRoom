package com.example.studyroom.repository;


import com.example.studyroom.model.DeletedMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeleteMemberRepository extends JpaRepository<DeletedMemberEntity, Long> {

    //DeleteMemberRepository findByShopId(long shopId);

}
