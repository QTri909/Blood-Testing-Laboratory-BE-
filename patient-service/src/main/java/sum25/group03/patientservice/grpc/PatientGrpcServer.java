package sum25.group03.patientservice.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.dao.DataAccessException;
import sum25.group03.patientservice.dtos.request.GrpcMappingPatientAndCreatorIdRequest;
import sum25.group03.patientservice.dtos.response.GrpcMappingPatientAndCreatorIdResponse;
import sum25.group03.patientservice.mapper.GrpcUserSnapshotMapper;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.entities.UserSnapshotEntity;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;
import sum25.group03.patientservice.services.interfaces.UserSnapshotService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class PatientGrpcServer extends PatientServiceGrpc.PatientServiceImplBase {

    private final UserSnapshotRepository userSnapshotRepository;
    private final MedicalRecordService medicalRecordService;
    private final UserSnapshotService userSnapshotService;
    private final GrpcUserSnapshotMapper grpcUserSnapshotMapper;

    @Override
    public void getPatientById(GetPatientByIdRequest request,
                               StreamObserver<GetPatientByIdResponse> responseObserver) {
        UserSnapshotEntity user = userSnapshotRepository
                .findByExternalUserId(request.getExternalUserId())
                .orElse(null);

        if (user == null) {
            responseObserver.onError(new RuntimeException("Patient not found"));
            return;
        }

        GetPatientByIdResponse response = GetPatientByIdResponse.newBuilder()
                .setId(user.getId())
                .setFullName(user.getFullName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "")
                .setGender(user.getGender() != null ? user.getGender() : "")
                .setEmail(user.getEmail())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void autoCreateMedicalRecord(
            AutoCreateMedicalRecordRequest request,
            StreamObserver<CreatedMedicalRecordResponse> createdRecordInfo
    ) {
        try {
            Long createdMedicalRecordId =
                    medicalRecordService.autoCreateNewMedicalRecordByTestOrder(
                            request.getCreatedBy(),
                            request.getPatientId()
                    );

            CreatedMedicalRecordResponse response =
                    CreatedMedicalRecordResponse.newBuilder()
                            .setRecordId(createdMedicalRecordId)
                            .build();

            createdRecordInfo.onNext(response);
            createdRecordInfo.onCompleted();

        } catch (IllegalArgumentException e) {
            createdRecordInfo.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            createdRecordInfo.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("Medical record already exists")
                            .withCause(e)
                            .asRuntimeException()
            );

        } catch (jakarta.persistence.EntityNotFoundException e) {
            createdRecordInfo.onError(
                    Status.NOT_FOUND
                            .withDescription("Patient not found")
                            .withCause(e)
                            .asRuntimeException()
            );

        } catch (Exception e) {
            log.error("Unhandled gRPC error in autoCreateMedicalRecord", e);

            createdRecordInfo.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error while creating medical record")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }


    @Override
    public void assignPatientIdToMedicalRecord(
            AssignPatientIdToMedicalRecordRequest request,
            StreamObserver<EmptyMsg> responseObserver
    ) {
        medicalRecordService.assignPatientIdToMedicalRecord(
                request.getMedicalRecordId(),
                request.getPatientId()
        );

        responseObserver.onNext(EmptyMsg.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void mappingPatientIdAndCreatorIdToTheirName(
            MappingPatientIdAndCreatorIdToTheirNameRequest request,
            StreamObserver<MappingPatientIdAndCreatorIdToTheirNameResponse> responseObserver
    ) {

        GrpcMappingPatientAndCreatorIdRequest javaDtoRequest = grpcUserSnapshotMapper.fromGrpcMappingRequest(request);
        var mapping = userSnapshotService.getGrpcMappingPatientAndCreatorName(javaDtoRequest);

        MappingPatientIdAndCreatorIdToTheirNameResponse grpcResponse = grpcUserSnapshotMapper.toGrpcMappingResponse(mapping);
        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserInformationByExternalIds(
            UserInformationByExternalIdsRequest request,
            StreamObserver<UserInformationByExternalIdsResponse> responseObserver
    ) {
        try {
            // 1. Validate input
            List<Long> externalUserIds = request.getExternalUserIdsList();

            // 2. Fetch from database
            List<UserSnapshotEntity> userSnapshots =
                    userSnapshotService.getUserInformationByExternalUserIds(externalUserIds);

            // 3. Map to gRPC response
            UserInformationByExternalIdsResponse response =
                    grpcUserSnapshotMapper.mapToGrpcUserInformationResponse(userSnapshots);

            // 4. Return result
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException grpcEx) {
            //  If a downstream gRPC call already threw a Status error
            responseObserver.onError(grpcEx);

        } catch (IllegalArgumentException ex) {
            //  For validation / bad input cases
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(ex.getMessage())
                            .withCause(ex)
                            .asRuntimeException()
            );

        } catch (DataAccessException ex) {
            //  For database failures (Spring)
            responseObserver.onError(
                    Status.UNAVAILABLE
                            .withDescription("Database temporarily unavailable")
                            .withCause(ex)
                            .asRuntimeException()
            );

        } catch (Exception ex) {
            //  Absolute safety net for all unexpected errors
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Unexpected server error while fetching user information")
                            .withCause(ex)
                            .asRuntimeException()
            );
        }
    }

}
