package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.contact.Contact;

import java.util.List;

public interface ContactService {

    Result submit(String firstName,String lastName,String email,String text);
    DataResult<List<Contact>> getAll();
    Result delete(Long formId);
    Result deleteAll();

}
