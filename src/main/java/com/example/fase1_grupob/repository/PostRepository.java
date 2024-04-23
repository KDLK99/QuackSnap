package com.example.fase1_grupob.repository;
import com.example.fase1_grupob.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT * FROM post p ORDER BY p.likes DESC", nativeQuery = true)
    List<Post> findPostsOrderByLikes();

    @Query(value = "SELECT * FROM post p ORDER BY p.counter DESC", nativeQuery = true)
    List<Post> findPostsOrderByComments();

    @Query(value = """
            SELECT * FROM post p NATURAL JOIN (SELECT DISTINCT cp.posts_id FROM category c NATURAL JOIN category_posts cp WHERE (:Category_id IS NULL OR cp.category_id=:Category_id)) as ccpi WHERE p.id=posts_id AND (:title IS NULL OR p.post_title=:title) ORDER BY CASE WHEN :orderby = 'likes' THEN p.likes
                                                                                                                                                                                                       WHEN :orderby = 'comments' THEN p.counter
                                                                                                                                                                                                       END DESC""", nativeQuery = true)
    List<Post> findPostsByCategoryIDOrdered(Integer Category_id, String orderby, String title);

    @Query(value = "SELECT * FROM post p WHERE p.post_title=:title", nativeQuery = true)
    List<Post> findPostsByPostTitle(String title);

    @Query(value = "SELECT * FROM post p WHERE p.post_title=:title ORDER BY p.likes DESC", nativeQuery = true)
    List<Post> findPostsByPostTitleOrderedByLikes(String title);

    @Query(value = "SELECT * FROM post p WHERE p.post_title=:title ORDER BY p.counter DESC", nativeQuery = true)
    List<Post> findPostsByPostTitleOrderedByComments(String title);



}
