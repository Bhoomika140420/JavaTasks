package Admin;

import java.util.ArrayList;
import java.util.List;

public class JobVacanciesAdmin {
    public static void main(String[] args) {
        List<JobCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new JobCriteria("Developer", new String[]{"Delhi", "New York"}, new String[]{"New Jersey", "Mumbai"}, 22, 60));
        criteriaList.add(new JobCriteria("Tester", new String[]{"London", "Sydney"}, new String[]{"Chennai", "Hyderabad"}, 18, 40));

        // Create number of threads required
        int numApplicants = 80;
        Thread[] applicants = new Thread[numApplicants];

        System.out.println("Total vacancies available: " + Constants.TOTAL_VACANCIES);
        for (int i = 0; i < numApplicants; i++) {
            applicants[i] = new Thread(new Applicant(criteriaList), "Applicant-" + (i + 1));
            applicants[i].start();
        }

        // Join threads to make sure the main thread waits for all to finish
        for (Thread applicant : applicants) {
            try {
                applicant.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Show final counts
        int totalAccepted = Applicant.getAcceptedCount();
        int totalRejected = Applicant.getRejectedCount();
        int totalReceived = Applicant.getTotalApplicants();

        System.out.println("Total accepted applicants: " + totalAccepted);
        System.out.println("Total rejected applicants: " + totalRejected);
        System.out.println("Total applicants received: " + totalReceived);

        System.out.println("All applicants processed. Program finished.");
    }
}
