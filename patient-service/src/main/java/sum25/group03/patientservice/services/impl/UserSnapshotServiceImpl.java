package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.dtos.request.UserSnapshotRequest;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.entities.UserSnapshotEntity;
import sum25.group03.patientservice.feign.IAMFeignClient;
import sum25.group03.patientservice.feign.dtos.UserDTO;
import sum25.group03.patientservice.feign.dtos.UserFeignResponseWrapper;
import sum25.group03.patientservice.feign.dtos.UserFilterUpdate;
import sum25.group03.patientservice.mapper.UserSnapshotMapper;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.services.interfaces.UserSnapshotService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserSnapshotServiceImpl implements UserSnapshotService {

    private final UserSnapshotRepository repository;
    private final UserSnapshotMapper mapper;
    private final IAMFeignClient userFeignClient;
    private final JdbcTemplate jdbcTemplate;

    // sync user information from IAM service
    @Override
    @Transactional
    public void syncUserSnapshots() {
        UserFeignResponseWrapper usersWrapper = userFeignClient.fetchUsersInfo();

        // debug:
        log.info("usersWrapper={}", usersWrapper);

        if (usersWrapper == null)
            throw new RuntimeException("usersWrapper is null");

        List<UserDTO> users = usersWrapper.getContent();
        List<UserFilterUpdate> updateInfos = mapper.toUpdateInfoDTOs(users);

        // debug:
        updateInfos.stream().forEach(System.out::println);
    }

    @Override
    public UserSnapshotResponse create(UserSnapshotRequest request) {
        UserSnapshotEntity entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public UserSnapshotResponse update(Long id, UserSnapshotRequest request) {
        UserSnapshotEntity existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User snapshot not found"));
        existing.setExternalUserId(request.getExternalUserId());
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserSnapshotResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User snapshot not found"));
    }

    @Override
    public UserSnapshotResponse getByExternalUserId(Long externalUserId) {
        return repository.findByExternalUserId(externalUserId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User snapshot not found"));
    }

    @Override
    public List<UserSnapshotResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
