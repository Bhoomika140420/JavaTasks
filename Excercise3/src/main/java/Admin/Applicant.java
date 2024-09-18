package Admin;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Applicant implements Runnable {
    private static final Random random = new Random();
    private static final AtomicInteger acceptedCount = new AtomicInteger(0);
    private static final AtomicInteger rejectedCount = new AtomicInteger(0);
    private static final AtomicInteger totalApplicants = new AtomicInteger(0);
    private static final Object vacancyLock = new Object();

    private List<JobCriteria> jobCriteriaList;

    public Applicant(List<JobCriteria> jobCriteriaList) {
        this.jobCriteriaList = jobCriteriaList;
    }

    @Override
    public void run() {
        int age = random.nextInt(60) + 1;  // Generate random age between 1 and 65
        String designation = random.nextBoolean() ? "Developer" : "Tester";  // Random designation
        
        // Increase total applicants count
        totalApplicants.incrementAndGet();
        boolean processed = false; // flag to check either its accepted / rejected 
        boolean validApplicant = false;

        synchronized (vacancyLock) {
            if (acceptedCount.get() >= Constants.TOTAL_VACANCIES) {
                // If all positions are filled, reject the applicant
                System.out.println(Thread.currentThread().getName() + ": Rejected due to no vacancies for " + designation + " (age: " + age + ")");
                rejectedCount.incrementAndGet();
                processed = true;
            } else {
                // Check if applicant meets any job criteria
                for (JobCriteria criteria : jobCriteriaList) {
                    if (criteria.isEligibleApplicant(designation, age)) {
                        validApplicant = true;
                        if (acceptedCount.get() < Constants.TOTAL_VACANCIES) {
                            System.out.println(Thread.currentThread().getName() + ": Accepted for " + designation + " (age: " + age + ")");
                            acceptedCount.incrementAndGet();
                            processed = true;
                            break;
                        }
                    } else if (criteria.designation.equals(designation)) {
                        // Rejected due to age mismatch
                        System.out.println(Thread.currentThread().getName() + ": Rejected due to age criteria mismatch for " + designation + " (age: " + age + ")");
                        rejectedCount.incrementAndGet();
                        processed = true;
                        break;
                    }
                }

                // If the applicant doesn't meet any criteria, reject them
                if (!validApplicant && !processed) {
                    System.out.println(Thread.currentThread().getName() + ": Rejected for " + designation + " - Invalid designation or criteria mismatch (age: " + age + ")");
                    rejectedCount.incrementAndGet();
                }
            }

            // Output remaining vacancies
            int remainingVacancies = Math.max(Constants.TOTAL_VACANCIES - acceptedCount.get(), 0);
            System.out.println("Remaining vacancies: " + remainingVacancies);
        }
    }

    public static int getAcceptedCount() {
        return acceptedCount.get();
    }

    public static int getRejectedCount() {
        return rejectedCount.get();
    }

    public static int getTotalApplicants() {
        return totalApplicants.get();
    }
}


   

