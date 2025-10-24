package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.dtos.request.CommentRequestDTO;
import sum25.group03.testorderservice.dtos.response.CommentResponseDTO;

import java.util.List;

public interface CommentService {
    CommentResponseDTO createComment(CommentRequestDTO requestDTO);
    CommentResponseDTO updateComment(Long id, CommentRequestDTO requestDTO);
    void deleteComment(Long id);
    CommentResponseDTO getCommentById(Long id);
    List<CommentResponseDTO> getCommentsByTestOrderId(Long testOrderId);
    List<CommentResponseDTO> getCommentsByTestResultId(Long testResultId);

}
