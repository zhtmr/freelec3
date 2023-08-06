package com.jojoldu.book.springboot.config.auth.dto;

import com.jojoldu.book.springboot.domain.user.Role;
import com.jojoldu.book.springboot.domain.user.User;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name(String.valueOf(response.get("name")))
                .email(String.valueOf(response.get("email")))
                .picture(String.valueOf(response.get("profile_image")))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .picture(String.valueOf(attributes.get("picture")))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public static OAuthAttributesBuilder builder() {
        return new OAuthAttributesBuilder();
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }

    public static class OAuthAttributesBuilder {
        private Map<String, Object> attributes;
        private String nameAttributeKey;
        private String name;
        private String email;
        private String picture;

        OAuthAttributesBuilder() {
        }

        public OAuthAttributesBuilder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public OAuthAttributesBuilder nameAttributeKey(String nameAttributeKey) {
            this.nameAttributeKey = nameAttributeKey;
            return this;
        }

        public OAuthAttributesBuilder name(String name) {
            this.name = name;
            return this;
        }

        public OAuthAttributesBuilder email(String email) {
            this.email = email;
            return this;
        }

        public OAuthAttributesBuilder picture(String picture) {
            this.picture = picture;
            return this;
        }

        public OAuthAttributes build() {
            return new OAuthAttributes(this.attributes, this.nameAttributeKey, this.name, this.email, this.picture);
        }

        public String toString() {
            return "OAuthAttributes.OAuthAttributesBuilder(attributes=" + this.attributes + ", nameAttributeKey=" + this.nameAttributeKey + ", name=" + this.name + ", email=" + this.email + ", picture=" + this.picture + ")";
        }
    }
}
