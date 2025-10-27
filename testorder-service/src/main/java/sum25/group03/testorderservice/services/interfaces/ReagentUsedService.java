package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.dtos.request.ReagentUsedRequestDTO;
import sum25.group03.testorderservice.dtos.response.ReagentUsedResponseDTO;

import java.util.List;

public interface ReagentUsedService {
    ReagentUsedResponseDTO createReagentUsed(ReagentUsedRequestDTO requestDTO);
    ReagentUsedResponseDTO updateReagentUsed(Long id, ReagentUsedRequestDTO requestDTO);
    void deleteReagentUsed(Long id);
    ReagentUsedResponseDTO getReagentUsedById(Long id);
    List<ReagentUsedResponseDTO> getReagentUsedByReagentId(Long reagentId);
}
