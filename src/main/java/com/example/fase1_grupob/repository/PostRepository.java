package com.example.fase1_grupob.repository;
import com.example.fase1_grupob.model.Category;
import com.example.fase1_grupob.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByCategories(List<Category> categories);
}
