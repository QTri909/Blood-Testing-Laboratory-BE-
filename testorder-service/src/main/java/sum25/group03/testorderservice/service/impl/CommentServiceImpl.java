package sum25.group03.testorderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.request.CommentRequestDTO;
import sum25.group03.testorderservice.dtos.response.CommentResponseDTO;
import sum25.group03.testorderservice.entities.Comment;
import sum25.group03.testorderservice.enums.CommentStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.mapper.CommentMapper;
import sum25.group03.testorderservice.repositories.CommentRepository;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.service.interfaces.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TestOrderRepository testOrderRepository;
    private final TestResultRepository testResultRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDTO createComment(CommentRequestDTO requestDTO) {
        log.info("Creating new comment for testOrderId: {} or testResultId: {}",
                requestDTO.getTestOrderId(), requestDTO.getTestResultId());

        validateCommentRequest(requestDTO);

        Comment comment = commentMapper.toEntity(requestDTO);
        comment.setStatus(CommentStatus.ACTIVE);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
//        comment.setCreatedBy("SYSTEM"); // TODO: Replace with actual user from IAM

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with id: {}", savedComment.getId());

        return commentMapper.toResponseDto(savedComment);
    }

    @Override
    public CommentResponseDTO updateComment(Long id, CommentRequestDTO requestDTO) {
        log.info("Updating comment with id: {}", id);

        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        if (existingComment.getStatus() == CommentStatus.DELETED) {
            throw new IllegalStateException("Cannot update deleted comment");
        }

        validateCommentRequest(requestDTO);

        commentMapper.updateEntity(requestDTO, existingComment);
        existingComment.setUpdatedAt(LocalDateTime.now());
//        existingComment.setUpdatedBy("SYSTEM");

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment updated successfully with id: {}", updatedComment.getId());

        return commentMapper.toResponseDto(updatedComment);
    }

    @Override
    public void deleteComment(Long id) {
        log.info("Deleting comment with id: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new IllegalStateException("Comment already deleted");
        }

        comment.setStatus(CommentStatus.DELETED);
        comment.setUpdatedAt(LocalDateTime.now());
//        comment.setDeletedBy();

        commentRepository.save(comment);
        log.info("Comment deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO getCommentById(Long id) {
        log.info("Fetching comment with id: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new ResourceNotFoundException("Comment has been deleted");
        }

        return commentMapper.toResponseDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByTestOrderId(Long testOrderId) {
        log.info("Fetching comments for testOrderId: {}", testOrderId);

        if (!testOrderRepository.existsById(testOrderId)) {
            throw new ResourceNotFoundException("Test order not found with id: " + testOrderId);
        }

        List<Comment> comments = commentRepository.findByTestOrderIdAndStatus(testOrderId, CommentStatus.ACTIVE);
        return comments.stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByTestResultId(Long testResultId) {
        log.info("Fetching comments for testResultId: {}", testResultId);

        if (!testResultRepository.existsById(testResultId)) {
            throw new ResourceNotFoundException("Test result not found with id: " + testResultId);
        }

        List<Comment> comments = commentRepository.findByTestResultIdAndStatus(testResultId, CommentStatus.ACTIVE);
        return comments.stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void validateCommentRequest(CommentRequestDTO requestDTO) {
        if (requestDTO.getTestOrderId() != null) {
            testOrderRepository.findById(requestDTO.getTestOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Test order not found with id: " + requestDTO.getTestOrderId()));
        }

        if (requestDTO.getTestResultId() != null) {
            testResultRepository.findById(requestDTO.getTestResultId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Test result not found with id: " + requestDTO.getTestResultId()));
        }
    }
}