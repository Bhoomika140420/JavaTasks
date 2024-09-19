package com.ixcd.JobVacancies.Jobs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

class JobCriteria {
    String designation;
    String[] primaryLocations;
    String[] secondaryLocations;
    int minAge;
    int maxAge;
    int noOfVacancy=10;

    JobCriteria(String designation, String[] primaryLocations, String[] secondaryLocations, int minAge, int maxAge) {
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

class Applicant implements Runnable {
    private static final Random random = new Random();
    private static final AtomicInteger filledPositions = new AtomicInteger(0);
    private static int totalPositions;
    
    private List<JobCriteria> jobCriteriaList;

    Applicant(List<JobCriteria> jobCriteriaList, int totalPositions) {
        this.jobCriteriaList = jobCriteriaList;
        Applicant.totalPositions = totalPositions;
    }

    @Override
    public void run() {
        if (filledPositions.get() >= totalPositions) {
            return;
        }

        // Randomly assign age and designation
        String[] designations = {"Developer", "Tester"};
        String designation = designations[random.nextInt(designations.length)].toLowerCase(); // Convert to lowercase
        int age = random.nextInt(70);  // Generate random age between 0 and 69

        // Check if applicant is valid for any criteria
        for (JobCriteria criteria : jobCriteriaList) {
            if (criteria.isEligibleApplicant(designation, age)) {
                if (filledPositions.get() < totalPositions) {
                    System.out.println(Thread.currentThread().getName() + ": Applied for " + designation + " (age: " + age + ")");
                    filledPositions.incrementAndGet();
                }
                return;
            }
        }

        // If no valid job found
        System.out.println(Thread.currentThread().getName() + ": Not eligible for any job (age: " + age + ")");
    }

    public static boolean applyUser(String designation, int age, List<JobCriteria> jobCriteriaList) {
        if (filledPositions.get() >= totalPositions) {
            System.out.println("Sorry, vacancies are closed for the application.");
            return false;
        }

        String designationLower = designation.toLowerCase(); // Convert user input to lowercase

        for (JobCriteria criteria : jobCriteriaList) {
            if (criteria.isEligibleApplicant(designationLower, age)) {
                if (filledPositions.get() < totalPositions) {
                    filledPositions.incrementAndGet();
                    System.out.println("Applied successfully for " + designation + " (age: " + age + ")");
                    return true;
                } else {
                    System.out.println("Sorry, vacancies are closed for the application.");
                    return false;
                }
            } else if (criteria.designation.equals(designationLower)) {
                // Age criteria not met
                System.out.println("Sorry, your age does not meet the criteria for the " + designation + " role.");
                return false;
            }
        }

        System.out.println("Couldn't apply, user is not eligible.");
        return false;
    }
}

public class JobVacanciesWithUserInput {
    public static void main(String[] args) {
        List<JobCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new JobCriteria("Developer", new String[]{"Delhi", "New York"}, new String[]{"New Jersey", "Mumbai"}, 22, 60));
        criteriaList.add(new JobCriteria("Tester", new String[]{"London", "Sydney"}, new String[]{"Chennai", "Hyderabad"}, 18, 40));

       // Set the number of job vacancies 
       int totalVacancies = 15;
       System.out.println("Total vacancies available: " + totalVacancies);

        // Create number of threads required
        int numApplicants = 40;
        Thread[] applicants = new Thread[numApplicants];

        for (int i = 0; i < numApplicants; i++) {
            applicants[i] = new Thread(new Applicant(criteriaList, totalVacancies), "Applicant-" + (i + 1));
            applicants[i].start();
        }

        // Join threads to make sure main thread waits for all to finish
        for (Thread applicant : applicants) {
            try {
                applicant.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Now take user input after some positions are filled
        Scanner scanner = new Scanner(System.in);

        System.out.println("Now it's time for user to apply!");
        System.out.print("Enter your designation (Developer/Tester): ");
        String userDesignation = scanner.nextLine().trim();

        System.out.print("Enter your age: ");
        int userAge = scanner.nextInt();

        // Check if the user can apply
        boolean result = Applicant.applyUser(userDesignation, userAge, criteriaList);

        if (!result) {
            System.out.println("User could not apply.");
        }

        System.out.println("All applicants processed. Program finished.");
        scanner.close();
    }
}
