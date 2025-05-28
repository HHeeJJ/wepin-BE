package wepin.store.utils;

import com.amazonaws.services.s3.AmazonS3;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class S3UploadUtils {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String[] imageExtensionArr = {".jpg", ".bmp", ".png"};

    @Autowired
    public S3UploadUtils(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }


    /**
     * 이미지파일 확장자 체크
     */
    public boolean checkImageFileExtension(String imageFileName) {
        if (StringUtils.isNotEmpty(imageFileName) && StringUtils.contains(imageFileName, ".")) {
            String fileExtension = imageFileName.substring(imageFileName.lastIndexOf("."));
            return Arrays.stream(imageExtensionArr)
                         .anyMatch(i -> StringUtils.equals(i, fileExtension));
        } else {
            return false;
        }
    }

    //    private Optional<File> convert(String file, String memberId) throws IOException {
    //        File convertFile = new File(memberId + ".png");
    //        byte[] fileBytes = Base64.getDecoder()
    //                                 .decode(file.split(",")[1]);
    //        if (convertFile.createNewFile()) {
    //            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
    //                fos.write(fileBytes);
    //            }
    //            return Optional.of(convertFile);
    //        }
    //        return Optional.empty();
    //    }

    private Optional<File> convert(String file, String memberId) throws IOException {
        // 파일 이름을 검증 및 정제
        String sanitizedMemberId = memberId.replaceAll("[^a-zA-Z0-9]", "_");
        File   convertFile       = Files.createTempFile(sanitizedMemberId, ".png")
                                        .toFile();

        byte[] fileBytes = Base64.getDecoder()
                                 .decode(file.split(",")[1]);
        if (convertFile.exists()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(fileBytes);
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    // Multipartfile을 File 객체로 변환
    private Optional<File> convert(MultipartFile file, String memberId) throws IOException {
        File convertFile = new File(memberId + ".png");
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public String upload(String multipartFile, String dirName, String memberId) throws IOException {
        File uploadFile = convert(multipartFile, memberId).orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName);
    }

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile, String dirName, String memberId) throws IOException {
        File uploadFile = convert(multipartFile, memberId).orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String fileName       = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(bucket, fileName, uploadFile);  // AmazonS3 인터페이스의 putObject 메서드 사용
        return amazonS3.getUrl(bucket, fileName)
                       .toString();  // AmazonS3 인터페이스의 getUrl 메서드 사용
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }
}
