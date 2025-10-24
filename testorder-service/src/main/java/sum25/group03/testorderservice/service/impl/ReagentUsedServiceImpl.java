package sum25.group03.testorderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dto.request.ReagentUsedRequestDTO;
import sum25.group03.testorderservice.dto.response.ReagentUsedResponseDTO;
import sum25.group03.testorderservice.entity.ReagentUsed;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.mapper.ReagentUsedMapper;
import sum25.group03.testorderservice.repositories.ReagentUsedRepository;
import sum25.group03.testorderservice.service.interfaces.ReagentUsedService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReagentUsedServiceImpl implements ReagentUsedService {

    private final ReagentUsedRepository reagentUsedRepository;
    private final ReagentUsedMapper reagentUsedMapper;

    @Override
    public ReagentUsedResponseDTO createReagentUsed(ReagentUsedRequestDTO requestDTO) {
        log.info("Creating new reagent used record for reagentId: {}", requestDTO.getReagentId());

        ReagentUsed reagentUsed = reagentUsedMapper.toEntity(requestDTO);

        ReagentUsed savedReagentUsed = reagentUsedRepository.save(reagentUsed);
        log.info("Reagent used record created successfully with id: {}", savedReagentUsed.getId());

        return reagentUsedMapper.toResponseDto(savedReagentUsed);
    }

    @Override
    public ReagentUsedResponseDTO updateReagentUsed(Long id, ReagentUsedRequestDTO requestDTO) {
        log.info("Updating reagent used record with id: {}", id);

        ReagentUsed existingReagentUsed = reagentUsedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reagent used record not found with id: " + id));

        reagentUsedMapper.updateEntity(requestDTO, existingReagentUsed);

        ReagentUsed updatedReagentUsed = reagentUsedRepository.save(existingReagentUsed);
        log.info("Reagent used record updated successfully with id: {}", updatedReagentUsed.getId());

        return reagentUsedMapper.toResponseDto(updatedReagentUsed);
    }

    @Override
    public void deleteReagentUsed(Long id) {
        log.info("Deleting reagent used record with id: {}", id);

        ReagentUsed reagentUsed = reagentUsedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reagent used record not found with id: " + id));

        reagentUsedRepository.delete(reagentUsed);
        log.info("Reagent used record deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ReagentUsedResponseDTO getReagentUsedById(Long id) {
        log.info("Fetching reagent used record with id: {}", id);

        ReagentUsed reagentUsed = reagentUsedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reagent used record not found with id: " + id));

        return reagentUsedMapper.toResponseDto(reagentUsed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReagentUsedResponseDTO> getReagentUsedByReagentId(Long reagentId) {
        log.info("Fetching reagent used records for reagentId: {}", reagentId);

        List<ReagentUsed> reagentUsedList = reagentUsedRepository.findByReagentId(reagentId);
        return reagentUsedList.stream()
                .map(reagentUsedMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}