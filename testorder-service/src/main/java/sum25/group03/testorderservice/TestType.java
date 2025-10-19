package sum25.group03.testorderservice;

public enum TestType {
    // ---------------- Hematology ----------------
    CBC,                // Complete Blood Count
    HEMOGLOBIN,         // Hemoglobin level
    HEMATOCRIT,         // Hematocrit percentage
    WBC_DIFF,           // White Blood Cell Differential
    PLATELET_COUNT,     // Platelet count

    // ---------------- Clinical Chemistry / Biochemistry ----------------
    GLUCOSE,            // Blood glucose
    CHOLESTEROL,        // Total cholesterol
    ALT,                // Alanine transaminase (liver)
    AST,                // Aspartate transaminase (liver)
    CREATININE,         // Kidney function
    BUN,                // Blood urea nitrogen
    NA,                 // Sodium (Natri)
    K,                  // Potassium (Kali)
    CL,                 // Chloride (Clo)

    // ---------------- Immunology / Serology ----------------
    HIV_ANTIBODY,       // HIV antibody test
    HBSAG,              // Hepatitis B surface antigen
    ANA,                // Antinuclear antibody
    CRP,                // C-reactive protein
    RHEUMATOID_FACTOR,  // Rheumatoid factor

    // ---------------- Microbiology ----------------
    BACTERIAL_CULTURE,          // General bacterial culture
    GRAM_STAIN,                 // Gram stain test
    ANTIBIOTIC_SUSCEPTIBILITY,  // Antibiotic susceptibility testing

    // ---------------- Coagulation ----------------
    PT,                 // Prothrombin Time
    INR,                // International Normalized Ratio
    D_DIMER,            // D-dimer test

    // ---------------- Urinalysis / Body Fluids ----------------
    URINE_ROUTINE,      // Routine urinalysis
    URINE_MICROSCOPY,   // Urine microscopy
    CSF_ANALYSIS,       // Cerebrospinal fluid analysis

    // ---------------- Blood Bank / Transfusion ----------------
    BLOOD_TYPING,       // ABO & Rh typing
    CROSSMATCHING,      // Blood crossmatching
    ANTIBODY_SCREENING, // Screening for irregular antibodies
}

