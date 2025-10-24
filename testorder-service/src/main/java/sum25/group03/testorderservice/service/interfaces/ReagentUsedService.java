package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.dto.request.ReagentUsedRequestDTO;
import sum25.group03.testorderservice.dto.response.ReagentUsedResponseDTO;

import java.util.List;

public interface ReagentUsedService {
    ReagentUsedResponseDTO createReagentUsed(ReagentUsedRequestDTO requestDTO);
    ReagentUsedResponseDTO updateReagentUsed(Long id, ReagentUsedRequestDTO requestDTO);
    void deleteReagentUsed(Long id);
    ReagentUsedResponseDTO getReagentUsedById(Long id);
    List<ReagentUsedResponseDTO> getReagentUsedByReagentId(Long reagentId);
}
