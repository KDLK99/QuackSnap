package com.example.fase1_grupob.repository;
import com.example.fase1_grupob.model.Category;
import com.example.fase1_grupob.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT likes, p.id, description, image_name, post_title FROM POST p NATURAL JOIN CATEGORY_POSTS cp NATURAL JOIN CATEGORY c  WHERE c.id=:Category_id", nativeQuery = true)
    List<Post> findPostsByCategoryID(Long Category_id);

}
