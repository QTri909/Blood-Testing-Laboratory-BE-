package sum25.group03.instrumentservice.audit.util;

import sum25.group03.instrumentservice.audit.model.AuditLog;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ObjectChangeDetector {

    public static List<AuditLog.FieldChange> detectChanges(Object oldObject, Object newObject) {
        List<AuditLog.FieldChange> changes = new ArrayList<>();

        if (oldObject == null || newObject == null) {
            return changes;
        }

        if (!oldObject.getClass().equals(newObject.getClass())) {
            log.warn("Objects are not of the same type, cannot detect changes");
            return changes;
        }

        Field[] fields = oldObject.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);

                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);

                if (Objects.equals(oldValue, newValue)) {
                    continue;
                }

                if (java.lang.reflect.Modifier.isTransient(field.getModifiers()) ||
                        java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                String fieldName = field.getName();
                String oldValueStr = oldValue != null ? oldValue.toString() : "null";
                String newValueStr = newValue != null ? newValue.toString() : "null";

                changes.add(new AuditLog.FieldChange(fieldName, oldValueStr, newValueStr));
                log.debug("Detected change in field '{}': {} -> {}", fieldName, oldValueStr, newValueStr);

            } catch (IllegalAccessException e) {
                log.warn("Cannot access field {}: {}", field.getName(), e.getMessage());
            }
        }

        return changes;
    }


    public static AuditLog.FieldChange createFieldChange(String fieldName, Object oldValue, Object newValue) {
        String oldValueStr = oldValue != null ? oldValue.toString() : "null";
        String newValueStr = newValue != null ? newValue.toString() : "null";
        return new AuditLog.FieldChange(fieldName, oldValueStr, newValueStr);
    }
}
