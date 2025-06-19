package org.datcheems.swp_projectnosmoking.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.datcheems.swp_projectnosmoking.dto.request.BlogPostRequest;
import org.datcheems.swp_projectnosmoking.dto.response.BlogResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.BlogCategory;
import org.datcheems.swp_projectnosmoking.entity.BlogPost;
import org.datcheems.swp_projectnosmoking.entity.BlogStatus;
import org.datcheems.swp_projectnosmoking.mapper.BlogMapper;
import org.datcheems.swp_projectnosmoking.repository.BlogCategoryRepository;
import org.datcheems.swp_projectnosmoking.repository.BlogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BlogService {
    BlogRepository blogRepository;

    BlogMapper blogMapper;

    BlogCategoryRepository blogCategoryRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<ResponseObject<BlogResponse>> createBlog(BlogPostRequest request) {
        ResponseObject<BlogResponse> response = new ResponseObject<>();

        try {
            BlogPost blogPost = blogMapper.toEntity(request);

            BlogCategory category = blogCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            blogPost.setCategory(category);


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            blogPost.setStatus(isAdmin ? BlogStatus.APPROVED : BlogStatus.PENDING);

            blogRepository.save(blogPost);

            BlogResponse blogResponse = blogMapper.toResponse(blogPost);

            response.setStatus("success");
            response.setMessage(isAdmin ? "Blog created and approved." : "Blog submitted for approval.");
            response.setData(blogResponse);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to create blog: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveBlog(Long id) {
        BlogPost post = blogRepository.findById(id).orElseThrow();
        post.setStatus(BlogStatus.APPROVED);
        blogRepository.save(post);
        return ResponseEntity.ok("Approved");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectBlog(Long id) {
        BlogPost post = blogRepository.findById(id).orElseThrow();
        post.setStatus(BlogStatus.REJECTED);
        blogRepository.save(post);
        return ResponseEntity.ok("Rejected");
    }


    public List<BlogPost> getAllBlogs() {
        return blogRepository.findAll();
    }
}
