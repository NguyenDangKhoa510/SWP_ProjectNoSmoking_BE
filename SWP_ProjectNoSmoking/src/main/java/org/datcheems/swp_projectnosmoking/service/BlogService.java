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


    public ResponseEntity<ResponseObject<List<BlogResponse>>> getAllBlogs() {
        ResponseObject<List<BlogResponse>> response = new ResponseObject<>();

        List<BlogPost> blogs = blogRepository.findAll();

        List<BlogResponse> blogResponses = blogs.stream()
                .map(blogMapper::toResponse)
                .toList();

        response.setStatus("success");
        response.setMessage("Blogs fetched successfully");
        response.setData(blogResponses);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseObject<BlogResponse>> getBlogById(Long id) {
        ResponseObject<BlogResponse> response = new ResponseObject<>();

        BlogPost blogPost = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found"));

        BlogResponse blogResponse = blogMapper.toResponse(blogPost);

        response.setStatus("success");
        response.setMessage("Blog fetched successfully");
        response.setData(blogResponse);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<ResponseObject<BlogResponse>> updateBlog(Long blogId, BlogPostRequest request) {
        ResponseObject<BlogResponse> response = new ResponseObject<>();

        try {
            BlogPost blogPost = blogRepository.findById(blogId)
                    .orElseThrow(() -> new IllegalArgumentException("Blog not found with ID: " + blogId));

            // Update fields
            blogPost.setTitle(request.getTitle());
            blogPost.setContent(request.getContent());
            blogPost.setCoverImage(request.getCoverImage());

            if (request.getCategoryId() != null) {
                BlogCategory category = blogCategoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
                blogPost.setCategory(category);
            }

            // Nếu update blog, mình muốn reset lại trạng thái về PENDING
            blogPost.setStatus(BlogStatus.PENDING);

            blogRepository.save(blogPost);

            BlogResponse blogResponse = blogMapper.toResponse(blogPost);

            response.setStatus("success");
            response.setMessage("Blog updated successfully and set to PENDING status.");
            response.setData(blogResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to update blog: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<ResponseObject<List<BlogResponse>>> getBlogsByCategory(Long categoryId) {
        ResponseObject<List<BlogResponse>> response = new ResponseObject<>();

        BlogCategory category = blogCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        List<BlogPost> blogs = blogRepository.findAllByCategory(category);

        List<BlogResponse> blogResponses = blogs.stream()
                .map(blogMapper::toResponse)
                .toList();

        response.setStatus("success");
        response.setMessage("Blogs fetched successfully for category ID: " + categoryId);
        response.setData(blogResponses);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<ResponseObject<String>> deleteBlog(Long blogId) {
        ResponseObject<String> response = new ResponseObject<>();

        try {
            BlogPost blogPost = blogRepository.findById(blogId)
                    .orElseThrow(() -> new IllegalArgumentException("Blog not found with ID: " + blogId));

            blogRepository.delete(blogPost);

            response.setStatus("success");
            response.setMessage("Xóa blog thành công");
            response.setData("Blog ID: " + blogId + " has been deleted.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to delete blog: " + e.getMessage());
            response.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
