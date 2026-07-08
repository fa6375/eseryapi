package com.eseryapi.construction.model;

public enum ProjectStatus {
    PLANNED("Planlaşdırılıb"),
    ONGOING("Davam edir"),
    COMPLETED("Tamamlanıb");

    private final String label;

    ProjectStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
