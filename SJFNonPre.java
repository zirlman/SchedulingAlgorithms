package org.unibl.etf.SchedulingAlgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Thread.sleep;

public class SJFNonPre {
    static private class Job {
        private int arrivalTime;
        private int remainingTime;
        private int waitingTime;
        private int id;
        static int idMaker;

        Job(int arrivalTime, int remainingTime) {
            this.arrivalTime = arrivalTime;
            this.remainingTime = remainingTime;
            id = ++idMaker;
            waitingTime = 0;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        int getRemainingTime() {
            return remainingTime;
        }

        void decreaseRemainigTime() {
            --remainingTime;
        }

        void increaseWaitingTime() {
            ++waitingTime;
        }

        int getId() {
            return id;
        }

        int getWaitingTime() {
            return waitingTime;
        }

        void updateWaitingTime(int passedTime) {
            if (arrivalTime < passedTime)
                ++waitingTime;
        }

        @Override
        public String toString() {
            return "Job " + id + " remaining time: " + remainingTime;
        }
    }

    // Procesi dolaze u istom vremenskom trenutku
    private static TreeSet<Job> jobQueue1 = new TreeSet<>(Comparator.comparingInt(Job::getRemainingTime).thenComparing(Job::getId));
    // Procesi dolaze u razlicitim vremenskim trenucima
    private static TreeSet<Job> jobQueue2 = new TreeSet<>(Comparator.comparingInt(Job::getArrivalTime).thenComparing(Job::getRemainingTime).thenComparing(Job::getId));
    private static int passedTime;

    // Primjer kada svi procesi dolaze u istom trenutku, bez preotimanja (nepotrebna metoda, ali ono, spajanje ugodnog sa beskorisnim)
    private static void loadJobs1(String[] jobs) {
        for (String job : jobs)
            jobQueue1.add(new Job(0, Integer.valueOf(job)));
        while (!jobQueue1.isEmpty()) {
            Job running = jobQueue1.pollFirst();
            while (running.getRemainingTime() > 0) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                jobQueue1.forEach(Job::increaseWaitingTime);
                running.decreaseRemainigTime();
                System.out.println(running);
            }
            System.out.println("Job " + running.getId() + " done, total waiting time: " + running.getWaitingTime());
        }
    }

    // Primjer kada procesi dolaze u razlicitim vremenskim trenucima, bez preotimanja
    private static void loadJobs2(String[] arrivalTime, String[] jobs) {
        ArrayList<Job> availableJobs = new ArrayList<>();
        for (int i = 0; i < arrivalTime.length; i++)
            jobQueue2.add(new Job(Integer.valueOf(arrivalTime[i]), Integer.valueOf(jobs[i])));

        while (!jobQueue2.isEmpty()) {
            // CPU ceka na dolazak procesa
            while (passedTime < jobQueue2.first().getArrivalTime()) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++passedTime;
            }
            // Svi procesi koji su aktivirani se ubacuju u ArrayList availableJobs
            while (!jobQueue2.isEmpty() && passedTime >= jobQueue2.first().getArrivalTime())
                availableJobs.add(jobQueue2.pollFirst());
            // Sortiranje ArrayList-e po vremenu dolaska
            availableJobs.sort(Comparator.comparingInt(Job::getRemainingTime));
            while (!availableJobs.isEmpty()) {
                Job running = availableJobs.remove(0);
                while (running.getRemainingTime() != 0) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ++passedTime;
                    jobQueue2.forEach(c -> c.updateWaitingTime(passedTime));
                    availableJobs.forEach(c -> c.updateWaitingTime(passedTime));
                    running.decreaseRemainigTime();
                    System.out.println(running);
                }
                System.out.println("Job " + running.getId() + " done, total waiting time: " + running.getWaitingTime());
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter time for each job (1,2,3,4,5,...): ");
        String input2 = scanner.next();
        String[] jobs = input2.split(",");
        //loadJobs1(jobs);
        // ------------- Primjer2 -------------
        System.out.print("Enter arrival time for each job (1,2,3,4,5,...): ");
        String input1 = scanner.next();
        String[] arrivalTime = input1.split(",");
        if (arrivalTime.length != jobs.length) {
            System.out.println("Invalid input arrivalTime.length != jobs.length!");

        } else
            loadJobs2(arrivalTime, jobs);
    }
}
