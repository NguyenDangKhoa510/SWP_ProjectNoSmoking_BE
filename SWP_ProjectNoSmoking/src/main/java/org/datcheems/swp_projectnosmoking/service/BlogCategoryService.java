package org.datcheems.swp_projectnosmoking.service;

import org.datcheems.swp_projectnosmoking.dto.request.BlogCategoryRequest;
import org.datcheems.swp_projectnosmoking.dto.response.BlogCategoryResponse;
import org.datcheems.swp_projectnosmoking.entity.BlogCategory;
import org.datcheems.swp_projectnosmoking.repository.BlogCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogCategoryService {

    @Autowired
    private BlogCategoryRepository blogCategoryRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public BlogCategoryResponse createCategory(BlogCategoryRequest request) {
        BlogCategory category = new BlogCategory();
        category.setName(request.getName());

        BlogCategory saved = blogCategoryRepository.save(category);

        BlogCategoryResponse response = new BlogCategoryResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());

        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BlogCategoryResponse updateCategory(Long id, BlogCategoryRequest request) {
        BlogCategory category = blogCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());

        BlogCategory updated = blogCategoryRepository.save(category);

        BlogCategoryResponse response = new BlogCategoryResponse();
        response.setId(updated.getId());
        response.setName(updated.getName());

        return response;
    }

    public List<BlogCategoryResponse> getAllCategories() {
        return blogCategoryRepository.findAll().stream().map(cat -> {
            BlogCategoryResponse res = new BlogCategoryResponse();
            res.setId(cat.getId());
            res.setName(cat.getName());
            return res;
        }).collect(Collectors.toList());
    }
}
