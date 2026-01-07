package com.safezone.product.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.safezone.product.dto.CreateProductRequest;
import com.safezone.product.dto.ProductResponse;
import com.safezone.product.entity.Product;

/**
 * MapStruct mapper for Product entity and DTO conversions.
 * Provides compile-time generated mapping implementations.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    /**
     * Converts a creation request to a Product entity.
     * Fields id, active, createdAt, updatedAt are ignored as they are set by JPA.
     *
     * @param request the product creation request
     * @return the mapped Product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductRequest request);

    /**
     * Converts a Product entity to a response DTO.
     *
     * @param product the Product entity
     * @return the mapped ProductResponse
     */
    ProductResponse toResponse(Product product);

    /**
     * Converts a list of Product entities to response DTOs.
     *
     * @param products the list of Product entities
     * @return the list of ProductResponse DTOs
     */
    List<ProductResponse> toResponseList(List<Product> products);
}
