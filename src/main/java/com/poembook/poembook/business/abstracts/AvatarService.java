package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.users.Avatar;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AvatarService {
    DataResult<List<Avatar>> getAll();

    DataResult<Avatar> getById(int id);

    DataResult<Avatar> getByUserId(Long id);

    Result add(MultipartFile multipartFile, String username);

    Result delete(int id) throws IOException;
}
