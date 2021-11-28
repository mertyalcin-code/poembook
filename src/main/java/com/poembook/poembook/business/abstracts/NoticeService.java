package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.notification.Notice;

import java.util.List;

public interface NoticeService {
    Result create(String notice,String username);
    Result delete(Long id);
    Result deleteAll(String username);
    DataResult<List<Notice>> listAll(String username);

}
