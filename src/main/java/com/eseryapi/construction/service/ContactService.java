package com.eseryapi.construction.service;

import com.eseryapi.construction.model.ContactMessage;
import com.eseryapi.construction.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactMessageRepository repository;

    public ContactService(ContactMessageRepository repository) {
        this.repository = repository;
    }

    public ContactMessage save(ContactMessage message) {
        return repository.save(message);
    }

    public List<ContactMessage> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public long countUnread() {
        return repository.countByIsReadFalse();
    }

    public void markRead(Long id) {
        repository.findById(id).ifPresent(m -> {
            m.setRead(true);
            repository.save(m);
        });
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
