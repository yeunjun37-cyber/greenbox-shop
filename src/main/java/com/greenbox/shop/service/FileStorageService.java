package com.greenbox.shop.service;

import com.greenbox.shop.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/** 상품 · 리뷰 이미지 업로드 저장 */
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED = Set.of("jpg", "jpeg", "png", "gif", "webp", "svg");

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /** @return 웹에서 접근할 경로(/uploads/...), 파일이 없으면 null */
    public String store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) return null;
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (ext == null || !ALLOWED.contains(ext.toLowerCase())) {
            throw new BusinessException("이미지 파일(jpg, png, gif, webp, svg)만 업로드할 수 있습니다.");
        }
        try {
            Path dir = root.resolve(folder);
            Files.createDirectories(dir);
            String name = UUID.randomUUID() + "." + ext.toLowerCase();
            file.transferTo(dir.resolve(name));
            return "/uploads/" + folder + "/" + name;
        } catch (IOException e) {
            throw new BusinessException("파일 저장에 실패했습니다.");
        }
    }
}
