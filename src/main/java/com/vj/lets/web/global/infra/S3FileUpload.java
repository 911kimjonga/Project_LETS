package com.vj.lets.web.global.infra;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * AWS S3에 파일 업로드 컴포넌트
 *
 * @author 김종원
 * @version 1.0
 * @since 24. 7. 18. (목)
 */
@Component
@RequiredArgsConstructor
public class S3FileUpload {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String imageUpload (MultipartFile multipartFile, Object object, int typeId) throws IOException {
        // 파일명 가져오기
        String fileName = multipartFile.getOriginalFilename();
        // 확장자 추출하기
        String ext = fileName.substring(fileName.lastIndexOf("."));
        // uuid 생성하기
        String uuid = UUID.randomUUID().toString();
        // 실행 컨트롤러 추출하기
        String className = object.getClass().getSimpleName();
        String controllerType = className.substring(0, className.indexOf("Controller")).toLowerCase();
        // 실행 컨트롤러, 시퀀스, uuid, 확장자 합쳐서 S3 에 업로드할 파일명 생성
        StringBuilder uuidFileName = new StringBuilder();
        uuidFileName.append(controllerType).append("-")
                .append(typeId).append("-")
                .append(uuid).append(ext);
        // 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        // S3에 파일 업로드
        amazonS3.putObject(bucket, uuidFileName.toString(), multipartFile.getInputStream(), metadata);
        // DB에 넣을 S3 파일 경로 반환
        String objectUrl = amazonS3.getUrl(bucket, uuidFileName.toString()).toString();

        return objectUrl;
    }

}
