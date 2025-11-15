package com.tien.fileservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tien.fileservice.entity.Image;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
    Optional<Image> findByPublicId(String publicId);

    Optional<Image> findByOwnerIdAndImageType(String ownerId, String imageType);

    Optional<Image> findByPostId(String postId);
}
