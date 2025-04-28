package com.example.demo.config;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "drmr3akoc",
                "api_key", "711469594242557",
                "api_secret", "Ux8hJQMGYiIfitpM5NaMUKmQj5M",
                "secure", true
        ));
    }
}