package org.example.sitopresentazionebandabenew.service.impl;

import org.example.sitopresentazionebandabenew.dto.requests.MessageRequest;
import org.example.sitopresentazionebandabenew.dto.responses.MessageResponse;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.ActionType;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.TargetType;
import org.example.sitopresentazionebandabenew.entity.Message;
import org.example.sitopresentazionebandabenew.entity.User;
import org.example.sitopresentazionebandabenew.exception.ResourceNotFoundException;
import org.example.sitopresentazionebandabenew.mapper.MessageMapper;
import org.example.sitopresentazionebandabenew.repository.MessageRepository;
import org.example.sitopresentazionebandabenew.service.ActivityLogService;
import org.example.sitopresentazionebandabenew.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ActivityLogService activityLogService;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper, ActivityLogService activityLogService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.activityLogService = activityLogService;
    }

    @Override
    public MessageResponse create(MessageRequest request) {
        Message message = messageMapper.toEntity(request);
        Message savedMessage = messageRepository.save(message);
        return messageMapper.toResponse(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponse getById(Long id) {
        Message message = findMessageOrThrow(id);
        return messageMapper.toResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getAll(Pageable pageable) {
        return messageRepository.findAllByOrderByReceivedAtDesc(pageable)
                .map(messageMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getUnread(Pageable pageable) {
        return messageRepository.findByReadFalseOrderByReceivedAtDesc(pageable)
                .map(messageMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getRead(Pageable pageable) {
        return messageRepository.findByReadTrueOrderByReceivedAtDesc(pageable)
                .map(messageMapper::toResponse);
    }

    @Override
    public MessageResponse markAsRead(Long id) {
        Message message = findMessageOrThrow(id);
        
        if (!message.isRead()) {
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
            message.setReadBy(getCurrentUser());
            
            message = messageRepository.save(message);

            activityLogService.log(
                    ActionType.READ,
                    TargetType.MESSAGE,
                    message.getId(),
                    message.getSubject(),
                    "Da: " + message.getSenderName()
            );
        }

        return messageMapper.toResponse(message);
    }

    @Override
    public int markAllAsRead() {
        int count = messageRepository.markAllAsRead();
        
        if (count > 0) {
            activityLogService.log(
                    ActionType.READ,
                    TargetType.MESSAGE,
                    null,
                    "Tutti i messaggi",
                    count + " messaggi segnati come letti"
            );
        }

        return count;
    }

    @Override
    public void delete(Long id) {
        Message message = findMessageOrThrow(id);
        String subject = message.getSubject();

        messageRepository.delete(message);

        activityLogService.log(ActionType.DELETE, TargetType.MESSAGE, id, subject);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread() {
        return messageRepository.countByReadFalse();
    }

    private Message findMessageOrThrow(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Messaggio", "id", id));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
