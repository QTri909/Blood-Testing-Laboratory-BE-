package sum25.group03.patientservice.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.entities.UserSnapshotEntity;

@GrpcService
@RequiredArgsConstructor
public class PatientGrpcServer extends PatientServiceGrpc.PatientServiceImplBase {

    private final UserSnapshotRepository userSnapshotRepository;

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
                .setEmail(user.getEmail())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
