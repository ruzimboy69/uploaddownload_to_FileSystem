package com.example.lesson9_1.controller;

import com.example.lesson9_1.entity.Attachment;
import com.example.lesson9_1.entity.Attachment_content;
import com.example.lesson9_1.repository.AttachmentContentRepository;
import com.example.lesson9_1.repository.AttachmentRepository;
import org.apache.tomcat.jni.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    AttachmentContentRepository attachmentContentRepository;
    @PostMapping("/upload")
    public String uploadFile(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        if(file!=null){
            String originalFileName=file.getOriginalFilename();
            long size=file.getSize();
            String contentType=file.getContentType();
            Attachment attachment=new Attachment();
            attachment.setFileOriginalName(originalFileName);
            attachment.setContentType(contentType);
            attachment.setSize(size);
            attachmentRepository.save(attachment);

            Attachment_content attachment_content=new Attachment_content();
            attachment_content.setAttachment(attachment);
            attachment_content.setAsosiyContent(file.getBytes());
            attachmentContentRepository.save(attachment_content);
            return "saqlandi";
        }
        return "xatolik";
    }
    @GetMapping("/info")
    public List<Attachment> getAll(){
        List<Attachment> all = attachmentRepository.findAll();
        return  all;
    }
    @GetMapping ("/info/{id}")
    public Optional<Attachment> getOne(@PathVariable Integer id){
        Optional<Attachment> byId = attachmentRepository.findById(id);
        return byId;
    }
    @GetMapping("/download/{id}")
    public void getFile(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<Attachment> byId = attachmentRepository.findById(id);
        if(byId.isPresent()){
            Attachment attachment=byId.get();
            Optional<Attachment_content> byAttachmentId = attachmentContentRepository.findByAttachmentId(id);
            if (byAttachmentId.isPresent()){
                Attachment_content attachment_content=byAttachmentId.get();
                response.setHeader("Content-Disposition","attachment; filename\""+attachment.getFileOriginalName()+"\"");
                response.setContentType(attachment.getContentType());
                FileCopyUtils.copy(attachment_content.getAsosiyContent(),response.getOutputStream());
            }


        }

    }


    private static final String uploadDirectory = "filelar";
/////bir nechta fayllarni upload qilish uchun ishlatilgan
    @PostMapping("/uploadSystem")
    public String uploadFiletoFileSystem(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
       while(fileNames.hasNext()){
           MultipartFile file = request.getFile(fileNames.next());
           if (file != null) {
               String originalFilename = file.getOriginalFilename();

               Attachment attachment = new Attachment();

               attachment.setFileOriginalName(originalFilename);
               attachment.setSize(file.getSize());
               attachment.setContentType(file.getContentType());

               String[] split = originalFilename.split("\\.");

               String name = UUID.randomUUID().toString() + "." + split[split.length - 1];

               attachment.setName(name);
               attachmentRepository.save(attachment);

               Path path = Paths.get(uploadDirectory +"/"+ name);
               Files.copy(file.getInputStream(), path);
           }
       }
       return "saqlandi";
    }
@GetMapping("/showAttachment")
public List<Attachment> showAll(){
    List<Attachment> all = attachmentRepository.findAll();
    return all;

}
    @GetMapping("/downloadSystem/{id}")
    public void getFileToFileSystem(@PathVariable Integer id,HttpServletResponse response) throws IOException {
        Optional<Attachment> byId = attachmentRepository.findById(id);
        if(byId.isPresent()){
            Attachment attachment=byId.get();
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + attachment.getFileOriginalName()+"\"");
            response.setContentType(attachment.getContentType());
            FileInputStream fileInputStream=new FileInputStream(uploadDirectory+"/"+attachment.getName());
            FileCopyUtils.copy(fileInputStream, response.getOutputStream());
        }
    }
}
