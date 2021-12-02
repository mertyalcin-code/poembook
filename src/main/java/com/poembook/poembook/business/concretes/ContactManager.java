package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.ContactService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.entities.contact.Contact;
import com.poembook.poembook.repository.ContactRepo;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.poembook.poembook.constant.ContactConstant.*;

@AllArgsConstructor
@Service
public class ContactManager implements ContactService {
    private ContactRepo contactRepo;
    @Override
    public Result submit(String firstName, String lastName, String email, String text) {
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setText(text);
        contact.setFormTime(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        contact.setFormType("contact");
        contactRepo.save(contact);
        return new SuccessResult(FORM_SEND_SUCCESS);
    }

    @Override
    public DataResult<List<Contact>> getAll() {
        List<Contact> contacts = contactRepo.findAll();
        if(contacts.size()<1){
            return new ErrorDataResult<>(CONTACT_NOT_FOUND);
        }
        return new SuccessDataResult<>(contacts,CONTACTS_LISTED);
    }

    @Override
    public Result delete(Long formId) {
        Contact contact = contactRepo.getByFormId(formId);
        if (contact==null){
            return new ErrorResult(CONTACT_NOT_FOUND);
        }
        contactRepo.delete(contact);
        return new SuccessResult(CONTACT_DELETED);
    }

    @Override
    public Result deleteAll() {
        List<Contact> contacts = contactRepo.findAll();
        if (contacts.size()<1){
            return new ErrorResult(CONTACT_NOT_FOUND);
        }
        contactRepo.deleteAll();
        return new SuccessResult(CONTACTS_DELETED);
    }
}
