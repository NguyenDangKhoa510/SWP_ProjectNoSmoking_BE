package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.BlogCategory;
import org.datcheems.swp_projectnosmoking.entity.BlogPost;
import org.datcheems.swp_projectnosmoking.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findAllByCategory(BlogCategory category);

}
