package com.safezone.user.mapper;

import com.safezone.user.dto.RegisterRequest;
import com.safezone.user.dto.UserResponse;
import com.safezone.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for User entity and DTO conversions.
 * Provides compile-time generated mapping implementations.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Converts a registration request to a User entity.
     * Generated fields (id, timestamps, status) are ignored.
     *
     * @param request the registration request
     * @return the mapped User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    User toEntity(RegisterRequest request);

    /**
     * Converts a User entity to a response DTO.
     *
     * @param user the User entity
     * @return the mapped UserResponse
     */
    UserResponse toResponse(User user);

    /**
     * Converts a list of User entities to response DTOs.
     *
     * @param users the list of User entities
     * @return the list of UserResponse DTOs
     */
    List<UserResponse> toResponseList(List<User> users);
}
