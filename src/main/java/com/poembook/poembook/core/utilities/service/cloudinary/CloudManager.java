package com.poembook.poembook.core.utilities.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.ErrorDataResult;
import com.poembook.poembook.core.utilities.result.SuccessDataResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CloudManager implements CloudService {
    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud_name}")
    private String CLOUD_NAME;
    @Value("${cloudinary.api_key}")
    private String API_KEY;
    @Value("${cloudinary.api_secret}")
    private String API_SECRET;

    public CloudManager() {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", CLOUD_NAME);
        valuesMap.put("api_key", API_KEY);
        valuesMap.put("api_secret", API_SECRET);
        cloudinary = new Cloudinary(valuesMap);
    }

    public DataResult<Map<String, String>> upload(MultipartFile multipartFile) {
        File file;
        try {
            file = convert(multipartFile);
            Map<String, String> result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            file.delete();
            return new SuccessDataResult<>(result);
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorDataResult<>("Dosya yuklenmedi.");
        }
    }

    public DataResult<Map> delete(String id) throws IOException {
        Map result = cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        return new SuccessDataResult<>(result);
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(multipartFile.getBytes());
        stream.close();
        return file;

    }
}
