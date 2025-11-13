package sum25.group03.instrumentservice.service.util;

import lombok.extern.slf4j.Slf4j;
import sum25.group03.instrumentservice.client.response.ReagentResponse;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.model.InstalledReagent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class ReagentValidator {
    public static boolean validateReagentVolume(List<InstalledReagent> installedReagents, List<ReagentResponse> reagentResponses) {
        Map<String, Double> REAGENT_CONSUMPTION = new HashMap<>();

        for (ReagentResponse reagentResponse : reagentResponses) {
            REAGENT_CONSUMPTION.put(reagentResponse.getReagentName(), reagentResponse.getUsageMax());
        }

        if (installedReagents == null || installedReagents.isEmpty()) {
            return false;
        }
        for(InstalledReagent   installedReagent: installedReagents){
            if(installedReagent.getStatus().equals(InstalledReagentStatus.EXPIRED)||installedReagent.getStatus().equals(InstalledReagentStatus.QUARANTINED)||installedReagent.getStatus().equals(InstalledReagentStatus.EMPTY)){
                return false;
            }
        }

        Map<String, InstalledReagent> reagentMap = new HashMap<>();
        for (InstalledReagent reagent : installedReagents) {
            reagentMap.put(reagent.getReagentName(), reagent);
        }
        for (Map.Entry<String, Double> entry : REAGENT_CONSUMPTION.entrySet()) {
            String reagentName = entry.getKey();
            Double requiredVolume = entry.getValue();
            InstalledReagent reagent = reagentMap.get(reagentName);
            if (reagent == null || reagent.getCurrentVolume() < requiredVolume || reagent.getStatus().equals(InstalledReagentStatus.QUARANTINED)|| reagent.getStatus().equals(InstalledReagentStatus.EMPTY)|| reagent.getStatus().equals(InstalledReagentStatus.EXPIRED)) {

                return false;
            }
        }

        return true;
    }
}
