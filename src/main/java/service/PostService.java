package service;

import model.Post;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PostService {

    private ConcurrentMap<Long, Post> posts = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong(1);


    public Collection<Post> findAll() {
        return posts.values();
    }

    public Post findById(long id) {
        return posts.get(id);
    }

    public void save(Post post) {

        if(post.getId() == null || post.getId() == 0) {
            long id = nextId.getAndIncrement();
            post.setId(id);
        }
        this.nextId.getAndIncrement();

        this.posts.put(post.getId(), post);
    }

    public void deleteById(long id) {
        this.posts.remove(id);
    }

    public List<Post> filteredPosts(String category){
        List<Post> aux = new ArrayList<>();
        for (Post post :  this.findAll()) {
            if (post.checkCategory(category)) {
                aux.add(post);
            }
        }
        return aux;
    }

    public AtomicLong getNextId(){
        return this.nextId;
    }

}
