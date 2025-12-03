package sum25.group03.warehouseservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;
import sum25.group03.warehouseservice.service.reagent.ReagentService;

@Component
@RequiredArgsConstructor
public class ReagentInventoryScheduler {
    private final ReagentService reagentService;

    @Scheduled(cron = "0 0 0 * * ?")  // mỗi ngày 00:00
    public void checkExpiryStatus() {
        reagentService.updateInventoryStatusesScheduler();
    }
}
