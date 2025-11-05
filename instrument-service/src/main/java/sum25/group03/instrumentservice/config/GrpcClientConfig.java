package sum25.group03.instrumentservice.config;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sum25.group03.warehouse.grpc.WarehouseServiceGrpc;
import sum25.group03.testorder.grpc.TestOrderServiceGrpc;


@Configuration
public class GrpcClientConfig {

    @Value("${grpc.warehouse.host:localhost}")
    private String warehouseHost;

    @Value("${grpc.warehouse.port:9002}")
    private int warehousePort;

    @Value("${grpc.testorder.host:localhost}")
    private String testOrderHost;

    @Value("${grpc.testorder.port:9001}")
    private int testOrderPort;

    @Bean
    public ManagedChannel warehouseChannel() {
        return ManagedChannelBuilder
                .forAddress(warehouseHost, warehousePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel testOrderChannel() {
        return ManagedChannelBuilder
                .forAddress(testOrderHost, testOrderPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public WarehouseServiceGrpc.WarehouseServiceBlockingStub warehouseServiceStub(ManagedChannel warehouseChannel) {
        return WarehouseServiceGrpc.newBlockingStub(warehouseChannel);
    }

    @Bean
    public TestOrderServiceGrpc.TestOrderServiceBlockingStub testOrderServiceStub(ManagedChannel testOrderChannel) {
        return TestOrderServiceGrpc.newBlockingStub(testOrderChannel);
    }
}
