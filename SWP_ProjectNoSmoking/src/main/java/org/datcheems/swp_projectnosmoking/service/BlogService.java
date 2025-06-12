package org.datcheems.swp_projectnosmoking.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.datcheems.swp_projectnosmoking.dto.request.BlogPostRequest;
import org.datcheems.swp_projectnosmoking.dto.response.BlogResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.BlogPost;
import org.datcheems.swp_projectnosmoking.mapper.BlogMapper;
import org.datcheems.swp_projectnosmoking.repository.BlogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BlogService {
    BlogRepository blogRepository;

    BlogMapper blogMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject<BlogResponse>> createBlog(BlogPostRequest request) {
        ResponseObject<BlogResponse> response = new ResponseObject<>();

        try {
            BlogPost blogPost = blogMapper.toEntity(request);

            blogRepository.save(blogPost);

            BlogResponse blogResponse = blogMapper.toResponse(blogPost);

            response.setStatus("success");
            response.setMessage("Blog created successfully");
            response.setData(blogResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to create blog: " + e.getMessage());
            response.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public List<BlogPost> getAllBlogs() {
        return blogRepository.findAll();
    }
}
