package org.datcheems.swp_projectnosmoking.mapper;

import org.datcheems.swp_projectnosmoking.dto.request.BlogPostRequest;
import org.datcheems.swp_projectnosmoking.dto.response.BlogResponse;
import org.datcheems.swp_projectnosmoking.entity.BlogPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    BlogMapper INSTANCE = Mappers.getMapper(BlogMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    BlogPost toEntity(BlogPostRequest request);

    BlogResponse toResponse(BlogPost blogPost);
}
