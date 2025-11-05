package sum25.group03.testorderservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.testorderservice.dtos.request.CommentRequestDTO;
import sum25.group03.testorderservice.dtos.response.CommentResponseDTO;
import sum25.group03.testorderservice.services.interfaces.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    //  Add Comment
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CommentRequestDTO requestDTO) {
        log.info("API - Create Comment");
        CommentResponseDTO response = commentService.createComment(requestDTO);
        return ResponseEntity.ok(response);
    }

    //  Modify Comment
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDTO requestDTO) {
        log.info("API - Update Comment id: {}", id);
        CommentResponseDTO response = commentService.updateComment(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    //  Delete Comment
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //delete return 204 no content
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("API - Delete Comment id: {}", id);
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // Get Comment by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long id) {
        log.info("API - Get Comment by id: {}", id);
        CommentResponseDTO response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }

    // Get Comments by TestOrderId
    @GetMapping("/test-order/{testOrderId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTestOrder(@PathVariable Long testOrderId) {
        log.info("API - Get Comments by TestOrder id: {}", testOrderId);
        List<CommentResponseDTO> comments = commentService.getCommentsByTestOrderId(testOrderId);
        return ResponseEntity.ok(comments);
    }

    // Get Comments by TestResultId
    @GetMapping("/test-result/{testResultId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTestResult(@PathVariable Long testResultId) {
        log.info("API - Get Comments by TestResult id: {}", testResultId);
        List<CommentResponseDTO> comments = commentService.getCommentsByTestResultId(testResultId);
        return ResponseEntity.ok(comments);
    }
}
