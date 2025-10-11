package com.tien.fileservice.repository;

import com.tien.fileservice.entity.FileManagement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileManagementRepository extends MongoRepository<FileManagement, String> {
}
