import java.util.Objects;

/**
 * FitTrack - Gym Membership and Access Management System
 * Represents a fitness trainer in the gym system.
 * 
 * Collaborative Project: Varun Pandey & Sujan Khadka (CP-086)
 */
public class Trainer {
    private String trainerId;
    private String name;
    private String specialization;
    private String contact;

    public Trainer(String trainerId, String name, String specialization, String contact) {
        this.trainerId = trainerId != null ? trainerId.trim() : "";
        this.name = name != null ? name.trim() : "";
        this.specialization = specialization != null ? specialization.trim() : "General Fitness";
        this.contact = contact != null ? contact.trim() : "";
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId != null ? trainerId.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization != null ? specialization.trim() : "";
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact != null ? contact.trim() : "";
    }

    public String toCsv() {
        return String.format("%s,%s,%s,%s",
                escapeCsv(trainerId),
                escapeCsv(name),
                escapeCsv(specialization),
                escapeCsv(contact));
    }

    public static Trainer fromCsv(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 4) {
            return null;
        }
        return new Trainer(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace(",", ";");
    }

    @Override
    public String toString() {
        return String.format("[%s] %-18s | Specialization: %-22s | Contact: %s",
                trainerId, name, specialization, contact);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(trainerId, trainer.trainerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainerId);
    }
}
