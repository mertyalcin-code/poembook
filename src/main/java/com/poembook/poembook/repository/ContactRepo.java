package com.poembook.poembook.repository;

import com.poembook.poembook.entities.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepo extends JpaRepository<Contact, Long> {
    Contact getByFormId(Long formId);

}
