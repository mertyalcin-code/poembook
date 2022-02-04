package com.poembook.poembook.core.utilities.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.ErrorDataResult;
import com.poembook.poembook.core.utilities.result.SuccessDataResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class CloudManager implements CloudService {
    private final Cloudinary cloudinary;

    private final String CLOUD_NAME;
    private final String API_KEY;
    private final String API_SECRET;

    @Autowired
    public CloudManager(
            @Value("${cloudinary.cloud_name}") String CLOUD_NAME,
            @Value("${cloudinary.api_key}")String API_KEY,
            @Value("${cloudinary.api_secret}")String API_SECRET
) {
        this.API_KEY=API_KEY;
        this.CLOUD_NAME=CLOUD_NAME;
        this.API_SECRET=API_SECRET;
        //@Value constructor dan sonra çalıştığı için bu şekilde yaptım.
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", CLOUD_NAME);
        valuesMap.put("api_key", API_KEY);
        valuesMap.put("api_secret", API_SECRET);
        cloudinary = new Cloudinary(valuesMap);
    }

    public DataResult<Map<String, String>> upload(MultipartFile multipartFile) {
        System.out.println(CLOUD_NAME);
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
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(multipartFile.getBytes());
        stream.close();
        return file;

    }
}
