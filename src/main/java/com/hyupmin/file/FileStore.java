package com.hyupmin.file;

import com.hyupmin.domain.attachmentFile.AttachmentFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}") // application.yml에 설정할 경로
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<AttachmentFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<AttachmentFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public AttachmentFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 실제 파일 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        // 엔티티 생성 (작성해주신 필드명에 맞춤)
        return AttachmentFile.builder()
                .fileName(originalFilename)   // 원래 이름
                .storeFileName(storeFileName) // 저장된 이름 (UUID)
                .build();
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
