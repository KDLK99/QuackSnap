package com.example.fase1_grupob.repository;
import com.example.fase1_grupob.model.Category;
import com.example.fase1_grupob.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM POST p NATURAL JOIN (SELECT DISTINCT cp.posts_id FROM CATEGORY c NATURAL JOIN CATEGORY_POSTS cp WHERE cp.category_id=:Category_id) WHERE p.id=posts_id", nativeQuery = true)
    List<Post> findPostsByCategoryID(int Category_id);

}
