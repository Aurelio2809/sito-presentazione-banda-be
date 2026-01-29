package org.example.sitopresentazionebandabenew.service;

import org.example.sitopresentazionebandabenew.dto.requests.MessageRequest;
import org.example.sitopresentazionebandabenew.dto.responses.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageResponse create(MessageRequest request);

    MessageResponse getById(Long id);

    Page<MessageResponse> getAll(Pageable pageable);

    Page<MessageResponse> getUnread(Pageable pageable);

    Page<MessageResponse> getRead(Pageable pageable);

    MessageResponse markAsRead(Long id);

    int markAllAsRead();

    void delete(Long id);

    long countUnread();
}
