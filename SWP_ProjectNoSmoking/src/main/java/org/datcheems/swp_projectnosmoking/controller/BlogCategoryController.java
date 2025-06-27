package org.datcheems.swp_projectnosmoking.controller;

import org.datcheems.swp_projectnosmoking.dto.request.BlogCategoryRequest;
import org.datcheems.swp_projectnosmoking.dto.response.BlogCategoryResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.BlogCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog-categories")
public class BlogCategoryController {

    @Autowired
    private BlogCategoryService blogCategoryService;

    public BlogCategoryController(BlogCategoryService blogCategoryService) {
        this.blogCategoryService = blogCategoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseObject<BlogCategoryResponse>> create(@RequestBody BlogCategoryRequest request) {
        BlogCategoryResponse created = blogCategoryService.createCategory(request);

        ResponseObject<BlogCategoryResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Blog category created successfully");
        response.setData(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<BlogCategoryResponse>> update(
            @PathVariable Long id,
            @RequestBody BlogCategoryRequest request) {

        BlogCategoryResponse updated = blogCategoryService.updateCategory(id, request);

        ResponseObject<BlogCategoryResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Blog category updated successfully");
        response.setData(updated);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponseObject<List<BlogCategoryResponse>>> getAll() {
        List<BlogCategoryResponse> list = blogCategoryService.getAllCategories();

        ResponseObject<List<BlogCategoryResponse>> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Fetched all blog categories successfully");
        response.setData(list);

        return ResponseEntity.ok(response);
    }
}
