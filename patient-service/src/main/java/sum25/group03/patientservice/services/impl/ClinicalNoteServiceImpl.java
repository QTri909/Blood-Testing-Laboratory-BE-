package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.dtos.request.ClinicalNoteRequest;
import sum25.group03.patientservice.dtos.response.ClinicalNoteResponse;
import sum25.group03.patientservice.entities.ClinicalNoteEntity;
import sum25.group03.patientservice.mapper.ClinicalNoteMapper;
import sum25.group03.patientservice.repositories.ClinicalNoteRepository;
import sum25.group03.patientservice.services.interfaces.ClinicalNoteService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClinicalNoteServiceImpl implements ClinicalNoteService {

    private final ClinicalNoteRepository repository;
    private final ClinicalNoteMapper mapper;

    @Override
    public ClinicalNoteResponse create(ClinicalNoteRequest request) {
        ClinicalNoteEntity entity = mapper.toEntity(request);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public ClinicalNoteResponse update(Long noteId, ClinicalNoteRequest request) {
        ClinicalNoteEntity existing = repository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Clinical note not found"));

        existing.setNote(request.getNote());
        existing.setUpdatedAt(LocalDateTime.now());

        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long noteId) {
        repository.deleteById(noteId);
    }

    @Override
    public ClinicalNoteResponse getById(Long noteId) {
        return repository.findById(noteId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Clinical note not found"));
    }

    @Override
    public List<ClinicalNoteResponse> getByRecordId(Long recordId) {
        return repository.findByRecordId(recordId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
