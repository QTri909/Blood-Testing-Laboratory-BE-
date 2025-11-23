package sum25.group03.instrumentservice.service.util;

import lombok.extern.slf4j.Slf4j;
import sum25.group03.instrumentservice.client.response.ReagentResponse;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.model.InstalledReagent;
import java.util.HashMap;
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
                    System.out.printf("- %s | Status=%s | Volume=%.2f%n",
                            r.getReagentName(), r.getStatus(), r.getCurrentVolume()));
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
        // 2. If ANY installed reagent is EXPIRED / QUARANTINED / EMPTY → fail
        // ---------------------------------------------------------
        for (InstalledReagent ir : installedReagents) {
            InstalledReagentStatus st = ir.getStatus();

            if (st == InstalledReagentStatus.EXPIRED ||
                    st == InstalledReagentStatus.QUARANTINED ||
                    st == InstalledReagentStatus.EMPTY) {

                System.out.printf(
                        "[FAIL] Reagent '%s' is in invalid status: %s%n",
                        ir.getReagentName(), st);

                return false;
            }
        }


        // ---------------------------------------------------------
        // 3. Build map for fast lookup by reagent name
        // ---------------------------------------------------------
        Map<String, InstalledReagent> installedMap =
                installedReagents.stream()
                        .collect(Collectors.toMap(
                                InstalledReagent::getReagentName,
                                r -> r
                        ));

        // ---------------------------------------------------------
        // 4. For each reagent response → check installed reagent
        // ---------------------------------------------------------
        for (ReagentResponse rr : reagentResponses) {
            String name = rr.getReagentName();
            double required = rr.getUsageMax();

            InstalledReagent installed = installedMap.get(name);

            // Case A: reagent not installed → fail
            if (installed == null) {
                System.out.printf(
                        "[FAIL] Reagent '%s' not installed. Required=%.2f%n",
                        name, required);
                return false;
            }

            // Case B: insufficient volume → fail
            if (installed.getCurrentVolume() < required) {
                System.out.printf(
                        "[FAIL] Reagent '%s' volume too low. Current=%.2f, Required=%.2f%n",
                        name, installed.getCurrentVolume(), required);
                return false;
            }

            // Case C: invalid status (safety check)
            if (installed.getStatus() == InstalledReagentStatus.EXPIRED ||
                    installed.getStatus() == InstalledReagentStatus.QUARANTINED ||
                    installed.getStatus() == InstalledReagentStatus.EMPTY) {

                System.out.printf(
                        "[FAIL] Reagent '%s' has invalid status: %s%n",
                        name, installed.getStatus());
                return false;
            }
        }

        // All checks passed
        System.out.println("[SUCCESS] All reagents valid.");
        return true;
    }

}
