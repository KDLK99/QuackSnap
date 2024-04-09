package com.example.fase1_grupob.repository;
import com.example.fase1_grupob.model.Category;
import com.example.fase1_grupob.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM post p NATURAL JOIN (SELECT DISTINCT cp.posts_id FROM category c NATURAL JOIN category_posts cp WHERE cp.category_id=:Category_id) as ccpi WHERE p.id=posts_id", nativeQuery = true)
    List<Post> findPostsByCategoryID(int Category_id);

}
