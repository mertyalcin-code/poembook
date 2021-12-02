package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.ContactService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.contact.Contact;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contact")
@AllArgsConstructor
public class ContactController {
    private ContactService contactService;
    @PostMapping("/submit")
    public Result submitForm(@RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String email,
                                 @RequestParam String text) {
        return contactService.submit(firstName, lastName, email,text);
    }
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
    public DataResult<List<Contact>> getAll() {
        return contactService.getAll();
    }
    @GetMapping("/delete/{formId}")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
    public Result delete(@PathVariable Long formId) {
        return contactService.delete(formId);
    }
    @GetMapping("/delete-all")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
    public Result deleteAll() {
        return contactService.deleteAll();
    }
}
