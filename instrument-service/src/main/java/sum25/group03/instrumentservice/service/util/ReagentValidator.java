package sum25.group03.instrumentservice.service.util;

import lombok.extern.slf4j.Slf4j;
import sum25.group03.instrumentservice.client.response.ReagentResponse;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.model.InstalledReagent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class ReagentValidator {

    public static boolean validateReagentVolume(
            List<InstalledReagent> installedReagents,
            List<ReagentResponse> reagentResponses
    ) {

        // ---------------------------------------------------------
        // 1. If no installed reagents → fail
        // ---------------------------------------------------------
        if (installedReagents == null || installedReagents.isEmpty()) {
            System.out.println("[FAIL] No installed reagents found.");
            return false;
        }

        // ---------------------------------------------------------
        for(InstalledReagent installed : installedReagents){
            String name = installed.getReagentName();
            Double current = installed.getCurrentVolume();
            Double min = installed.getUsageMin();
            Double max = installed.getUsageMax();
            if (current == null) {
                System.out.printf("[FAIL] Reagent '%s' current volume is null.%n", name);
                return false;
            }

            if(installed.getStatus() == InstalledReagentStatus.EXPIRED || installed.getStatus() == InstalledReagentStatus.REMOVED){
                System.out.println("[FAIL] Reagent is expired or removed." + name);
                return false;
            }

            if (min == null || max == null) {
                System.out.printf("[FAIL] Reagent '%s' has invalid usage bounds: min=%s max=%s%n", name, min, max);
                return false;
            }
            if (current < min || current > max) {
                System.out.printf(
                        "[FAIL] Reagent '%s' volume out of allowed range. Current=%.2f, Allowed=[%.2f..%.2f]%n",
                        name, current, min, max);
                return false;
            }
        }
        // All checks passed
        System.out.println("[SUCCESS] All reagents valid.");
        return true;
    }

}
