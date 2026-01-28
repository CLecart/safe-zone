package com.safezone.product.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.safezone.product.mapper.ProductMapper;

/**
 * Fallback configuration to ensure MapStruct mappers are available as Spring
 * beans
 * in test and runtime contexts. MapStruct-generated implementations should also
 * be annotated with @Component when using componentModel=SPRING, but in some
 * test classpath situations the bean can be missing. This bean guarantees
 * availability without directly referencing the generated impl.
 */
@Configuration
public class MapperConfig {

    @Bean
    @Primary
    public ProductMapper productMapper() {
        return Mappers.getMapper(ProductMapper.class);
    }
}
