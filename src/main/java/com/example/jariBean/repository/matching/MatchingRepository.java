package com.example.jariBean.repository.matching;

import com.example.jariBean.entity.Matching;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface MatchingRepository extends MongoRepository<Matching, String>, MatchingRepositoryTemplate {

    List<Matching> findByUserId(String userId, Pageable pageable);

}
