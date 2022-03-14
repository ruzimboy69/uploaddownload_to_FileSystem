package com.example.lesson9_1.repository;

import com.example.lesson9_1.entity.Attachment_content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachmentContentRepository extends JpaRepository<Attachment_content,Integer> {
    Optional<Attachment_content> findByAttachmentId(Integer id);
}
