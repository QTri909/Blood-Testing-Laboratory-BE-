package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entity.Comment;
import sum25.group03.testorderservice.enums.CommentStatus;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByTestOrderIdAndStatus(Long testOrderId, CommentStatus status);

    List<Comment> findByTestResultIdAndStatus(Long testResultId, CommentStatus status);
}
