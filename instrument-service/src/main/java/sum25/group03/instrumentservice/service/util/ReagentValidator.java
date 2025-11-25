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

        System.out.println("=== Installed Reagents ===");
        if (installedReagents != null) {
            installedReagents.forEach(r ->
                    System.out.printf("- %s | Status=%s | Volume=%s | Min=%s | Max=%s%n",
                            r.getReagentName(), r.getStatus(), r.getCurrentVolume(), r.getUsageMin(), r.getUsageMax()));
        }

        System.out.println("=== Reagent Responses ===");
        reagentResponses.forEach(r ->
                System.out.printf("- %s | UsageMax=%.2f%n",
                        r.getReagentName(), r.getUsageMax()));


        // ---------------------------------------------------------
        // 1. If no installed reagents → fail
        // ---------------------------------------------------------
        if (installedReagents == null || installedReagents.isEmpty()) {
            System.out.println("[FAIL] No installed reagents found.");
            return false;
        }

        // ---------------------------------------------------------
        // 2. Build map for fast lookup by reagent name
        // ---------------------------------------------------------
        Map<String, InstalledReagent> installedMap =
                installedReagents.stream()
                        .collect(Collectors.toMap(
                                InstalledReagent::getReagentName,
                                r -> r
                        ));

        // ---------------------------------------------------------
        // 3. For each reagent response → check installed reagent
        //    - installed must exist
        //    - status must NOT be EXPIRED, QUARANTINED, REMOVED
        //    - currentVolume must be non-null and within [usageMin, usageMax]
        // ---------------------------------------------------------
        for (ReagentResponse rr : reagentResponses) {
            String name = rr.getReagentName();

            InstalledReagent installed = installedMap.get(name);

            if (installed == null) {
                System.out.printf("[FAIL] Reagent '%s' not installed.%n", name);
                return false;
            }

            InstalledReagentStatus st = installed.getStatus();
            if (st == InstalledReagentStatus.EXPIRED ||
                    st == InstalledReagentStatus.QUARANTINED ||
                    st == InstalledReagentStatus.REMOVED) {

                System.out.printf(
                        "[FAIL] Reagent '%s' is in invalid status: %s%n",
                        name, st);

                return false;
            }

            Double current = installed.getCurrentVolume();
            Double min = installed.getUsageMin();
            Double max = installed.getUsageMax();

            if (current == null) {
                System.out.printf("[FAIL] Reagent '%s' current volume is null.%n", name);
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
