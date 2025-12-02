package com.hyupmin.file;

import com.hyupmin.domain.attachmentFile.AttachmentFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class FileStore {

    @Value("${file.dir}")           // 예: C:/upload/ or /home/upload/
    private String fileDir;

    // 단일 파일 최대 크기 (기본 10MB) - application.yml에서 오버라이드 가능
    @Value("${file.max-size-per-file:10485760}")
    private long maxFileSizePerFile;

    // 허용 확장자 목록
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png",
            "pdf", "hwp", "hwpx",
            "ppt", "pptx",
            "doc", "docx",
            "xls", "xlsx",
            "zip", "txt"
    );

    /**
     * 실제 저장 경로 + 파일명 (ex. C:/upload/uuid.png)
     */
    public String getFullPath(String filename) {
        if (!fileDir.endsWith(File.separator)) {
            return fileDir + File.separator + filename;
        }
        return fileDir + filename;
    }

    /**
     * 여러 파일 저장 (비어있는 파일은 무시)
     */
    public List<AttachmentFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<AttachmentFile> storeFileResult = new ArrayList<>();

        if (multipartFiles == null) {
            return storeFileResult;
        }

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                AttachmentFile attachmentFile = storeFile(multipartFile);
                storeFileResult.add(attachmentFile);
            }
        }
        return storeFileResult;
    }

    /**
     * 단일 파일 저장
     */
    public AttachmentFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // 1. 기본 정보 추출
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("파일 이름을 가져올 수 없습니다.");
        }

        String ext = extractExt(originalFilename);
        long size = multipartFile.getSize();

        // 2. 검증 (확장자, 용량)
        validateExtension(ext, originalFilename);
        validateFileSize(size, originalFilename);

        // 3. 저장용 파일명 생성 (UUID)
        String storeFileName = createStoreFileName(ext);

        // 4. 디렉터리 없으면 생성
        File dir = new File(fileDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new IOException("파일 저장 디렉터리를 생성할 수 없습니다: " + fileDir);
            }
        }

        // 5. 실제 파일 저장
        File saveTarget = new File(getFullPath(storeFileName));
        multipartFile.transferTo(saveTarget);

        // 6. 엔티티 생성 (엔티티 필드명에 맞추기)
        return AttachmentFile.builder()
                .originalFileName(originalFilename)        // 원본 파일명
                .storedFileName(storeFileName)             // 서버에 저장된 파일명 (UUID.xxx)
                .filePath(saveTarget.getAbsolutePath())    // or fileDir만 저장해도 됨
                .fileSize(size)                            // 바이트 단위 크기
                .fileType(ext)                             // 확장자 (jpg, pdf 등)
                .isDeleted(false)                          // 기본값: 삭제 안 됨
                .createdAt(LocalDateTime.now())            // 업로드 시각
                .build();
    }

    /**
     * 저장될 파일명 생성 (UUID.확장자)
     */
    private String createStoreFileName(String ext) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    /**
     * 확장자 추출
     */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if (pos == -1) {
            throw new IllegalArgumentException("확장자가 없는 파일은 업로드할 수 없습니다: " + originalFilename);
        }
        return originalFilename.substring(pos + 1).toLowerCase();
    }

    /**
     * 허용 확장자 검사
     */
    private void validateExtension(String ext, String originalFilename) {
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException(
                    "허용되지 않은 파일 형식입니다. 파일명: " + originalFilename + ", 확장자: " + ext
            );
        }
    }

    /**
     * 단일 파일 용량 검사
     */
    private void validateFileSize(long size, String originalFilename) {
        if (size > maxFileSizePerFile) {
            throw new IllegalArgumentException(
                    "파일 크기가 제한을 초과했습니다. (" +
                            "제한: " + maxFileSizePerFile + " bytes, " +
                            "실제: " + size + " bytes, " +
                            "파일명: " + originalFilename + ")"
            );
        }
    }
}
