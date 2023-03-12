package com.jasmine.filemanager.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {


    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        if(multipartFile.isEmpty()){
            return false;
        }
        return isSupportedContentType(multipartFile.getContentType());
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals(MediaType.TEXT_PLAIN_VALUE);
    }
}
