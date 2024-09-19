package Admin;

public class JobCriteria {
    String designation;
    String[] primaryLocations;
    String[] secondaryLocations;
    int minAge;
    int maxAge;

    public JobCriteria(String designation, String[] primaryLocations, String[] secondaryLocations, int minAge, int maxAge) {
        this.designation = designation.toLowerCase(); // Convert to lowercase
        this.primaryLocations = primaryLocations;
        this.secondaryLocations = secondaryLocations;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public boolean isEligibleApplicant(String designation, int age) {
        return this.designation.equals(designation.toLowerCase()) && age >= minAge && age <= maxAge;
    }
}
