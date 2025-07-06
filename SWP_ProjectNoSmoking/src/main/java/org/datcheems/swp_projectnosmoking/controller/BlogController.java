package org.datcheems.swp_projectnosmoking.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.datcheems.swp_projectnosmoking.dto.request.BlogPostRequest;
import org.datcheems.swp_projectnosmoking.dto.response.BlogResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.BlogPost;
import org.datcheems.swp_projectnosmoking.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BlogController {

    BlogService blogService;

    @PostMapping("/create")
    public ResponseEntity<ResponseObject<BlogResponse>> createBlogPost(@RequestBody BlogPostRequest request) {
        return blogService.createBlog(request);
    }

    @GetMapping("/getAllBlog")
    public ResponseEntity<ResponseObject<List<BlogResponse>>> getAllBlogPosts() {
        return blogService.getAllBlogs();
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveBlog(@PathVariable Long id) {
        return blogService.approveBlog(id);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectBlog(@PathVariable Long id) {
        return blogService.rejectBlog(id);
    }

    @GetMapping("/getBlogById/{id}")
    public ResponseEntity<ResponseObject<BlogResponse>> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseObject<BlogResponse>> updateBlog(
            @PathVariable Long id,
            @RequestBody BlogPostRequest request
    ) {
        return blogService.updateBlog(id, request);
    }

}
