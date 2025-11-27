package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.request.CommentRequestDTO;
import sum25.group03.testorderservice.dtos.response.CommentResponseDTO;
import sum25.group03.testorderservice.dtos.response.GrpcUserInfo;
import sum25.group03.testorderservice.entities.Comment;
import sum25.group03.testorderservice.enums.ActionCommentsLog;
import sum25.group03.testorderservice.enums.CommentStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.grpc.PatientGrpcClient;
import sum25.group03.testorderservice.mapper.CommentMapper;
import sum25.group03.testorderservice.repositories.CommentRepository;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final  CommentLogServiceImpl commentLogService;
    private final PatientGrpcClient patientGrpcClient;


    @Override
    public CommentResponseDTO createComment(CommentRequestDTO requestDTO) {
        log.info("Creating new comment for testOrderId: {} or testResultId: {}",
                requestDTO.getTestOrderId(), requestDTO.getTestResultId());

        Comment comment = commentMapper.toEntity(requestDTO);

        if (requestDTO.getTestOrderId() != null) {
            comment.setTestOrder(
                    testOrderRepository.findById(requestDTO.getTestOrderId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Test order not found with id: " + requestDTO.getTestOrderId()))
            );
        }

        if (requestDTO.getTestResultId() != null) {
            comment.setTestResult(
                    testResultRepository.findById(requestDTO.getTestResultId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Test result not found with id: " + requestDTO.getTestResultId()))
            );
        }

        comment.setStatus(CommentStatus.ACTIVE);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with id: {}", savedComment.getId());

        //create comment  log
        commentLogService.logAction(
                ActionCommentsLog.CREATE_COMMENT,
                requestDTO.getUserId(),
                savedComment.getId(),
                null,
                savedComment.getCommentText()
        );

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
        String oldCommentText = existingComment.getCommentText();

        commentMapper.updateEntity(requestDTO, existingComment);
        existingComment.setUpdatedAt(LocalDateTime.now());
//        existingComment.setUpdatedBy("SYSTEM");

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment updated successfully with id: {}", updatedComment.getId());
        //update comment log
        commentLogService.logAction(
                ActionCommentsLog.UPDATE_COMMENT,
                requestDTO.getUserId(),
                updatedComment.getId(),
                oldCommentText,
                updatedComment.getCommentText()
        );

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
        //delete comment log
        commentLogService.logAction(
                ActionCommentsLog.DELETE_COMMENT,
                null,
                comment.getId(),
                comment.getCommentText(),
                null
        );
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

    private List<CommentResponseDTO> handleConvertListCommentToDtos(List<Comment> comments) {
        // fill externalId with real user information:
        List<Long> externalUserIds = comments.stream()
                .map(Comment::getUserId).toList();

        Map<Long, GrpcUserInfo> userInfoMap = Map.of();
        try {
            userInfoMap = patientGrpcClient.mappingExternalUserIdsToUserInfos(externalUserIds);
        } catch(Exception e) {
            log.error("Failed to fetch user information via gRPC: {}", e.getMessage());
        }

        List<CommentResponseDTO> responseList = comments.stream()
                .map(commentMapper::toResponseDto)
                .toList();

        for (CommentResponseDTO dto: responseList) {
            GrpcUserInfo userInfo = userInfoMap.get(dto.getUserId());
            dto.setCreatorInfo(userInfo);
        }

        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByTestOrderId(Long testOrderId) {
        log.info("Fetching comments for testOrderId: {}", testOrderId);

        if (!testOrderRepository.existsById(testOrderId)) {
            throw new ResourceNotFoundException("Test order not found with id: " + testOrderId);
        }

        List<Comment> comments = commentRepository.findByTestOrderIdAndStatus(testOrderId, CommentStatus.ACTIVE);
        return handleConvertListCommentToDtos(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByTestResultId(Long testResultId) {
        log.info("Fetching comments for testResultId: {}", testResultId);

        if (!testResultRepository.existsById(testResultId)) {
            throw new ResourceNotFoundException("Test result not found with id: " + testResultId);
        }

        List<Comment> comments = commentRepository.findByTestResultIdAndStatus(testResultId, CommentStatus.ACTIVE);
        return handleConvertListCommentToDtos(comments);
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