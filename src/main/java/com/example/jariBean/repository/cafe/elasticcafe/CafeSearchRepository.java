package com.example.jariBean.repository.cafe.elasticcafe;

import com.example.jariBean.entity.elasticentity.Cafe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CafeSearchRepository extends ElasticsearchRepository<Cafe, String>, CafeSearchRepositoryTemplate {




    List<String> findByTextLike(String text);
}
